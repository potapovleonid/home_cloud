package com.example.server;

import com.example.server.network.AuthorizeHandler;
import com.example.server.network.SQLConnection;
import com.example.server.network.IncomingHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

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
                        protected void initChannel(SocketChannel sh) {
                            sh.pipeline().addLast(new AuthorizeHandler(LoggerApp.getLogger()));
                            sh.pipeline().addLast(new IncomingHandler("server_files", LoggerApp.getLogger()));
                            LoggerApp.info("Client connection");
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

    public static void main(String[] args) {
        SQLConnection.connect();

        new ServerApp(8189).run();

        SQLConnection.disconnect();
    }
}
