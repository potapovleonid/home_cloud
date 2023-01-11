package com.example.common.constants;

public enum SignalBytes {
    SENDING_FILE((byte) 25),
    SENDING_LIST((byte) 23),
    REQUEST_LIST((byte) 115),
    REQUEST_FILE((byte) 105),
    REQUEST_REGISTER_NEW_USER((byte) 111),
    REQUEST_AUTHORIZE((byte) 107),
    RECEIVED_SUCCESS_FILE((byte) 17),
    SUCCESS_AUTH((byte) 127),
    SUCCESS_REGISTER_USER((byte) 126),
    FAILED_REGISTER_USER((byte) -125),
    FAILED_AUTH((byte) -128);

    private final byte signalByte;

    SignalBytes(byte signalByte) {
        this.signalByte = signalByte;
    }

    public byte getSignalByte() {
        return signalByte;
    }
}
