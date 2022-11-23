package com.example.client;

import com.example.client.network.Network;
import com.example.client.network.RequestAuthorize;
import com.example.client.network.RequestFile;

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
//                    Network.getNetwork().getChannel().writeAndFlush(new RequestFile("1.mp4"));
//                    LoggerApp.info("Sent request");
                    mainFXApp.startMainPanel();
                } else {
                    LoggerApp.info("Please try authenticate again");
                }
            })).start();
            countDownNetworkConnections.await();

            Network.getNetwork().getChannel().writeAndFlush(new RequestAuthorize("des", "des123"));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
