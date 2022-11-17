package com.example.client;

import java.nio.file.Path;

public interface CallbackUploadAndDownload {

    void download(Path path);
    void upload(Path path);

}
