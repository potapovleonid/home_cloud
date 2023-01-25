package com.example.client.network.networking;

public class RequestRegisterUser implements Networking{
    private String login;
    private byte[] password;

    public RequestRegisterUser(String login, String password) {
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
