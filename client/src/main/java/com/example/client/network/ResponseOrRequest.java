package com.example.client.network;

public abstract class ResponseOrRequest {

    private final MsgType type;

    public ResponseOrRequest(MsgType type) {
        this.type = type;
    }

    public MsgType getType() {
        return type;
    }
}
