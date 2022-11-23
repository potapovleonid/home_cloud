package com.example.client.network;

public class ResponseStatusComplete extends ResponseOrRequest {

    private final boolean status;

    public ResponseStatusComplete(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}
