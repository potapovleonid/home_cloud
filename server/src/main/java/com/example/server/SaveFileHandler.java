package com.example.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;

public class SaveFileHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = Logger.getLogger(SaveFileHandler.class);
    private State state = State.IDLE;
    private int filenameLength;
    private String filename;
    private long fileLength;
    private long receivedFileLength;
    private BufferedOutputStream out;
    private final byte STATUS_SEND_FILE = 25;

    public SaveFileHandler() {
        BasicConfigurator.configure();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        while (buf.readableBytes() > 0){
            if (state == State.IDLE){
                byte checkState = buf.readByte();
                if (checkState == STATUS_SEND_FILE){
                    receivedFileLength = 0L;
                    state = State.NAME_LENGTH;
                    logger.trace("File state is user send file");
                }
            }

            if (state == State.NAME_LENGTH){
                if (buf.readableBytes() >= 4){
                    filenameLength = buf.readInt();
                    state = State.NAME;
                    logger.trace("Length name: " + filenameLength + " bytes");
                }
            }

            if (state == State.NAME){
                if (buf.readableBytes() >= filenameLength){
                    byte[] byteBufFilename = new byte[filenameLength];
                    buf.readBytes(byteBufFilename);
                    filename = new String(byteBufFilename, StandardCharsets.UTF_8);
                    out = new BufferedOutputStream(new FileOutputStream("server_files" +
                            FileSystems.getDefault().getSeparator() + filename));
                    state = State.FILE_LENGTH;
                    logger.trace("Filename: " + filename);
                }
            }

            if (state == State.FILE_LENGTH){
                if (buf.readableBytes() >= 8){
                    fileLength = buf.readLong();
                    logger.trace("File length: " + fileLength);
                    state = State.FILE;
                }
            }

            if (state == State.FILE){
                while (buf.readableBytes() > 0){
                    out.write(buf.readByte());
                    receivedFileLength++;
                    if (receivedFileLength == fileLength){
                        state = State.IDLE;
                        logger.trace("File received");
                        out.close();
                        break;
                    }
                }
            }
        }
        if (buf.readableBytes() == 0){
            buf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
