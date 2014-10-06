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

    }

    @Override
    public void close(Client c) {
        // TODO Auto-generated method stub

    }

    @Override
    public void open(Client c) {
        // TODO Auto-generated method stub

    }

    public static String randomString(int length) {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(length);
    }
}
