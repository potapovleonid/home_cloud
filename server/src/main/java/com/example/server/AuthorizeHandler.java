package com.example.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;

public class AuthorizeHandler extends ChannelInboundHandlerAdapter {

    private Logger logger;

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

        int lengthPassword = buf.readInt();
        byte[] passwordBytes = new byte[lengthLogin];
        buf.readBytes(passwordBytes);

        String password = new String(passwordBytes, StandardCharsets.UTF_8);

        if(SQLConnection.authorizeUser(login, password)){
            ctx.pipeline().remove(AuthorizeHandler.class);
        }
    }
}
