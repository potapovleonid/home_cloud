package com.example.client.network;

public class ResponseStatusComplete extends ResponseOrRequest {

    private final boolean status;

    public ResponseStatusComplete(MsgType type, boolean status) {
        super(type);
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}
