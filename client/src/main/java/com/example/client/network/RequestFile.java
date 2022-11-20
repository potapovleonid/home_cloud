package com.example.client.network;

public class RequestFile extends ResponseOrRequest{

    private final String fileName;

    public RequestFile(MsgType type, String fileName) {
        super(type);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
