package com.example.client.network;

public class Request {

    private RequestType type;
    private String filename;

    public Request(RequestType type, String filename) {
        this.type = type;
        this.filename = filename;
    }

    public RequestType getType() {
        return type;
    }

    public String getFilename() {
        return filename;
    }
}
