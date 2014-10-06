package com.synload.socketframework.encoding;

public class TransmissionMessage {
    public TransmissionMessage(byte[] command, Object value) {
        this.command = command;
        this.value = value;
    }

    private Object value;
    private byte[] command;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public byte[] getCommand() {
        return command;
    }

    public void setCommand(byte[] command) {
        this.command = command;
    }
}
