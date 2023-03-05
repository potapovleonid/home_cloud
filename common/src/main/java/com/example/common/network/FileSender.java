package com.example.common.network;

import com.example.common.constants.LengthBytesDataTypes;
import com.example.common.constants.SignalBytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSender {
    public static void sendFile(Path path, ChannelHandlerContext channel, ChannelFutureListener completeListener,
                                Logger logger) throws IOException {

        logger.info(path.toAbsolutePath() + " sending");
        FileRegion region = new DefaultFileRegion(path.toFile(), 0, Files.size(path));

        byte[] filenameBytes = path.getFileName().toString().getBytes(StandardCharsets.UTF_8);
        int filenameLength = filenameBytes.length;
        long fileLength = Files.size(path);

        int length =
                LengthBytesDataTypes.SIGNAL_BYTE.getLength() +
                LengthBytesDataTypes.INT.getLength() +
                filenameLength +
                LengthBytesDataTypes.LONG.getLength();

        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(length);

        buf.writeByte(SignalBytes.FILE_SENDING.getSignalByte());
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
