package com.example.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSender {
    public static void sendFile(Path path, Channel channel, ChannelFutureListener completeListener, Logger logger) throws IOException {
        FileRegion region = new DefaultFileRegion(path.toFile(), 0, Files.size(path));

        byte[] filenameBytes = path.getFileName().toString().getBytes(StandardCharsets.UTF_8);
        int filenameLength = filenameBytes.length;
        long fileLength = Files.size(path);

        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1 + 4 + filenameLength + 8);

        buf.writeByte(SignalBytes.SENDING_FILE.getSignalByte());
        buf.writeInt(filenameLength);
        buf.writeBytes(filenameBytes);
        buf.writeLong(fileLength);

        channel.writeAndFlush(buf);
        logger.info("File info sent");

        ChannelFuture sendOperationFuture = channel.writeAndFlush(region);
        if (completeListener != null){
            sendOperationFuture.addListener(completeListener);
        }
        logger.info("File sent");
    }
}
