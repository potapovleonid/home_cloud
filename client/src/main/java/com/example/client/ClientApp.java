package com.example.client;

import com.example.client.network.AuthorizeSender;
import com.example.client.network.Network;
import com.example.common.network.FileSender;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class ClientApp{

    public static void main(String[] args) {
        try {
            CountDownLatch countDownNetworkConnections = new CountDownLatch(1);
            new Thread(() -> Network.getNetwork().start(countDownNetworkConnections,
            resultAuth -> {
                if (resultAuth){
                    LoggerApp.info("Auth success, delete auth pipeline");
                    sendFile("1.mp4");
                    sendFile("2.mp4");
                } else {
                    LoggerApp.info("Please try authenticate again");
                }
            })).start();
            countDownNetworkConnections.await();

            AuthorizeSender.sendAuthRequest(Network.getNetwork().getChannel(), "des", "des123");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(String filename) throws IOException {
        FileSender.sendFile(
                Paths.get("client_files" + FileSystems.getDefault().getSeparator() + filename),
                Network.getNetwork().getChannel(),
                finishListener -> {
                    if (!finishListener.isSuccess()){
                        LoggerApp.info(finishListener.cause().getMessage());
                    }
                    if (finishListener.isSuccess()){
                        LoggerApp.info("Send file is completed");
                    }
                },
                LoggerApp.getLogger(),
                resultDownload -> {
                    if (resultDownload){
                        JOptionPane.showMessageDialog(null, "File's success downloaded");
                    } else {
                        JOptionPane.showMessageDialog(null, "File's fail downloaded");
                    }
                });
    }
}
