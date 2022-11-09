package com.example.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;

public class AuthSender {

//    TODO Callback

    public static void sendAuthRequest(Channel channel, String login, String password){
        int lengthLogin = login.getBytes().length;
        byte[] bytesLogin = login.getBytes();
        int lengthPassword = password.getBytes().length;
        byte[] bytesPassword = password.getBytes();

        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(4 + lengthLogin + 4 + lengthPassword);
        buf.writeInt(lengthLogin);
        buf.writeBytes(bytesLogin);
        buf.writeInt(lengthPassword);
        buf.writeBytes(bytesPassword);

        channel.writeAndFlush(buf);
    }
}
