package com.example.client;

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
    private static Network myNetwork = new Network();
    private Channel channel;

    public void start(CountDownLatch countDownNetworkConnections) {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress("localhost", 8189)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sh) throws Exception {
                            sh.pipeline().addLast(new SaveFileHandler());
                            channel = sh;
                        }
                    });
            LoggerApp.addInfo("Connection complete");
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
