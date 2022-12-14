package com.example.client.network.networking;

public class ResponseStatusComplete implements Networking {

    private final boolean status;

    public ResponseStatusComplete(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}
