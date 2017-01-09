package com.example.yongzou.localsockettest.jniutil;

/**
 * Created by yong.zou on 2016/12/8.
 */

public class JniLocalSocket {
    static {
        System.loadLibrary("native-lib");
    }
    public native int createSocket();
    public native int logT();

    public native static void logTest();
}
