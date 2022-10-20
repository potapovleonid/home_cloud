package com.example.server;

import com.example.common.SendFile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
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
            SendFile sf = (SendFile) msg;
            Path path = Paths.get("server_files", sf.getFilename());

            createDirectoryIfNotExist(Paths.get("server_files"));
            if (!Files.exists(path)) {
                Files.createFile(path);
            } else {
                ctx.writeAndFlush("File already exists");
            }
            Files.write(path, sf.getByteBuf(), StandardOpenOption.APPEND);
            ctx.writeAndFlush("File accept");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void createDirectoryIfNotExist(Path path) throws IOException {
        if (!Files.exists(path)){
            Files.createDirectory(path);
        }
    }
}
