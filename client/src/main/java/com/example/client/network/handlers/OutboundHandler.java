package com.example.client.network.handlers;

import com.example.client.network.networking.*;
import com.example.common.constants.LengthBytesDataTypes;
import com.example.common.constants.SignalBytes;
import com.example.common.network.FileSender;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class OutboundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object obj, ChannelPromise promise) throws Exception {
        if (obj instanceof Networking) {
            if (obj instanceof RequestAuthorize) {
                RequestAuthorize req = (RequestAuthorize) obj;
                sendRequestAuthorize(ctx, req);
                return;
            }
            if (obj instanceof RequestFile) {
                RequestFile req = (RequestFile) obj;
                sendRequestFile(ctx, req);
                return;
            }
            if (obj instanceof ResponseStatusComplete) {
                ResponseStatusComplete resp = (ResponseStatusComplete) obj;
                sendResponseGetStatus(ctx, resp);
                return;
            }
            if (obj instanceof RequestList) {
                sendRequestList(ctx);
                return;
            }
            if (obj instanceof SendFile) {
                SendFile file = (SendFile) obj;
                sendFile(ctx, file);
                return;
            }
            if (obj instanceof RequestRegisterUser) {
                RequestRegisterUser req = (RequestRegisterUser) obj;

                byte[] bytesLogin = req.getLogin().getBytes(StandardCharsets.UTF_8);
                int lengthLogin = bytesLogin.length;
                byte[] bytesPassword = req.getPassword().getBytes(StandardCharsets.UTF_8);
                int lengthPassword = bytesPassword.length;

//                TODO hashcode password
                ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(LengthBytesDataTypes.SIGNAL_BYTE.getLength() +
                        LengthBytesDataTypes.INT.getLength() + lengthLogin +
                        LengthBytesDataTypes.INT.getLength() + lengthPassword);

                buf.writeByte(SignalBytes.REQUEST_REGISTER_NEW_USER.getSignalByte());
                buf.writeInt(lengthLogin);
                buf.writeBytes(bytesLogin);
                buf.writeInt(lengthPassword);
                buf.writeBytes(bytesPassword);

                ctx.writeAndFlush(buf);
            }
            throw new IllegalArgumentException("Unknown outbound command");
        }
    }

    private void sendRequestAuthorize(ChannelHandlerContext ctx, RequestAuthorize req) {
        byte[] bytesLogin = req.getLogin().getBytes(StandardCharsets.UTF_8);
        int lengthLogin = bytesLogin.length;
        byte[] bytesPassword = req.getPassword().getBytes(StandardCharsets.UTF_8);
        int lengthPassword = bytesPassword.length;

        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(LengthBytesDataTypes.SIGNAL_BYTE.getLength() +
                LengthBytesDataTypes.INT.getLength() + lengthLogin +
                LengthBytesDataTypes.INT.getLength() + lengthPassword);

        buf.writeByte(SignalBytes.REQUEST_AUTHORIZE.getSignalByte());
        buf.writeInt(lengthLogin);
        buf.writeBytes(bytesLogin);
        buf.writeInt(lengthPassword);
        buf.writeBytes(bytesPassword);

        ctx.writeAndFlush(buf);
    }

    private void sendRequestFile(ChannelHandlerContext ctx, RequestFile req) {
        String filename = req.getFileName();
        byte[] fileNameBytes = filename.getBytes(StandardCharsets.UTF_8);
        int lengthFilename = fileNameBytes.length;
        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(LengthBytesDataTypes.SIGNAL_BYTE.getLength() +
                LengthBytesDataTypes.INT.getLength() + lengthFilename);

        buf.writeByte(SignalBytes.REQUEST_FILE.getSignalByte());
        buf.writeInt(lengthFilename);
        buf.writeBytes(fileNameBytes);

        ctx.writeAndFlush(buf);
    }

    private void sendResponseGetStatus(ChannelHandlerContext ctx, ResponseStatusComplete resp) {
        if (resp.isStatus()) {
            ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(LengthBytesDataTypes.SIGNAL_BYTE.getLength());
            buf.writeByte(SignalBytes.RECEIVED_SUCCESS_FILE.getSignalByte());
            ctx.writeAndFlush(buf);
        }
    }

    private void sendRequestList(ChannelHandlerContext ctx) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1);
        buf.writeByte(SignalBytes.REQUEST_LIST.getSignalByte());
        ctx.writeAndFlush(buf);
    }

    private void sendFile(ChannelHandlerContext ctx, SendFile file) throws IOException {
        FileSender.sendFile(
                file.getFilePath(),
                ctx,
                file.getChannelFutureListener(),
                file.getLogger()
        );
    }
}
