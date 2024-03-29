package com.example.server.network;

import com.example.common.FileInfo;
import com.example.common.constants.SignalBytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ListSender {
    public static void sendList(Path path, Channel channel, Logger logger){
        try {
            List<FileInfo> files = Files.list(path).map(FileInfo::new).collect(Collectors.toList());

            byte[] bytesOfTheList = listToByteArray(files);
            int listFilesLength = bytesOfTheList.length;

            ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1 + 4);

            logger.info("Length list: " + listFilesLength);

            buf.writeByte(SignalBytes.LIST_SENDING.getSignalByte());
            buf.writeInt(listFilesLength);

            logger.info("Length signal byte + int: " + buf.readableBytes() + " bytes");

            channel.write(buf);

            buf = ByteBufAllocator.DEFAULT.directBuffer(listFilesLength);
            buf.writeBytes(bytesOfTheList);

            logger.info("Length file list in buffer: " + buf.readableBytes() + " bytes");

            channel.writeAndFlush(buf);
            logger.info("Server sent file list");
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
    }


    private static byte[] listToByteArray(List list) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(list);
        return out.toByteArray();
    }

}
