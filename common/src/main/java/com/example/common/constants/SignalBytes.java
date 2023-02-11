package com.example.common.constants;

public enum SignalBytes {
    REQUEST_FILE((byte) 10),
    SENDING_FILE((byte) 11),
    RECEIVED_SUCCESS_FILE((byte) 12),

    REQUEST_LIST((byte) 20),
    SENDING_LIST((byte) 21),

    REQUEST_AUTHORIZE((byte) 30),
    SUCCESS_AUTH((byte) 31),
    FAILED_AUTH((byte) 32),

    REQUEST_REGISTER_NEW_USER((byte) 50),
    SUCCESS_REGISTER_USER((byte) 51),
    FAILED_REGISTER_USER((byte) 52),

    REQUEST_CHANGE_PASSWORD((byte) -120);

    private final byte signalByte;

    SignalBytes(byte signalByte) {
        this.signalByte = signalByte;
    }

    public byte getSignalByte() {
        return signalByte;
    }
}
