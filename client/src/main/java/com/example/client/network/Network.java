package com.example.client.network;

import com.example.client.ConfigApp;
import com.example.client.callbacks.CallbackAuthenticated;
import com.example.client.LoggerApp;
import com.example.client.callbacks.CallbackGettingFileList;
import com.example.client.network.handlers.AuthorizeHandler;
import com.example.client.network.handlers.OutboundHandler;
import com.example.client.network.handlers.IncomingHandler;
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

    public void start(CountDownLatch countDownNetworkConnections) {
        EventLoopGroup group = new NioEventLoopGroup();

        try{
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(ConfigApp.getConfig().getIpAddress(), ConfigApp.getConfig().getPort())
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sh) {
                            sh.pipeline().addLast(new OutboundHandler());
                            sh.pipeline().addLast(new AuthorizeHandler(LoggerApp.getLogger()));
                            sh.pipeline().addLast(new IncomingHandler("client_files", LoggerApp.getLogger()));
                            channel = sh;
                        }
                    });
            LoggerApp.info("Connection complete");
            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            countDownNetworkConnections.countDown();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            countDownNetworkConnections.countDown();
            e.printStackTrace();
        }
    }

    public static Network getNetwork() {
        return myNetwork;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setCallbackAuthenticated(CallbackAuthenticated callback) {
        channel.pipeline().get(AuthorizeHandler.class).setCallbackAuthenticated(callback);
    }

    public void setCallbackGettingFileList(CallbackGettingFileList callback) {
        channel.pipeline().get(IncomingHandler.class).setCallbackGetFileList(callback);
    }
}
