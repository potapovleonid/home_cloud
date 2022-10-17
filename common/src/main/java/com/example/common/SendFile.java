package com.example.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SendFile extends SendObject{

    private String filename;
    private byte[] byteBuf;

    public SendFile(String filename, Path path) {
        this.filename = filename;

        try {
            this.byteBuf = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getByteBuf() {
        return byteBuf;
    }

    public void setByteBuf(byte[] byteBuf) {
        this.byteBuf = byteBuf;
    }
}
