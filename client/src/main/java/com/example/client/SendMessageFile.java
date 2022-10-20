package com.example.client;

import com.example.common.SendFile;
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

    public static void main(String[] args) {
        SendMessageFile smf = new SendMessageFile();
        Path path = Paths.get("client_files", "1.txt");
        smf.send("1.txt", path);
    }

    public void send(String filename, Path path){
        try(Socket socket = new Socket("localhost", 8189);) {
            ObjectEncoderOutputStream out = new ObjectEncoderOutputStream(socket.getOutputStream());
            ObjectDecoderInputStream in = new ObjectDecoderInputStream(socket.getInputStream());
            SendFile sf = new SendFile(filename, path);
            out.writeObject(sf);
            out.flush();
            Object obj = in.readObject();
            String sendResult = (String) obj;
            System.out.println(sendResult);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
