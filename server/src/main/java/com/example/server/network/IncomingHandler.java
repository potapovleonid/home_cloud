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
    private enum typeValues {
        FILENAME, OLD_PASSWORD, NEW_PASSWORD
    }

    private String login;
    private HandlerState handlerState = HandlerState.IDLE;

    private int stringLength;
    private String filename;

    private BufferedOutputStream out;
    private long fileLength;
    private long receivedFileLength;

    private String oldPassword;
    private String newPassword;

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
                    readingStringLength(buf);
                    swapHandlerState(HandlerState.NAME, "Length name: " + stringLength + " bytes");
                    break;
                case NAME:
                    readingStringOnLength(buf, typeValues.FILENAME);
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
            login = userLogin;
            pathSaveFiles += FileSystems.getDefault().getSeparator() + userLogin;
            logger.info("Set path save files: " + pathSaveFiles);
            return true;
        }
        return false;
    }

    private void checkingSignalByte(ByteBuf buf, ChannelHandlerContext ctx) {
        byte checkState = buf.readByte();
        if (checkState == SignalBytes.FILE_SENDING.getSignalByte()) {
            receivedFileLength = 0L;
            swapHandlerState(HandlerState.NAME_LENGTH, "File state is user send file");
            return;
        }
        if (checkState == SignalBytes.FILE_REQUEST.getSignalByte()) {
            swapHandlerState(HandlerState.REQUEST_NAME_LENGTH, "Request file");
            return;
        }
        if (checkState == SignalBytes.FILE_RECEIVED_SUCCESS.getSignalByte()) {
            logger.info("File sending success");
            return;
        }
        if (checkState == SignalBytes.LIST_REQUEST.getSignalByte()) {
            logger.info("Send file list");
            sendFileList(ctx);
            return;
        }
        if (checkState == SignalBytes.CHANGE_PASSWORD_REQUEST.getSignalByte()) {
            logger.info("Get request change password");
            readingOldAndNewPasswords(buf);
            if (checkPasswords()) {
                boolean result = SQLConnection.changePassword(login, oldPassword, newPassword);
                logger.info("Result changing password is: " + result);
                if (result){
                    sendSignalByte(ctx, SignalBytes.CHANGE_PASSWORD_SUCCESS.getSignalByte());
                } else {
                    sendSignalByte(ctx, SignalBytes.CHANGE_PASSWORD_FAILED.getSignalByte());
                }
                return;
            }
            return;
        }
    }

    private void swapHandlerState(HandlerState state, String loggerMsg) {
        handlerState = state;
        logger.info(loggerMsg);
    }

    private void readingStringLength(ByteBuf buf) {
        if (buf.readableBytes() >= LengthBytesDataTypes.INT.getLength()) {
            stringLength = buf.readInt();
        }
    }

    private void readingStringOnLength(ByteBuf buf, typeValues type) {
        if (buf.readableBytes() >= stringLength) {
            byte[] byteBufFilename = new byte[stringLength];
            buf.readBytes(byteBufFilename);
            if (type == typeValues.FILENAME) {
                filename = new String(byteBufFilename, StandardCharsets.UTF_8);
                return;
            }
            if (type == typeValues.OLD_PASSWORD) {
                oldPassword = new String(byteBufFilename, StandardCharsets.UTF_8);
                return;
            }
            if (type == typeValues.NEW_PASSWORD) {
                newPassword = new String(byteBufFilename, StandardCharsets.UTF_8);
            }
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
                out = null;
                sendSignalByte(ctx, SignalBytes.FILE_RECEIVED_SUCCESS.getSignalByte());
                ListSender.sendList(Paths.get(pathSaveFiles), ctx.channel(), logger);
                break;
            }
        }
    }

    private void sendSignalByte(ChannelHandlerContext ctx, byte signalByte) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(LengthBytesDataTypes.SIGNAL_BYTE.getLength());
        logger.info("Byte: " + signalByte);
        buf.writeByte(signalByte);
        ctx.writeAndFlush(buf);
    }


    private void readRequestFilenameLength(ByteBuf buf) {
        readingStringLength(buf);
        swapHandlerState(HandlerState.REQUEST_NAME, "Filename length " + stringLength);
    }

    private void readingRequestFilenameAndSendFile(ByteBuf buf, ChannelHandlerContext ctx) {
        readingStringOnLength(buf, typeValues.FILENAME);
        sendRequestFile(ctx);
        swapHandlerState(HandlerState.IDLE, "Request filename: " + filename);
    }

    private void sendRequestFile(ChannelHandlerContext ctx) {
        try {
            FileSender.sendFile(
                    Paths.get(pathSaveFiles, FileSystems.getDefault().getSeparator(), filename),
                    ctx,
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

    private void readingOldAndNewPasswords(ByteBuf buf) {
        readingStringLength(buf);
        readingStringOnLength(buf, typeValues.OLD_PASSWORD);
        readingStringLength(buf);
        readingStringOnLength(buf, typeValues.NEW_PASSWORD);
        logger.info("Reading passwords completed");
    }

    private boolean checkPasswords() {
        if (new String(oldPassword.getBytes(StandardCharsets.UTF_8)).length() < 5 || new String(newPassword.getBytes(StandardCharsets.UTF_8)).length() < 5) {
            return false;
        }
        return !newPassword.equals(oldPassword);
    }
}