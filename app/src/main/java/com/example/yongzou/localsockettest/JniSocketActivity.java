package com.example.yongzou.localsockettest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.yongzou.localsockettest.jniutil.JniLocalSocket;
import com.example.yongzou.localsockettest.socket.LocalServerSocketManager;
import com.example.yongzou.localsockettest.socket.LocalSocketClient;

/**
 * Created by yong.zou on 2016/12/9.
 */

public class JniSocketActivity extends AppCompatActivity {
    private LocalSocketClient client;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        client = new LocalSocketClient();
        int socketId = new JniLocalSocket().createSocket();
        Log.d("Local","Jni SocketClient Create"+socketId);
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText("JniSocketActivity");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                client.sendJson("client by "+client.socketId+" content:sdhusdaiufiuhds");
                LocalServerSocketManager.getInstance().sendJson("看哈防丢哈萨克\\0Iuhfiuhasdifuhasiuhdf");
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            LocalServerSocketManager.getInstance().closeAll();
//            LocalServerSocketManager.getInstance().sendXml("Iuhfiuhasdifuhasiuhdf");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
