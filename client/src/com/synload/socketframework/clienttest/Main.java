package com.synload.socketframework.clienttest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.synload.socketframework.encoding.Transmission;
import com.synload.socketframework.encoding.TransmissionMessage;

public class Main {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("synload.com", 2006);
            s.setTcpNoDelay(true);

            // What do to with the transmission!
            String fileName = sendFile(s, "./webcam-toy-photo1.jpg");
            System.out.println("Sent: " + fileName);
            // Done with communication
            
            String userName = "jim";
            User admin = new User(userName,"","",true);
            Main.sendUser(s, admin);
            User rU = Main.receiveUser(s, userName);
            s.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static User receiveUser(Socket s, String name) throws IOException, ClassNotFoundException{
        User n = null;
        Transmission.encodeMessage(new byte[]{0x01,0x01,0x03}, name.getBytes());
        byte[] buffer = new byte[8 * 1024];
        int readData = 0;
        long userLength = 0;
        while ((readData = s.getInputStream().read(buffer)) != -1) {
            byte[] data = Arrays.copyOf(buffer, readData);
            TransmissionMessage m = Transmission.decodeStringMessage(data);
            if (m.getCommand()[0] == 0x01 && m.getCommand()[1] == 0x01
                    && m.getCommand()[2] == 0x03) {
                userLength = decodeLong((byte[])m.getValue());
                break;
            }
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if(userLength>0){
            long leftToRead = userLength;
            InputStream is = s.getInputStream();
            while ((readData = is.read(buffer)) != -1) {
                leftToRead -= readData;
                bos.write(Arrays.copyOf(buffer, readData));
                if (leftToRead == 0) {
                    break;
                }
            }
            byte[] classBytes= bos.toByteArray();
            n = decodeUser(classBytes);
        }
        return n;
    }
    public static long decodeLong(byte[] data) throws IOException{
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in = new ObjectInputStream(bis);
        long decodedLong = in.readLong();
        in.close();
        bis.close();
        return decodedLong;
    }
    public static byte[] encodeLong(long data) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeLong(data);
        byte[] encodedLong = bos.toByteArray();
        out.close();
        bos.close();
        return encodedLong;
    }
    
    public static void sendUser(Socket s, User u) throws IOException{
        byte[] data = encodeUser(u);
        byte[] encodedMessage = Transmission.encodeMessage(new byte[]{0x01,0x01,0x04}, encodeLong(data.length));
        s.getOutputStream().write(encodedMessage);
        s.getOutputStream().flush();
        s.getOutputStream().write(data);
        s.getOutputStream().flush();
    }
    
    public static byte[] encodeUser(User u) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(u);
        byte[] classBytes = bos.toByteArray();
        out.close();
        bos.close();
        return classBytes;
    }
    
    public static User decodeUser(byte[] data) throws IOException, ClassNotFoundException{
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in = new ObjectInputStream(bis);
        User userInstance = (User)in.readObject();
        in.close();
        bis.close();
        return userInstance;
    }
    
    public static String sendFile(Socket s, String fileName) throws IOException {

        ByteArrayOutputStream ltob = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(ltob);
        dos.writeLong((new File(fileName)).length());
        byte[] commandEncode = Transmission.encodeMessage(new byte[] { 0x01,
                0x01, 0x01 }, ltob.toByteArray());
        ltob.close();
        dos.close();

        s.getOutputStream().write(commandEncode);
        s.getOutputStream().flush();

        InputStream is = new FileInputStream(new File(fileName));
        byte[] buffer = new byte[8 * 1024];
        int readData = 0;
        while ((readData = is.read(buffer)) != -1) {
            s.getOutputStream().write(Arrays.copyOf(buffer, readData));
            s.getOutputStream().flush();
        }
        is.close();
        String i = "";
        while ((readData = s.getInputStream().read(buffer)) != -1) {
            byte[] data = Arrays.copyOf(buffer, readData);
            TransmissionMessage m = Transmission.decodeStringMessage(data);
            if (m.getCommand()[0] == 0x01 && m.getCommand()[1] == 0x01
                    && m.getCommand()[2] == 0x01) {
                i = (String) m.getValue();
                break;
            }
        }

        commandEncode = Transmission.encodeMessage(new byte[] { 0x01, 0x01,
                0x02 }, i.getBytes());

        s.getOutputStream().write(commandEncode);
        s.getOutputStream().flush();

        long filesize = 0;
        while ((readData = s.getInputStream().read(buffer)) != -1) {
            byte[] data = Arrays.copyOf(buffer, readData);
            TransmissionMessage m = Transmission.decodeLongMessage(data);
            if (m.getCommand()[0] == 0x01 && m.getCommand()[1] == 0x01
                    && m.getCommand()[2] == 0x02) {
                filesize = (long) m.getValue();
                break;
            }
        }
        System.out.println("filesize: " + filesize);

        return i;
    }
}
