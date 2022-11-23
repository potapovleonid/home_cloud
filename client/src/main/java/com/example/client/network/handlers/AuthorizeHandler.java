package com.example.client.network.handlers;

import com.example.client.CallbackAuthenticated;
import com.example.common.constants.SignalBytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

public class AuthorizeHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger;
    private final CallbackAuthenticated callbackAuthenticated;

    public AuthorizeHandler(Logger logger, CallbackAuthenticated callbackAuthenticated) {
        this.logger = logger;
        this.callbackAuthenticated = callbackAuthenticated;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte checkResponse = buf.readByte();
        if (checkResponse == SignalBytes.SUCCESS_AUTH.getSignalByte()) {
            logger.info("Auth is success");
            ctx.pipeline().remove(AuthorizeHandler.class);
            callbackAuthenticated.isAuthorize(true);
        }
        if (checkResponse == SignalBytes.FAILED_AUTH.getSignalByte()){
            logger.info("Auth is fail");
            callbackAuthenticated.isAuthorize(false);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
