package com.example.client;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class ClientApp {

    public static void main(String[] args) {
        LoggerApp.init();

        try {
            CountDownLatch countDownNetworkConnections = new CountDownLatch(1);
            new Thread(() -> Network.getNetwork().start(countDownNetworkConnections)).start();
            countDownNetworkConnections.await();

            FileSender.sendFile(
                Paths.get("client_files" + FileSystems.getDefault().getSeparator() + "1.mp4"),
                Network.getNetwork().getChannel(),
                finishListener -> {
                    if (!finishListener.isSuccess()){
                        LoggerApp.addInfo(finishListener.cause().getMessage());
                    }
                    if (finishListener.isSuccess()){
                        LoggerApp.addInfo("Send file is completed");
                    }
                });

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
