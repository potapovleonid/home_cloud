package com.example.client.network;

import com.example.common.constants.SignalBytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

public class AuthHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger;

    public AuthHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte checkResponse = buf.readByte();
        if (checkResponse == SignalBytes.SUCCESS_AUTH.getSignalByte()) {
            logger.info("Auth is success");
            ctx.pipeline().remove(AuthHandler.class);
        }
        if (checkResponse == SignalBytes.FAILED_AUTH.getSignalByte()){
            logger.info("Auth is fail");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
