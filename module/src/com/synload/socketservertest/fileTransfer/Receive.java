package com.synload.socketservertest.fileTransfer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.synload.socketframework.annotations.Event;
import com.synload.socketframework.encoding.Transmission;
import com.synload.socketframework.events.CommandEvent;
import com.synload.socketservertest.TestModule;

public class Receive {
    @Event(description="Receive file from test client!")
    public void getFile(CommandEvent e) {
        try {
            if (e.getTransmission().getCommand().length == 3) {
                /*
                 * Receive byte transfer
                 */
                if (e.getTransmission().getCommand()[0] == 0x01
                        && e.getTransmission().getCommand()[1] == 0x01
                        && e.getTransmission().getCommand()[2] == 0x01) {
                    ByteArrayInputStream btol = new ByteArrayInputStream(
                            (byte[]) e.getTransmission().getValue());
                    DataInputStream dis = new DataInputStream(btol);
                    long fileLength = dis.readLong();
                    dis.close();
                    btol.close();
                    if (fileLength > 0) {
                        String fileName = "uploads/"
                                + TestModule.randomString(25);
                        OutputStream os = new FileOutputStream(new File(
                                fileName));
                        byte[] buffer = new byte[8 * 1024];
                        int readData = 0;
                        long leftToRead = fileLength;
                        InputStream is = e.getClient().getIs();
                        while ((readData = is.read(buffer)) != -1) {
                            leftToRead -= readData;
                            os.write(Arrays.copyOf(buffer, readData));
                            if (leftToRead == 0) {
                                break;
                            }
                        }
                        os.close();
                        e.getClient()
                                .getOs()
                                .write(Transmission.encodeMessage(new byte[] {
                                        0x01, 0x01, 0x01 }, fileName.getBytes()));
                        e.getClient().getOs().flush();
                    }
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Event(description="Send file size from requested file!")
    public void getSize(CommandEvent e) {
        try {
            if (e.getTransmission().getCommand().length == 3) {
                /*
                 * Send File Size
                 */
                if (e.getTransmission().getCommand()[0] == 0x01
                        && e.getTransmission().getCommand()[1] == 0x01
                        && e.getTransmission().getCommand()[2] == 0x02) {
                    String fileName = new String((byte[]) e.getTransmission()
                            .getValue());

                    ByteArrayOutputStream ltob = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(ltob);
                    dos.writeLong((new File(fileName)).length());
                    byte[] commandEncode = Transmission
                            .encodeMessage(new byte[] { 0x01, 0x01, 0x02 },
                                    ltob.toByteArray());
                    ltob.close();
                    dos.close();

                    e.getClient().getOs().write(commandEncode);
                    e.getClient().getOs().flush();
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
