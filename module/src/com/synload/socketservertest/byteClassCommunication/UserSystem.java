package com.synload.socketservertest.byteClassCommunication;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.synload.socketframework.annotations.Event;
import com.synload.socketframework.encoding.Transmission;
import com.synload.socketframework.events.CommandEvent;
import com.synload.socketframework.server.Utils;

public class UserSystem {
    
    public static List<User> users = new ArrayList<User>();
    
    public User get(String userName){
        for(User u: users){
            if(u.getName().equalsIgnoreCase(userName)){
                return u;
            }
        }
        return null;
    }
    
    @Event(description="Send user class object")
    public void sendUserRequest(CommandEvent e) {
        try {
            if (e.getTransmission().getCommand().length == 3) {
                if (e.getTransmission().getCommand()[0] == 0x01
                        && e.getTransmission().getCommand()[1] == 0x01
                        && e.getTransmission().getCommand()[2] == 0x03) {
                    String userName = new String( ((byte[])e.getTransmission().getValue()) );
                    
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutput out = new ObjectOutputStream(bos);
                    out.writeObject(this.get(userName));
                    byte[] classBytes = bos.toByteArray();
                    out.close();
                    bos.close();
                    bos = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(bos);
                    out.writeLong(classBytes.length);
                    byte[] length = bos.toByteArray();
                    out.close();
                    bos.close();
                    //classBytes.length
                    e.getClient()
                        .getOs()
                        .write(Transmission.encodeMessage(new byte[]{0x01,0x01,0x03},length));
                    e.getClient().getOs().flush();
                    e.getClient().getOs().write(classBytes);
                    e.getClient().getOs().flush();
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    
    @Event(description="Receive user class object")
    public void receiveUser(CommandEvent e) {
        try {
            if (e.getTransmission().getCommand().length == 3) {
                if (e.getTransmission().getCommand()[0] == 0x01
                        && e.getTransmission().getCommand()[1] == 0x01
                        && e.getTransmission().getCommand()[2] == 0x04) {
                    ByteArrayInputStream btol = new ByteArrayInputStream(
                            (byte[]) e.getTransmission().getValue());
                    DataInputStream dis = new DataInputStream(btol);
                    long classSize = dis.readLong();
                    dis.close();
                    btol.close();
                    byte[] buffer = new byte[8 * 1024];
                    int readData = 0;
                    long leftToRead = classSize;
                    byte[] classBytes = new byte[]{};
                    InputStream is = e.getClient().getIs();
                    while ((readData = is.read(buffer)) != -1) {
                        leftToRead -= readData;
                        Utils.combine( classBytes, Arrays.copyOf(buffer, readData));
                        if (leftToRead == 0) {
                            break;
                        }
                    }
                    ByteArrayInputStream bis = new ByteArrayInputStream(classBytes);
                    ObjectInput in = new ObjectInputStream(bis);
                    User readUserData = (User)in.readObject();
                    users.add(readUserData);
                    System.out.println("Received User Object: "+readUserData.getName());
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
