package com.example.client;

import com.example.client.network.Network;
import java.util.concurrent.CountDownLatch;

public class ClientApp {

    public static void main(String[] args) throws Exception {
        FXController fxController = new FXController();
        Thread networkThread = null;

        try {
            CountDownLatch countDownNetworkConnections = new CountDownLatch(1);
            networkThread = new Thread(() -> Network.getNetwork().start(countDownNetworkConnections));
            networkThread.start();

            countDownNetworkConnections.await();

            if (networkThread.isInterrupted()){
                System.exit(0);
                return;
            }

            fxController.startFX();
        } catch (InterruptedException e) {
            System.exit(0);
            e.printStackTrace();
        }
    }


}
