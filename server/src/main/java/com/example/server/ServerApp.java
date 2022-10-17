package com.example.server;

import com.example.common.SendFile;
import com.example.common.SendObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class ServerApp {

    static Logger logger = Logger.getLogger(ServerApp.class);

    private final int PORT;

    public ServerApp(int PORT) {
        BasicConfigurator.configure();
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
                            sh.pipeline().addLast(new ObjectEncoder());
                            sh.pipeline().addLast(new ObjectDecoder(1024 * 1024 * 100,
                                    ClassResolvers.cacheDisabled(null)));
                            sh.pipeline().addLast(new CheckObjectHandler());
                        }
                    });

            ChannelFuture chf = sb.bind(PORT).sync();
            System.out.println("Started");
            chf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ServerApp(8189).run();
    }
}
