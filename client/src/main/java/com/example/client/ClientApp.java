package com.example.client;

import com.example.client.network.AuthorizeSender;
import com.example.client.network.Network;
import com.example.common.network.FileSender;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

public class ClientApp{

    private static final MainFXApp mainFXApp = new MainFXApp();

    public static void main(String[] args) {
        try {
            CountDownLatch countDownNetworkConnections = new CountDownLatch(1);
            new Thread(() -> Network.getNetwork().start(countDownNetworkConnections,
            resultAuth -> {
                if (resultAuth){
                    LoggerApp.info("Auth success, delete auth pipeline");
                    mainFXApp.startMainPanel();
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
}
