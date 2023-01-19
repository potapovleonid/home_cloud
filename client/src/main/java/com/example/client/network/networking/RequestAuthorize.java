package com.example.client.network.networking;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RequestAuthorize implements Networking {

    private String login;
    private byte[] password;

    public RequestAuthorize(String login, String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        this.login = login;
        assert md != null;
        this.password = md.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    public String getLogin() {
        return login;
    }

    public byte[] getPassword() {
        return password;
    }
}
