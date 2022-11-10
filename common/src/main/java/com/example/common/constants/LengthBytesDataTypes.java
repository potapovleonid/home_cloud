package com.example.common.constants;

public enum LengthBytesDataTypes {
    SIGNAL_BYTE(1),
    INT(4),
    LONG(8);

    private int length;

    LengthBytesDataTypes(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }
}
