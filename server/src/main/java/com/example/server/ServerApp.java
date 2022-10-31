package com.example.server;

import com.example.common.FileSender;
import com.example.common.SaveFileHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

public class ServerApp {

    private final int PORT;

    public ServerApp(int PORT) {
        this.PORT = PORT;
    }

    private void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sh) throws Exception {
                            sh.pipeline().addLast(new SaveFileHandler("server_files", LoggerApp.getLogger()));
                            LoggerApp.info("Client connection");
                            sendFile(sh);
                        }
                    });

            ChannelFuture chf = sb.bind(PORT).sync();
            chf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void sendFile(SocketChannel sh) {
        try {
            FileSender.sendFile(
                    Paths.get("server_files" + FileSystems.getDefault().getSeparator() + "welcome.txt"),
                    sh,
                    finishListener -> {
                        if (!finishListener.isSuccess()) {
                            LoggerApp.info(finishListener.cause().getMessage());
                        }
                        if (finishListener.isSuccess()) {
                            LoggerApp.info("Send file is completed");
                        }
                    }, LoggerApp.getLogger());
            ListSender.sendFile(Paths.get("."), sh, LoggerApp.getLogger());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LoggerApp.init();
        new ServerApp(8189).run();
    }
}
