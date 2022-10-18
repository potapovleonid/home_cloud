package com.example.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SendMessageFile {

    private Logger logger = Logger.getLogger(SendMessageFile.class);

    public SendMessageFile() {
        BasicConfigurator.configure();
    }

    public void send(Path path){
        try(Socket socket = new Socket("localhost", 8189);) {
            ObjectEncoderOutputStream eout = new ObjectEncoderOutputStream(socket.getOutputStream());
            ObjectDecoderInputStream oin = new ObjectDecoderInputStream(socket.getInputStream());
            com.example.common.SendFile sf = new com.example.common.SendFile("1.txt", path);
            eout.writeObject(sf);
            eout.flush();
            Object obj = oin.readObject();
            String sendResult = (String) obj;
            System.out.println(sendResult);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SendMessageFile smf = new SendMessageFile();
        Path path = Paths.get("client_files", "1.txt");
        smf.send(path);
    }

}
