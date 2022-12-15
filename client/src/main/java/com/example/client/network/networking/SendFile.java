package com.example.client.network.networking;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.apache.log4j.Logger;

import java.nio.file.Path;

public class SendFile implements Networking {

    private final Path filePath;
    private final Channel channel;
    private final ChannelFutureListener channelFutureListener;
    private final Logger logger;

    public SendFile(Path filePath, Channel channel, ChannelFutureListener channelFutureListener, Logger logger) {
        this.filePath = filePath;
        this.channel = channel;
        this.channelFutureListener = channelFutureListener;
        this.logger = logger;
    }

    public Path getFilePath() {
        return filePath;
    }

    public Channel getChannel() {
        return channel;
    }

    public ChannelFutureListener getChannelFutureListener() {
        return channelFutureListener;
    }

    public Logger getLogger() {
        return logger;
    }
}
