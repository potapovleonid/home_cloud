package com.example.client.network;

import com.example.client.CallbackAuthenticated;
import com.example.client.LoggerApp;
import com.example.client.network.handlers.AuthorizeHandler;
import com.example.client.network.handlers.OutboundHandler;
import com.example.client.network.handlers.SaveFileHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CountDownLatch;

public class Network {
    private static final Network myNetwork = new Network();
    private Channel channel;

    public void start(CountDownLatch countDownNetworkConnections, CallbackAuthenticated callbackAuthenticated) {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress("localhost", 8189)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sh) {
                            sh.pipeline().addLast(new OutboundHandler());
                            sh.pipeline().addLast(new AuthorizeHandler(LoggerApp.getLogger(), callbackAuthenticated));
                            sh.pipeline().addLast(new SaveFileHandler("client_files", LoggerApp.getLogger()));
                            channel = sh;
                        }
                    });
            LoggerApp.info("Connection complete");
            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            countDownNetworkConnections.countDown();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Network getNetwork() {
        return myNetwork;
    }

    public Channel getChannel() {
        return channel;
    }
}
