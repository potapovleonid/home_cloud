package com.example.server;

import com.example.common.SendFile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.*;

public class CheckObjectHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg.getClass().getName());
        if (msg instanceof SendFile){
            System.out.println("Save file...");
            SendFile sf = (SendFile) msg;
            Path path = Paths.get("server_files", sf.getFilename());
            if (!Files.exists(path)) {
                Files.createFile(path);
            } else {
                throw new Exception("File already exists");
            }
            Files.write(path, sf.getByteBuf(), StandardOpenOption.APPEND);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
