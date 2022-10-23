package com.example.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSender {
    private static final byte STATUS_SEND_FILE = 25;

    public static void sendFile(Path path, Channel channel, ChannelFutureListener completeListener) throws IOException {
        FileRegion region = new DefaultFileRegion(path.toFile(), 0, Files.size(path));

        byte[] filenameBytes = path.getFileName().toString().getBytes(StandardCharsets.UTF_8);
        int filenameLength = filenameBytes.length;
        long fileLength = Files.size(path);

        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1 + filenameLength + filenameBytes.length + 8);

        buf.writeByte(FileSender.STATUS_SEND_FILE);
        buf.writeInt(filenameLength);
        buf.writeBytes(filenameBytes);
        buf.writeLong(fileLength);

        channel.writeAndFlush(buf);
        LoggerApp.addInfo("Sent file info");

        ChannelFuture sendOperationFuture = channel.writeAndFlush(region);
        if (completeListener != null){
            sendOperationFuture.addListener(completeListener);
        }
        LoggerApp.addInfo("Sent file");
    }
}
