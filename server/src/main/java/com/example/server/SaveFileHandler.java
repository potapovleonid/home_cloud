package com.example.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;

public class SaveFileHandler extends ChannelInboundHandlerAdapter {

    private State state = State.IDLE;
    private int filenameLength;
    private String filename;
    private long fileLength;
    private long receivedFileLength;
    private BufferedOutputStream out;
    private final byte STATUS_SEND_FILE = 25;

    public SaveFileHandler() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        while (buf.readableBytes() > 0){
            if (state == State.IDLE){
                checkingFileSubmission(buf);
            }

            if (state == State.NAME_LENGTH){
                readingFilenameLength(buf);
            }

            if (state == State.NAME){
                readingFilename(buf);
            }

            if (state == State.FILE_LENGTH){
                readingFileLength(buf);
            }

            if (state == State.FILE){
                writingFile(buf);
            }
        }
        if (buf.readableBytes() == 0){
            buf.release();
        }
    }

    private void checkingFileSubmission(ByteBuf buf) {
        byte checkState = buf.readByte();
        if (checkState == STATUS_SEND_FILE){
            receivedFileLength = 0L;
            state = State.NAME_LENGTH;
            LoggerApp.addInfo("File state is user send file");
        }
    }

    private void readingFilenameLength(ByteBuf buf) {
        if (buf.readableBytes() >= 4){
            filenameLength = buf.readInt();
            state = State.NAME;
            LoggerApp.addInfo("Length name: " + filenameLength + " bytes");
        }
    }

    private void readingFilename(ByteBuf buf) throws FileNotFoundException {
        if (buf.readableBytes() >= filenameLength){
            byte[] byteBufFilename = new byte[filenameLength];
            buf.readBytes(byteBufFilename);
            filename = new String(byteBufFilename, StandardCharsets.UTF_8);
            out = new BufferedOutputStream(new FileOutputStream("server_files" +
                    FileSystems.getDefault().getSeparator() + filename));
            state = State.FILE_LENGTH;
            LoggerApp.addInfo("Filename: " + filename);
        }
    }

    private void readingFileLength(ByteBuf buf) {
        if (buf.readableBytes() >= 8){
            fileLength = buf.readLong();
            LoggerApp.addInfo("File length: " + fileLength);
            state = State.FILE;
        }
    }

    private void writingFile(ByteBuf buf) throws IOException {
        while (buf.readableBytes() > 0){
            out.write(buf.readByte());
            receivedFileLength++;
            if (receivedFileLength == fileLength){
                state = State.IDLE;
                LoggerApp.addInfo("File received");
                out.close();
                break;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause.getMessage().equals("An existing connection was forcibly closed by the remote host")){
            LoggerApp.addInfo("Client disconnected");
            return;
        }
        cause.printStackTrace();
        ctx.close();
    }
}
