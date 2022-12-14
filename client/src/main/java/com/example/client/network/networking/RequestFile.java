package com.example.client.network.networking;

public class RequestFile implements Networking {

    private final String fileName;

    public RequestFile(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
