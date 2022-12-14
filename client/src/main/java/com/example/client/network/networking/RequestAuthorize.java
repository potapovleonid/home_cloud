package com.example.client.network.networking;

public class RequestAuthorize implements Networking {

    private String login;
    private String password;

    public RequestAuthorize(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
