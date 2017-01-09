package com.example.yongzou.localsockettest.socket;

import android.content.Context;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.text.TextUtils;
import android.util.Log;

import com.example.yongzou.localsockettest.socket.model.PacketData;
import com.example.yongzou.localsockettest.socket.util.DataUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yong.zou on 2016/12/9.
 */

public class LocalServerSocketManager {

    private String TAG = "LocalServer";

    private static LocalServerSocketManager instance;

    public static final String SERVICENAME = "test_server_name";

    private LocalServerSocket serverSocket;

    private boolean isRuning = false;

    private Map<String,SocketManager> managers;

    private List<SocketManager> list;

    public static LocalServerSocketManager getInstance() {
        if (instance == null) {
            instance = new LocalServerSocketManager();
        }
        return instance;
    }

    private LocalServerSocketManager() {
        managers = new HashMap<>();
        list = new ArrayList<>();
        new Thread(){
            @Override
            public void run() {
                try {
                    isRuning = true;
                    serverSocket = new LocalServerSocket(SERVICENAME);

                } catch (IOException e) {
                    e.printStackTrace();
                    isRuning = false;
                }

                while (isRuning){
                    Log.d(TAG, "wait for new client coming !");
                    try {
                        LocalSocket interactClientSocket = serverSocket.accept();
                        Log.d(TAG, "accept one socket");
                        list.add(new SocketManager(interactClientSocket));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void addSocketManager(String name,SocketManager socketManager){
        SocketManager last = managers.get(name);
        if(last!=null&&last!=socketManager){
            last.sendClose();
        }
        managers.put(name,socketManager);
    }

    public void sendJson(String json){
//       Set<String> keys = managers.keySet();
//        for(String key:keys){
//            SocketManager socketManager = managers.get(key);
//            socketManager.sendJson(json);
//        }
        for(SocketManager socketManager:list){
            socketManager.sendJson(json);
        }
    }

    public void closeAll(){
        Set<String> keys = managers.keySet();
        for(String key:keys){
            SocketManager socketManager = managers.get(key);
            socketManager.sendClose();
            list.remove(socketManager);
        }
        managers.clear();
        for(SocketManager socketManager:list){
            socketManager.sendClose();
        }
        list.clear();
    }

    public void sendXml(String xml){
       Set<String> keys = managers.keySet();
        for(String key:keys){
            SocketManager socketManager = managers.get(key);
            socketManager.sendXml(xml);
        }
    }

    private class SocketManager{
        LocalSocket localSocket;
        private OutputStream os;
        private InputStream is;
        private Thread receiveThread;
        private Thread sendThread;
        private boolean isRuning;
        private List<PacketData> sendData;
        private String from;

        public SocketManager(LocalSocket localSocket) {
            this.localSocket = localSocket;
            try {
                isRuning = true;
                sendData = new ArrayList<>();
                is = localSocket.getInputStream();
                os = localSocket.getOutputStream();
                createReceiveThread();
                createSendThread();
            } catch (IOException e) {
                e.printStackTrace();
                isRuning = false;
            }

        }
        private void createReceiveThread() {
            receiveThread = new Thread() {
                @Override
                public void run() {

                    while (isRuning) {

                        try {
                            PacketData receive = PacketData.readPacketData(is);
                            if (receive.type == LocalSocketConst.TYPE_LOGIN) {
                                from = receive.content;
                                addSocketManager(receive.content, SocketManager.this);
                            } else if(receive.type == LocalSocketConst.TYPE_CLOSE){
                                close();
                            }else{
//                                sendJson("service of " + receive.content);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            receiveThread.start();
        }

        private void close() {
            if(TextUtils.isEmpty(from)){
                managers.remove(from);
            }
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
                                    e.printStackTrace();
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
            sendPacket(new PacketData().setType(LocalSocketConst.TYPE_CONTENT_JSON).setContent(json));
        }

        public void sendXml(String xml){
            if(TextUtils.isEmpty(xml)){
                return;
            }
            sendPacket(new PacketData().setType(LocalSocketConst.TYPE_CONTENT_XML).setContent(xml));
        }

        public void sendClose(){
            sendPacket(new PacketData().setType(LocalSocketConst.TYPE_CLOSE));
//            close();
        }

        public void sendPacket(PacketData data){
            sendData.add(data);
            if(sendData.size()==1){
                sendThread.interrupt();
            }
        }

    }

}
