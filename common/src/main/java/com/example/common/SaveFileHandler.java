package com.example.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;

public class SaveFileHandler extends ChannelInboundHandlerAdapter {

    private HandlerState handlerState = HandlerState.IDLE;
    private int filenameLength;
    private String filename;
    private long fileLength;
    private long receivedFileLength;
    private BufferedOutputStream out;

    private final String pathSaveFiles;
    private final Logger logger;

    public SaveFileHandler(String pathSaveFiles, Logger logger) {
        this.pathSaveFiles = pathSaveFiles;
        this.logger = logger;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        while (buf.readableBytes() > 0){
            if (handlerState == HandlerState.IDLE){
                checkingSignalByte(buf);
            }

            if (handlerState == HandlerState.NAME_LENGTH){
                readingFilenameLength(buf);
            }

            if (handlerState == HandlerState.NAME){
                readingFilename(buf);
            }

            if (handlerState == HandlerState.FILE_LENGTH){
                readingFileLength(buf);
            }

            if (handlerState == HandlerState.FILE){
                writingFile(buf, ctx);
            }

            if (handlerState == HandlerState.CHECK_COMPLETE){

            }
        }
        if (buf.readableBytes() == 0){
            buf.release();
        }
    }

    private void checkingSignalByte(ByteBuf buf) {
        byte checkState = buf.readByte();
        if (checkState == SignalBytes.SENDING_FILE.getSignalByte()){
            receivedFileLength = 0L;
            handlerState = HandlerState.NAME_LENGTH;
            logger.info("File state is user send file");
        }
        if (checkState == SignalBytes.RECEIVED_SUCCESS_FILE.getSignalByte()){
            logger.info("File sending success");
        }
    }

    private void readingFilenameLength(ByteBuf buf) {
        if (buf.readableBytes() >= 4){
            filenameLength = buf.readInt();
            handlerState = HandlerState.NAME;
            logger.info("Length name: " + filenameLength + " bytes");
        }
    }

    private void readingFilename(ByteBuf buf) throws FileNotFoundException {
        if (buf.readableBytes() >= filenameLength){
            byte[] byteBufFilename = new byte[filenameLength];
            buf.readBytes(byteBufFilename);
            filename = new String(byteBufFilename, StandardCharsets.UTF_8);
            out = new BufferedOutputStream(new FileOutputStream(pathSaveFiles +
                    FileSystems.getDefault().getSeparator() + filename));
            handlerState = HandlerState.FILE_LENGTH;
            logger.info("Filename: " + filename);
        }
    }

    private void readingFileLength(ByteBuf buf) {
        if (buf.readableBytes() >= 8){
            fileLength = buf.readLong();
            logger.info("File length: " + fileLength);
            handlerState = HandlerState.FILE;
        }
    }

    private void writingFile(ByteBuf buf, ChannelHandlerContext ctx) throws IOException {
        while (buf.readableBytes() > 0){
            out.write(buf.readByte());
            receivedFileLength++;
            if (receivedFileLength == fileLength){
                handlerState = HandlerState.IDLE;
                logger.info("File received");
                out.close();
                successfullyReceivedFile(ctx);
                break;
            }
        }
    }

    private void successfullyReceivedFile(ChannelHandlerContext ctx) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1);
        buf.writeByte(SignalBytes.RECEIVED_SUCCESS_FILE.getSignalByte());
        ctx.writeAndFlush(buf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause.getMessage().equals("An existing connection was forcibly closed by the remote host")){
            logger.info("Client disconnected");
            return;
        }
        cause.printStackTrace();
        ctx.close();
    }
}