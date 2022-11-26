package com.example.client.network.handlers;

import com.example.client.CallbackAuthenticated;
import com.example.common.constants.SignalBytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

public class AuthorizeHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger;
    private CallbackAuthenticated callbackAuthenticated;

    public AuthorizeHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte checkResponse = buf.readByte();
        if (checkResponse == SignalBytes.SUCCESS_AUTH.getSignalByte()) {
            logger.info("Get signal byte " + SignalBytes.SUCCESS_AUTH.getSignalByte() + " - Authorize is success");
            callbackAuthenticated.isAuthorize(true);
            ctx.pipeline().remove(AuthorizeHandler.class);
        }
        if (checkResponse == SignalBytes.FAILED_AUTH.getSignalByte()){
            logger.info("Get signal byte " + SignalBytes.FAILED_AUTH.getSignalByte() + " - Authorize is fail");
            callbackAuthenticated.isAuthorize(false);
        }
    }

    public void setCallbackAuthenticated(CallbackAuthenticated callbackAuthenticated){
        this.callbackAuthenticated = callbackAuthenticated;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
