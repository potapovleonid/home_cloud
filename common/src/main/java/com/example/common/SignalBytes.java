package com.example.common;

public enum SignalBytes {
    SENDING_FILE ((byte) 25),
    SENDING_LIST((byte) 23),
    RECEIVED_SUCCESS_FILE ((byte) 17);

    private final byte signalByte;

    SignalBytes(byte signalByte) {
        this.signalByte = signalByte;
    }

    public byte getSignalByte() {
        return signalByte;
    }
}
