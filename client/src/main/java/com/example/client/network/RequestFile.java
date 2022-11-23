package com.example.client.network;

public class RequestFile extends ResponseOrRequest{

    private final String fileName;

    public RequestFile(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
