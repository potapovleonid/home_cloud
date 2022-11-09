package com.example.client;

import com.example.common.FileSender;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class ClientApp {

    public static void main(String[] args) {
        try {
            CountDownLatch countDownNetworkConnections = new CountDownLatch(1);
            new Thread(() -> Network.getNetwork().start(countDownNetworkConnections)).start();
            countDownNetworkConnections.await();

            AuthSender.sendAuthRequest(Network.getNetwork().getChannel(), "des", "des123");

            while (Network.getNetwork().getChannel().pipeline().toMap().size() == 2){
                Thread.sleep(500);
            }

            sendFile();

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFile() throws IOException {
        FileSender.sendFile(
                Paths.get("client_files" + FileSystems.getDefault().getSeparator() + "1.mp4"),
                Network.getNetwork().getChannel(),
                finishListener -> {
                    if (!finishListener.isSuccess()){
                        LoggerApp.info(finishListener.cause().getMessage());
                    }
                    if (finishListener.isSuccess()){
                        LoggerApp.info("Send file is completed");
                    }
                }, LoggerApp.getLogger());
    }
}
