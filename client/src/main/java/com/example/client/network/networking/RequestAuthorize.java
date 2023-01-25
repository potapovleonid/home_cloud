package com.example.client.network.networking;

public class RequestAuthorize implements Networking {

    private String login;
    private byte[] password;

    public RequestAuthorize(String login, String password) {
        this.login = login;
        this.password = MD5HashPassword.getBytesHashPassword(password);
    }

    public String getLogin() {
        return login;
    }

    public byte[] getPassword() {
        return password;
    }
}
