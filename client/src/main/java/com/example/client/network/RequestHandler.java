package com.example.client.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class RequestHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object obj, ChannelPromise promise) throws Exception {
        if (obj instanceof Request){
            Request req = (Request) obj;
            if (req.getType() == RequestType.UPLOAD){
//                TODO send req
            }
        }
    }

}
