package com.synload.socketframework.encoding;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

public class Transmission {
    public static byte[] encodeMessage(byte[] command, byte[] data) {
        byte[] combined = new byte[command.length + data.length];
        System.arraycopy(command, 0, combined, 0, command.length);
        System.arraycopy(data, 0, combined, command.length, data.length);
        return combined;
    }

    public static TransmissionMessage decodeStringMessage(byte[] data)
            throws IOException {
        TransmissionMessage m = new TransmissionMessage(Arrays.copyOfRange(
                data, 0, 3), new String(
                Arrays.copyOfRange(data, 3, data.length)));
        return m;
    }

    public static TransmissionMessage decodeLongMessage(byte[] data)
            throws IOException {
        ByteArrayInputStream btol = new ByteArrayInputStream(
                Arrays.copyOfRange(data, 3, data.length));
        DataInputStream dis = new DataInputStream(btol);
        long i = dis.readLong();
        dis.close();
        btol.close();
        TransmissionMessage m = new TransmissionMessage(Arrays.copyOfRange(
                data, 0, 3), i);
        return m;
    }

    public static TransmissionMessage decodeMessage(byte[] data)
            throws IOException {
        TransmissionMessage m = new TransmissionMessage(Arrays.copyOfRange(
                data, 0, 3), Arrays.copyOfRange(data, 3, data.length));
        return m;
    }
}
