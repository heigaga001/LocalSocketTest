package com.example.yongzou.localsockettest.socket;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.text.TextUtils;
import android.util.Log;

import com.example.yongzou.localsockettest.socket.model.PacketData;
import com.example.yongzou.localsockettest.socket.util.DataUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by yong.zou on 2016/12/9.
 */

public class LocalSocketClient {
    private String TAG = "LocalSocketClient";
    LocalSocket localSocket;
    public static int id = -1;
    public String socketId;
    private OutputStream os;
    private InputStream is;
    private Thread receiveThread;
    private Thread sendThread;
    private boolean isRuning;
    private List<PacketData> sendData;



    public LocalSocketClient() {
        localSocket = new LocalSocket();
        socketId = getSocketId();
        sendData = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                LocalSocketAddress localSocketAddress = new LocalSocketAddress(LocalServerSocketManager.SERVICENAME);
                try {
                    localSocket.connect(localSocketAddress);
                    isRuning = true;
                    is = localSocket.getInputStream();
                    os = localSocket.getOutputStream();
                    sendData.add(new PacketData().setType(LocalSocketConst.TYPE_LOGIN).setContent(socketId));
                    createReceiveThread();
                    createSendThread();
                } catch (IOException e) {
                    e.printStackTrace();
                    close();
                }
            }
        }.start();

    }

    private  void createReceiveThread() {
        receiveThread = new Thread() {
            @Override
            public void run() {

                while (isRuning) {

                    try {
                        PacketData receive = PacketData.readPacketData(is);
                        if(receive.type == LocalSocketConst.TYPE_CLOSE){
                            Log.d("LocalClient","receive type close");
                            close();
                        }else{


//                                sendJson("service of " + receive.content);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        close();
                    }
                }
            }
        };
        receiveThread.start();
    }

    private void close() {
        isRuning = false;
        if(os!=null){
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(is!=null){
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(localSocket!=null){
            try {
                localSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        is = null;
        os = null;
        localSocket = null;
    }

    private void createSendThread() {
        sendThread = new Thread() {
            @Override
            public void run() {
                while (isRuning) {
                    try {
                        if (sendData.size() > 0) {
                            PacketData data = sendData.remove(0);
                            byte[] datas = new byte[1];
                            datas[0] = data.type;
                            os.write(datas);
                            if (data.type != LocalSocketConst.TYPE_CLOSE) {
                                os.write(data.getContent());
                            }
                            os.flush();
                        }
                        if(sendData.size()==0){
                            try {
                                sleep(50000);
                            } catch (InterruptedException e) {
//                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        sendThread.start();
    }

    public void sendJson(String json){
        if(TextUtils.isEmpty(json)){
            return;
        }
        sendData.add(new PacketData().setType(LocalSocketConst.TYPE_CONTENT_JSON).setContent(json));
        if(sendData.size()==1){
            sendThread.interrupt();
        }
    }

    public void sendXml(String xml){
        if(TextUtils.isEmpty(xml)){
            return;
        }
        sendData.add(new PacketData().setType(LocalSocketConst.TYPE_CONTENT_XML).setContent(xml));
        if(sendData.size()==1){
            sendThread.interrupt();
        }
    }

    private static String getSocketId() {
        if (id < 0) {
            id = new Random().nextInt(10000);
        }
        id++;
        return "local_client_" + id;
    }




}
