package com.example.client.network.networking;

public class RequestChangePassword implements Networking {

    private String oldPassword;
    private String newPassword;

    public RequestChangePassword(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
