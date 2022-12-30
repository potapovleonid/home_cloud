package com.example.client.network.handlers;

import com.example.client.callbacks.CallbackAuthenticated;
import com.example.common.constants.SignalBytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.log4j.Logger;

public class AuthorizeHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger;
    private CallbackAuthenticated callbackAuthenticated;
    private boolean authResult = false;

    public AuthorizeHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (authResult){
            ctx.fireChannelRead(msg);
            return;
        }
        ByteBuf buf = (ByteBuf) msg;
        byte checkResponse = buf.readByte();
        if (checkResponse == SignalBytes.SUCCESS_AUTH.getSignalByte()) {
            authResult = true;
            logger.info("Get signal byte " + SignalBytes.SUCCESS_AUTH.getSignalByte() + " - Authorize is success");
            callbackAuthenticated.isAuthorize(true);
        }
        if (checkResponse == SignalBytes.FAILED_AUTH.getSignalByte()){
            logger.info("Get signal byte " + SignalBytes.FAILED_AUTH.getSignalByte() + " - Authorize is fail");
            callbackAuthenticated.isAuthorize(false);
        }
        if (checkResponse == SignalBytes.SUCCESS_REGISTER.getSignalByte()){
            logger.info("Get signal byte " + SignalBytes.SUCCESS_REGISTER.getSignalByte() + " - Register new user is success");
            new Alert(Alert.AlertType.INFORMATION, "New user successfully created", ButtonType.OK);
        }
        if (checkResponse == SignalBytes.FAILED_REGISTER.getSignalByte()){
            logger.info("Get signal byte " + SignalBytes.FAILED_REGISTER.getSignalByte() + " - Register new user is fail");
            new Alert(Alert.AlertType.WARNING, "New user fail create", ButtonType.OK);
        }
        if (checkResponse == SignalBytes.FAILED_REGISTER_EXIST_USER.getSignalByte()){
            logger.info("Get signal byte " + SignalBytes.FAILED_REGISTER_EXIST_USER.getSignalByte() + " - Register new user is fail");
            new Alert(Alert.AlertType.WARNING, "Username is already in use", ButtonType.OK);
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
