package com.example.client.network.handlers;

import com.example.client.network.networking.*;
import com.example.common.constants.LengthBytesDataTypes;
import com.example.common.constants.SignalBytes;
import com.example.common.network.FileSender;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;

import java.nio.charset.StandardCharsets;

public class OutboundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object obj, ChannelPromise promise) throws Exception {
        if (obj instanceof Networking) {
            if (obj instanceof RequestAuthorize){
                RequestAuthorize req = (RequestAuthorize) obj;
                int lengthLogin = req.getLogin().getBytes().length;
                byte[] bytesLogin = req.getLogin().getBytes();
                int lengthPassword = req.getPassword().getBytes().length;
                byte[] bytesPassword = req.getPassword().getBytes();

                ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(4 + lengthLogin + 4 + lengthPassword);
                buf.writeInt(lengthLogin);
                buf.writeBytes(bytesLogin);
                buf.writeInt(lengthPassword);
                buf.writeBytes(bytesPassword);

                ctx.writeAndFlush(buf);
                return;
            }
            if (obj instanceof RequestFile) {
                RequestFile req = (RequestFile) obj;
                String filename = req.getFileName();
                byte[] fileNameBytes = filename.getBytes(StandardCharsets.UTF_8);
                int lengthFilename = fileNameBytes.length;
                ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(LengthBytesDataTypes.SIGNAL_BYTE.getLength() +
                        LengthBytesDataTypes.INT.getLength() + lengthFilename);

                buf.writeByte(SignalBytes.REQUEST_FILE.getSignalByte());
                buf.writeInt(lengthFilename);
                buf.writeBytes(fileNameBytes);

                ctx.writeAndFlush(buf);
                return;
            }
            if (obj instanceof ResponseStatusComplete) {
                ResponseStatusComplete resp = (ResponseStatusComplete) obj;
                if (resp.isStatus()) {
                    ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(LengthBytesDataTypes.SIGNAL_BYTE.getLength());
                    buf.writeByte(SignalBytes.RECEIVED_SUCCESS_FILE.getSignalByte());
                    ctx.writeAndFlush(buf);
                }
                return;
            }
            if (obj instanceof RequestList){
                ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1);
                buf.writeByte(SignalBytes.REQUEST_LIST.getSignalByte());
                ctx.writeAndFlush(buf);
                return;
            }
            if (obj instanceof SendFile){
                SendFile file = (SendFile) obj;
                FileSender.sendFile(
                        file.getFilePath(),
                        file.getChannel(),
                        file.getChannelFutureListener(),
                        file.getLogger()
                        );
                return;
            }
            throw new IllegalArgumentException("Unknown outbound command");
        }
    }
}
