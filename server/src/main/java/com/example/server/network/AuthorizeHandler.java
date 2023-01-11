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

    public AuthorizeHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        if(resultAuth){
            ctx.fireChannelRead(msg);
            return;
        }

        ByteBuf buf = (ByteBuf) msg;

        byte signal = buf.readByte();

        if (signal == SignalBytes.REQUEST_REGISTER_NEW_USER.getSignalByte()){
            logger.info("Get signal register new user");
//            TODO ADD METHOD
            int lengthLogin = buf.readInt();
            byte[] loginBytes = new byte[lengthLogin];
            buf.readBytes(loginBytes);

            String login = new String(loginBytes, StandardCharsets.UTF_8);

            logger.info(String.format("Get login: %s", login));

            int lengthPassword = buf.readInt();
            byte[] passwordBytes = new byte[lengthPassword];
            buf.readBytes(passwordBytes);

            String password = new String(passwordBytes, StandardCharsets.UTF_8);
//            TODO ----

            boolean resultRegisterUser = SQLConnection.addUser(login, password);

            sendResponseResult(ctx, resultRegisterUser, SignalBytes.SUCCESS_REGISTER_USER, SignalBytes.FAILED_REGISTER_USER);
        }

        if (signal == SignalBytes.REQUEST_AUTHORIZE.getSignalByte()) {
            logger.info("Get signal authorize user");

            int lengthLogin = buf.readInt();
            byte[] loginBytes = new byte[lengthLogin];
            buf.readBytes(loginBytes);

            String login = new String(loginBytes, StandardCharsets.UTF_8);

            logger.info(String.format("Get login: %s", login));

            int lengthPassword = buf.readInt();
            byte[] passwordBytes = new byte[lengthPassword];
            buf.readBytes(passwordBytes);

            String password = new String(passwordBytes, StandardCharsets.UTF_8);

            resultAuth = SQLConnection.authorizeUser(login, password);

            logger.info(String.format("Result auth: %b", resultAuth));

            if (resultAuth) {
                sendResponseResult(ctx, resultAuth, SignalBytes.SUCCESS_AUTH, SignalBytes.FAILED_AUTH);
                ctx.fireChannelRead(login);
            }
        }
    }

//    TODO readLogin
//    TODO readPassword

    private void sendResponseResult(ChannelHandlerContext ctx, boolean result, SignalBytes signalTrue, SignalBytes signalFalse){
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
}
