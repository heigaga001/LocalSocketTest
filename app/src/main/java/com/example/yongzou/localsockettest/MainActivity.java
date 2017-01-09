package com.example.yongzou.localsockettest;

import android.content.Intent;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.yongzou.localsockettest.jniutil.JniLocalSocket;
import com.example.yongzou.localsockettest.socket.LocalServerSocketManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
private String TAG = "MainActivity";
    private ServerSocketThread mServerSocketThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
//       new JniLocalSocket().createSocket();
//        mServerSocketThread = new ServerSocketThread();
//        mServerSocketThread.start();'
        LocalServerSocketManager.getInstance();
        JniLocalSocket.logTest();

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
            startActivity(new Intent(this,JniSocketActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mServerSocketThread!=null){
            mServerSocketThread.stopRun();
        }

    }

    /* 内部类begin */
    private class ServerSocketThread extends Thread
    {
        private boolean keepRunning = true;
        private LocalServerSocket serverSocket;

        private void stopRun()
        {
            keepRunning = false;
        }

        @Override
        public void run()
        {
            try
            {
                serverSocket = new LocalServerSocket("pym_local_socket");
            }
            catch (IOException e)
            {
                e.printStackTrace();

                keepRunning = false;
            }

            while(keepRunning)
            {
                Log.d(TAG, "wait for new client coming !");

                try
                {
                    LocalSocket interactClientSocket = serverSocket.accept();

                    //由于accept()在阻塞时，可能Activity已经finish掉了，所以再次检查keepRunning
                    if (keepRunning)
                    {
                        Log.d(TAG, "new client coming !"+interactClientSocket.getLocalSocketAddress());

                        new InteractClientSocketThread(interactClientSocket).start();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();

                    keepRunning = false;
                }
            }

            if (serverSocket != null)
            {
                try
                {
                    serverSocket.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private class InteractClientSocketThread extends Thread
    {
        private LocalSocket interactClientSocket;

        public InteractClientSocketThread(LocalSocket interactClientSocket)
        {
            this.interactClientSocket = interactClientSocket;
        }

        @Override
        public void run()
        {
            StringBuilder recvStrBuilder = new StringBuilder();
            InputStream inputStream = null;
            try
            {
                inputStream = interactClientSocket.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                char[] buf = new char[4096];
                int readBytes = -1;
                while ((readBytes = inputStreamReader.read(buf)) != -1)
                {
                    String tempStr = new String(buf, 0, readBytes);
                    recvStrBuilder.append(tempStr);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();

                Log.d(TAG, "resolve data error !");
            }
            finally
            {

                if (inputStream != null)
                {
                    try
                    {
                        inputStream.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    /* 内部类end */

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public String stringFromJNI(){
        return "test";
    }
}
