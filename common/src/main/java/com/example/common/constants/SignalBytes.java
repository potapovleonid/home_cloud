package com.example.common.constants;

public enum SignalBytes {
    FILE_REQUEST((byte) 10),
    FILE_SENDING((byte) 11),
    FILE_RECEIVED_SUCCESS((byte) 12),
    FILE_DELETE_REQUEST((byte) 13),
    FILE_DELETE_SUCCESS((byte) 14),
    FILE_DELETE_FAILED((byte) 15),

    LIST_REQUEST((byte) 20),
    LIST_SENDING((byte) 21),

    AUTHORIZE_REQUEST((byte) 30),
    AUTHORIZE_SUCCESS((byte) 31),
    AUTHORIZE_FAILED((byte) 32),

    REGISTER_NEW_USER_REQUEST((byte) 50),
    REGISTER_USER_SUCCESS((byte) 51),
    REGISTER_USER_FAILED((byte) 52),

    CHANGE_PASSWORD_REQUEST((byte) -120),
    CHANGE_PASSWORD_SUCCESS((byte) -121),
    CHANGE_PASSWORD_FAILED((byte) -122)
    ;

    private final byte signalByte;

    SignalBytes(byte signalByte) {
        this.signalByte = signalByte;
    }

    public byte getSignalByte() {
        return signalByte;
    }
}
