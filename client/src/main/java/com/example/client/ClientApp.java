package com.example.client;

import com.example.client.network.Network;
import java.util.concurrent.CountDownLatch;

public class ClientApp {

    public static void main(String[] args) {
        FXController fxController = new FXController();

        try {
            CountDownLatch countDownNetworkConnections = new CountDownLatch(1);
            new Thread(() -> Network.getNetwork().start(countDownNetworkConnections)).start();
            countDownNetworkConnections.await();

            fxController.startFX();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
