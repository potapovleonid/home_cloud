package com.example.server.network;

import com.example.common.constants.SignalBytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;

public class AuthorizeHandler extends ChannelInboundHandlerAdapter {

    private boolean resultAuth = false;
    private final Logger logger;

    private String login;
    private String password;

    public AuthorizeHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (resultAuth) {
            ctx.fireChannelRead(msg);
            return;
        }

        ByteBuf buf = (ByteBuf) msg;

        byte signal = buf.readByte();

        if (signal == SignalBytes.REGISTER_NEW_USER_REQUEST.getSignalByte()) {
            getRequestAndRegisterNewUser(ctx, buf);
        }

        if (signal == SignalBytes.AUTHORIZE_REQUEST.getSignalByte()) {
            getRequestAndAuthorize(ctx, buf);
        }
    }

    private void getRequestAndRegisterNewUser(ChannelHandlerContext ctx, ByteBuf buf) {
        logger.info("Get signal register new user");

        readAndSetLoginAndPassword(buf);
        logger.info(String.format("Get login: %s", login));

        boolean resultRegisterUser = SQLConnection.addUser(login, password);

        sendResponseResultAndClearCredentials(ctx, resultRegisterUser, SignalBytes.REGISTER_USER_SUCCESS, SignalBytes.REGISTER_USER_FAILED);
    }

    private void getRequestAndAuthorize(ChannelHandlerContext ctx, ByteBuf buf) {
        logger.info("Get signal authorize user");

        readAndSetLoginAndPassword(buf);
        logger.info(String.format("Get login: %s", login));

        resultAuth = SQLConnection.authorizeUser(login, password);

        logger.info(String.format("Result auth: %b", resultAuth));

        if (resultAuth) {
            ctx.fireChannelRead(login);
            clearPasswordAndLogin();
        }

        sendResponseResultAndClearCredentials(ctx, resultAuth, SignalBytes.AUTHORIZE_SUCCESS, SignalBytes.AUTHORIZE_FAILED);
    }

    private void readAndSetLoginAndPassword(ByteBuf buf) {
        login = readLoginFromBuff(buf);
        password = readPasswordFromBuff(buf);
    }

    private String readLoginFromBuff(ByteBuf buf) {
        int lengthLogin = buf.readInt();
        byte[] loginBytes = new byte[lengthLogin];
        buf.readBytes(loginBytes);
        return new String(loginBytes, StandardCharsets.UTF_8);
    }

    private String readPasswordFromBuff(ByteBuf buf) {
        int lengthPassword = buf.readInt();
        byte[] passwordBytes = new byte[lengthPassword];
        buf.readBytes(passwordBytes);

        return new String(passwordBytes, StandardCharsets.UTF_8);
    }

    private void sendResponseResultAndClearCredentials(ChannelHandlerContext ctx, boolean result, SignalBytes signalTrue, SignalBytes signalFalse) {
        ByteBuf byteBufResponse = getByteBufWithResponse(result, signalTrue, signalFalse);

        ctx.writeAndFlush(byteBufResponse);
    }

    private ByteBuf getByteBufWithResponse(boolean result, SignalBytes signalTrue, SignalBytes signalFalse) {
        ByteBuf byteBufResponse = ByteBufAllocator.DEFAULT.directBuffer(1);

        if (result) {
            byteBufResponse.writeByte(signalTrue.getSignalByte());
        } else {
            byteBufResponse.writeByte(signalFalse.getSignalByte());
        }
        return byteBufResponse;
    }

    private void clearPasswordAndLogin() {
        login = null;
        password = null;
    }
}
