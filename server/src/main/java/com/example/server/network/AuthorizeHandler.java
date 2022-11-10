package com.example.server.network;

import com.example.common.constants.SignalBytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;

public class AuthorizeHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger;

    public AuthorizeHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;

        int lengthLogin = buf.readInt();
        byte[] loginBytes = new byte[lengthLogin];
        buf.readBytes(loginBytes);

        String login = new String(loginBytes, StandardCharsets.UTF_8);

        logger.info(String.format("Login length %d, login: %s", lengthLogin, login));

        int lengthPassword = buf.readInt();
        byte[] passwordBytes = new byte[lengthPassword];
        buf.readBytes(passwordBytes);

        String password = new String(passwordBytes, StandardCharsets.UTF_8);

        boolean resultAuth = SQLConnection.authorizeUser(login, password);

        logger.info(String.format("Result auth: %b", resultAuth));

        if(resultAuth){
            ctx.pipeline().remove(AuthorizeHandler.class);
        }

        sendResponse(ctx, resultAuth);
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
