package com.example.client.network.handlers;

import com.example.client.callbacks.CallbackGettingFileList;
import com.example.client.network.networking.ResponseStatusComplete;
import com.example.common.FileInfo;
import com.example.common.constants.HandlerState;
import com.example.common.constants.LengthBytesDataTypes;
import com.example.common.constants.SignalBytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class IncomingHandler extends ChannelInboundHandlerAdapter {
    private HandlerState handlerState = HandlerState.IDLE;

    private int listLength;
    private int filenameLength;
    private long fileLength;
    private long receivedFileLength;
    private BufferedOutputStream out;

    private String pathSaveFiles;
    private final Logger logger;

    private CallbackGettingFileList callbackGettingFileList;

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
        ByteBuf buf = (ByteBuf) msg;
        while (buf.readableBytes() > 0) {
            switch (handlerState) {
                case IDLE:
                    checkingSignalByte(buf);
                    break;
                case LIST_LENGTH:
                    readingFileListLength(buf);
                    break;
                case LIST:
                    readingFilesList(buf);
                    break;
                case NAME_LENGTH:
                    readingFilenameLength(buf);
                    break;
                case NAME:
                    readingFilename(buf);
                    break;
                case FILE_LENGTH:
                    readingFileLength(buf);
                    break;
                case FILE:
                    writingFile(buf, ctx);
                    break;
            }
        }
        if (buf.readableBytes() == 0) {
            buf.release();
        }
    }

    private void checkingSignalByte(ByteBuf buf) {
        byte checkState = buf.readByte();
        if (checkState == SignalBytes.SENDING_FILE.getSignalByte()) {
            receivedFileLength = 0L;
            handlerState = HandlerState.NAME_LENGTH;
            logger.info("File state is user send file");
        }
        if (checkState == SignalBytes.RECEIVED_SUCCESS_FILE.getSignalByte()) {
            logger.info("File sending success");
        }
        if (checkState == SignalBytes.SENDING_LIST.getSignalByte()) {
            handlerState = HandlerState.LIST_LENGTH;
            logger.info("Send file list");
        }
    }

    private void readingFileListLength(ByteBuf buf) {
        if (buf.readableBytes() >= LengthBytesDataTypes.INT.getLength()) {
            listLength = buf.readInt();
            handlerState = HandlerState.LIST;
            logger.info("Length list: " + listLength + " bytes");
        }
    }

    private void readingFilesList(ByteBuf buf) throws IOException, ClassNotFoundException {
        if (buf.readableBytes() >= listLength) {
            logger.info("start download list");

            byte[] bytesOfTheList = new byte[listLength];
            buf.readBytes(bytesOfTheList);

            ByteArrayInputStream is = new ByteArrayInputStream(bytesOfTheList);
            ObjectInputStream ois = new ObjectInputStream(is);

            List<FileInfo> filesList = (List<FileInfo>) ois.readObject();

            logger.info(String.format("Files list length %d", filesList.toArray().length));

            filesList.forEach(s -> System.out.println(s.toString()));
            handlerState = HandlerState.IDLE;
            logger.info("State: " + handlerState);

            callbackGettingFileList.getFileList(filesList);
        }
    }

    private void readingFilenameLength(ByteBuf buf) {
        if (buf.readableBytes() >= LengthBytesDataTypes.INT.getLength()) {
            filenameLength = buf.readInt();
            handlerState = HandlerState.NAME;
            logger.info("Length name: " + filenameLength + " bytes");
        }
    }

    private void readingFilename(ByteBuf buf) throws FileNotFoundException {
        if (buf.readableBytes() >= filenameLength) {
            byte[] byteBufFilename = new byte[filenameLength];
            buf.readBytes(byteBufFilename);
            String filename = new String(byteBufFilename, StandardCharsets.UTF_8);

            checkExistsFilePathAndCreate();

            out = new BufferedOutputStream(new FileOutputStream(pathSaveFiles +
                    FileSystems.getDefault().getSeparator() + filename));
            handlerState = HandlerState.FILE_LENGTH;
            logger.info("Filename: " + filename);
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
            logger.info("File length: " + fileLength);
            handlerState = HandlerState.FILE;
        }
    }

    private void writingFile(ByteBuf buf, ChannelHandlerContext ctx) throws IOException {
        while (buf.readableBytes() > 0) {
            out.write(buf.readByte());
            receivedFileLength++;
            if (receivedFileLength == fileLength) {
                handlerState = HandlerState.IDLE;
                logger.info("File received");
                JOptionPane.showMessageDialog(null, "File's success downloaded");
                out.close();
                successfullyReceivedFile(ctx);
                break;
            }
        }
    }

    private void successfullyReceivedFile(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new ResponseStatusComplete(true));
    }

    public void setCallbackGetFileList(CallbackGettingFileList callback){
        this.callbackGettingFileList = callback;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause.getMessage().equals("An existing connection was forcibly closed by the remote host")) {
            logger.info("Client disconnected");
            return;
        }
        cause.printStackTrace();
        ctx.close();
    }
}
