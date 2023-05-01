package com.example.client.network.networking;

public class RequestDeleteFile implements Networking {

    private String filename;

    public RequestDeleteFile(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
