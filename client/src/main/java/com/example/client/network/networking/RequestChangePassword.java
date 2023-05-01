package com.example.client.network.networking;

public class RequestChangePassword implements Networking {

    private byte[] bytesOldPassword;
    private byte[] bytesNewPassword;

    public RequestChangePassword(String oldPassword, String newPassword) {
        this.bytesOldPassword = MD5HashPassword.getBytesHashPassword(oldPassword);
        this.bytesNewPassword = MD5HashPassword.getBytesHashPassword(newPassword);
    }

    public byte[] getBytesOldPassword() {
        return bytesOldPassword;
    }

    public byte[] getBytesNewPassword() {
        return bytesNewPassword;
    }
}
