package com.example.client.network.handlers;

import com.example.client.callbacks.CallbackAuthenticated;
import com.example.common.constants.SignalBytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;
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
        if (checkResponse == SignalBytes.AUTHORIZE_SUCCESS.getSignalByte()) {
            authResult = true;
            logger.info("Get signal byte " + SignalBytes.AUTHORIZE_SUCCESS.getSignalByte() + " - Authorize is success");
            callbackAuthenticated.isAuthorize(true);
        }
        if (checkResponse == SignalBytes.AUTHORIZE_FAILED.getSignalByte()){
            logger.info("Get signal byte " + SignalBytes.AUTHORIZE_FAILED.getSignalByte() + " - Authorize is fail");
            callbackAuthenticated.isAuthorize(false);
        }
        if (checkResponse == SignalBytes.REGISTER_USER_SUCCESS.getSignalByte()){
            logger.info("Get signal byte " + SignalBytes.REGISTER_USER_SUCCESS.getSignalByte() + " - Register new user is success");
            Platform.runLater(() ->
                    new Alert(Alert.AlertType.INFORMATION, "New user successfully created", ButtonType.OK).showAndWait()
            );
        }
        if (checkResponse == SignalBytes.REGISTER_USER_FAILED.getSignalByte()){
            logger.info("Get signal byte " + SignalBytes.REGISTER_USER_FAILED.getSignalByte() + " - Register new user is fail");
            Platform.runLater(() ->
                    new Alert(Alert.AlertType.WARNING, "Username is already in use", ButtonType.OK).showAndWait()
            );
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
