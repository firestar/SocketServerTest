package com.synload.socketservertest;

import java.math.BigInteger;
import java.security.SecureRandom;

import com.synload.socketframework.annotations.Module;
import com.synload.socketframework.annotations.Module.LogLevel;
import com.synload.socketframework.module.ModuleClass;
import com.synload.socketframework.server.Client;

@Module(name = "TestModule", author = "Nathaniel Davidson", log = LogLevel.INFO)
public class TestModule extends ModuleClass {
    
    @Override
    public void initialize() {
           /*
            * THIS MOD MAPS 
            * 1,1,1 - Receive file byte data from stream (includes file length)
            * 1,1,2 - Send file size (includes file length)
            * 1,1,3 - Send user data (includes reference )
            * 1,1,4 - Receive user info (includes length of byte data)
            * */
    }

    @Override
    public void close(Client c) {
        System.out.println(c.getSocket().getLocalAddress().getHostAddress());
    }

    @Override
    public void open(Client c) {
        System.out.println(c.getSocket().getLocalAddress().getHostAddress());
    }

    public static String randomString(int length) {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(length);
    }
}
