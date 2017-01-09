package com.example.yongzou.localsockettest.socket.model;

import android.text.TextUtils;
import android.util.Log;

import com.example.yongzou.localsockettest.socket.LocalSocketClient;
import com.example.yongzou.localsockettest.socket.LocalSocketConst;
import com.example.yongzou.localsockettest.socket.util.DataUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yong.zou on 2016/12/9.
 */

public class PacketData {
    private static final String TAG = "LocalPacket";
    public byte type;
    public String content;



    public PacketData setType(byte type) {
        this.type = type;
        return this;
    }

    public PacketData setContent(String content) {
        this.content = content;
        return this;
    }

    public static PacketData readPacketData(InputStream is) throws IOException {
        PacketData pdata = new PacketData();
        byte[] data = new byte[1];
        int length = is.read(data);
        if (length > 0) {
            byte type = data[0];
            pdata.setType(type);
            if (type != LocalSocketConst.TYPE_CLOSE) {
                    byte[] sizes = new byte[4];
                    is.read(sizes);
                    int size = DataUtils.bytesToInt(sizes, 0);
                    data = new byte[size];
                    is.read(data);
                    String content = new String(data);
                    pdata.setContent(content);
                    Log.d(TAG, "receive:type:" + type + " content:" + content);
            }
        }
        return pdata;
    }

    public byte[] getContent() {
        if (content == null) {
           content = "";
        }
        byte[] contentBytes = content.getBytes();

        byte[] data = new byte[contentBytes.length + 4];
        byte[] lenbytes = DataUtils.intToBytes(contentBytes.length);
//        byte[] lenStrings = DataUtils.intToBytes(content.length());
        System.arraycopy(lenbytes, 0, data, 0, 4);
//        System.arraycopy(lenStrings, 0, data, 4, 4);
        System.arraycopy(contentBytes, 0, data, 4, contentBytes.length);

//        Log.d("LocalServer",String.format("send length %d:%d:%d:%d:%d:%d:%d",(int)lenbytes[0],(int)lenbytes[1],(int)lenbytes[2],(int)lenbytes[3],content.length(),content.getBytes().length,DataUtils.bytesToInt(lenbytes,0)));
        return data;
    }
}
