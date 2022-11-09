package com.example.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

public class AuthHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger;
    private final byte SUCCESS_AUTH = 127;
    private final byte FAILED_AUTH = -128;

    public AuthHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte checkResponse = buf.readByte();
        if (checkResponse == SUCCESS_AUTH) {
            logger.info("Auth is success");
            ctx.pipeline().remove(AuthHandler.class);
        }
        if (checkResponse == FAILED_AUTH){
            logger.info("Auth is fail");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
