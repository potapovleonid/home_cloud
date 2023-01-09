package com.example.server.network;

import com.example.common.constants.SignalBytes;
import com.example.server.LoggerApp;
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

        if (signal == SignalBytes.REQUEST_AUTHORIZE.getSignalByte()) {
            int lengthLogin = buf.readInt();
            byte[] loginBytes = new byte[lengthLogin];
            buf.readBytes(loginBytes);

            String login = new String(loginBytes, StandardCharsets.UTF_8);

            logger.info(String.format("Login length %d, login: %s", lengthLogin, login));

            int lengthPassword = buf.readInt();
            byte[] passwordBytes = new byte[lengthPassword];
            buf.readBytes(passwordBytes);

            String password = new String(passwordBytes, StandardCharsets.UTF_8);

            resultAuth = SQLConnection.authorizeUser(login, password);

            logger.info(String.format("Result auth: %b", resultAuth));

            if (resultAuth) {
                sendResponse(ctx, resultAuth);
                ctx.fireChannelRead(login);
            }
        }
    }

    private void sendResponse(ChannelHandlerContext ctx, boolean result){
        ByteBuf byteBufResponse = getByteBufWithResponse(result);

        ctx.writeAndFlush(byteBufResponse);
    }

    private ByteBuf getByteBufWithResponse(boolean result) {
        ByteBuf byteBufResponse = ByteBufAllocator.DEFAULT.directBuffer(1);

        if (result) {
            byteBufResponse.writeByte(SignalBytes.SUCCESS_AUTH.getSignalByte());
        } else {
            byteBufResponse.writeByte(SignalBytes.FAILED_AUTH.getSignalByte());
        }
        return byteBufResponse;
    }
}
