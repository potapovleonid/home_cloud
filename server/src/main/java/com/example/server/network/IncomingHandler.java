package com.example.server.network;

import com.example.common.constants.HandlerState;
import com.example.common.constants.LengthBytesDataTypes;
import com.example.common.constants.SignalBytes;
import com.example.common.network.FileSender;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class IncomingHandler extends ChannelInboundHandlerAdapter {
    private HandlerState handlerState = HandlerState.IDLE;

    private int filenameLength;
    private String filename;
    private long fileLength;
    private long receivedFileLength;
    private BufferedOutputStream out;

    private String pathSaveFiles;
    private final Logger logger;

    public IncomingHandler(String pathSaveFiles, Logger logger) {
        Path saveDirectory = Paths.get(pathSaveFiles);
        this.pathSaveFiles = pathSaveFiles;
        this.logger = logger;
        if (!Files.exists(saveDirectory)) {
            try {
                Files.createDirectory(saveDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (checkAndSetUserLoginIfMsgIsLogin(msg)) {
            return;
        }

        ByteBuf buf = (ByteBuf) msg;
        while (buf.readableBytes() > 0) {
            switch (handlerState) {
                case IDLE:
                    checkingSignalByte(buf, ctx);
                    break;
                case NAME_LENGTH:
                    readingFilenameLength(buf);
                    swapHandlerState(HandlerState.NAME, "Length name: " + filenameLength + " bytes");
                    break;
                case NAME:
                    readingFilename(buf);
                    checkExistFileAndCreateOutput();
                    swapHandlerState(HandlerState.FILE_LENGTH, "Filename: " + filename);
                    break;
                case FILE_LENGTH:
                    readingFileLength(buf);
                    break;
                case FILE:
                    writingFile(buf, ctx);
                    break;
                case REQUEST_NAME_LENGTH:
                    readRequestFilenameLength(buf);
                    break;
                case REQUEST_NAME:
                    readingRequestFilenameAndSendFile(buf, ctx);
                    break;
            }
        }
        if (buf.readableBytes() == 0) {
            buf.release();
        }
    }

    private void sendFileList(ChannelHandlerContext ctx) {
        ListSender.sendList(Paths.get(pathSaveFiles), ctx.channel(), logger);
    }

    private boolean checkAndSetUserLoginIfMsgIsLogin(Object msg) {
        if (msg instanceof String) {
            String userLogin = (String) msg;
            pathSaveFiles += FileSystems.getDefault().getSeparator() + userLogin;
            logger.info("Set path save files: " + pathSaveFiles);
            return true;
        }
        return false;
    }

    private void checkingSignalByte(ByteBuf buf, ChannelHandlerContext ctx) {
        byte checkState = buf.readByte();
        if (checkState == SignalBytes.SENDING_FILE.getSignalByte()) {
            receivedFileLength = 0L;
            swapHandlerState(HandlerState.NAME_LENGTH, "File state is user send file");
        }
        if (checkState == SignalBytes.REQUEST_FILE.getSignalByte()) {
            swapHandlerState(HandlerState.REQUEST_NAME_LENGTH, "Request file");
        }
        if (checkState == SignalBytes.RECEIVED_SUCCESS_FILE.getSignalByte()) {
            logger.info("File sending success");
        }
        if (checkState == SignalBytes.REQUEST_LIST.getSignalByte()) {
            sendFileList(ctx);
        }
    }

    private void swapHandlerState(HandlerState state, String loggerMsg) {
        handlerState = state;
        logger.info(loggerMsg);
    }

    private void readingFilenameLength(ByteBuf buf) {
        if (buf.readableBytes() >= LengthBytesDataTypes.INT.getLength()) {
            filenameLength = buf.readInt();
        }
    }

    private void readingFilename(ByteBuf buf) {
        if (buf.readableBytes() >= filenameLength) {
            byte[] byteBufFilename = new byte[filenameLength];
            buf.readBytes(byteBufFilename);
            filename = new String(byteBufFilename, StandardCharsets.UTF_8);
        }
    }

    private void checkExistFileAndCreateOutput() {
        checkExistsFilePathAndCreate();

        try {
            out = new BufferedOutputStream(new FileOutputStream(pathSaveFiles +
                    FileSystems.getDefault().getSeparator() + filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkExistsFilePathAndCreate() {
        if (!Files.exists(Paths.get(pathSaveFiles))) {
            try {
                Files.createDirectory(Paths.get(pathSaveFiles));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readingFileLength(ByteBuf buf) {
        if (buf.readableBytes() >= LengthBytesDataTypes.LONG.getLength()) {
            fileLength = buf.readLong();
            swapHandlerState(HandlerState.FILE, "File length: " + fileLength);
        }
    }

    private void writingFile(ByteBuf buf, ChannelHandlerContext ctx) throws IOException {
        while (buf.readableBytes() > 0) {
            out.write(buf.readByte());
            receivedFileLength++;
            if (receivedFileLength == fileLength) {
                swapHandlerState(HandlerState.IDLE, "File received");
                out.close();
                successfullyReceivedFile(ctx);
                break;
            }
        }
    }

    private void successfullyReceivedFile(ChannelHandlerContext ctx) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(LengthBytesDataTypes.SIGNAL_BYTE.getLength());
        buf.writeByte(SignalBytes.RECEIVED_SUCCESS_FILE.getSignalByte());
        ctx.writeAndFlush(buf);
        ListSender.sendList(Paths.get(pathSaveFiles), ctx.channel(), logger);
    }

    private void readRequestFilenameLength(ByteBuf buf) {
        readingFilenameLength(buf);
        swapHandlerState(HandlerState.REQUEST_NAME, "Filename length " + filenameLength);
    }

    private void readingRequestFilenameAndSendFile(ByteBuf buf, ChannelHandlerContext ctx) {
        readingFilename(buf);
        sendRequestFile(ctx);
        swapHandlerState(HandlerState.IDLE, "Request filename: " + filename);
    }

    private void sendRequestFile(ChannelHandlerContext ctx) {
        try {
            FileSender.sendFile(Paths.get(pathSaveFiles, FileSystems.getDefault().getSeparator(), filename),
                    ctx.channel(),
                    null,
                    logger
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        swapHandlerState(HandlerState.IDLE, "Requested file sent");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause.getMessage().equals("An existing connection was forcibly closed by the remote host")) {
            logger.info("Client disconnected");
            return;
        }
        logger.warn(cause.getMessage());
        ctx.close();
    }
}
