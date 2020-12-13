package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Activitygatelock extends AppCompatActivity implements View.OnClickListener {
    private EditText mIp;       //  IP
    private EditText mPort;     //  端口号
    private String mStrIp;      //  字符串类型ip
    private int miPort;        //  字符类型端口
    private Button mBtnConnect; //  连接
    private Socket mSocket;
    private PrintStream out;
    private Button button1,button2;
    private ConnectThread mConnectThread;
    private ImageView iv1;
    private int[] img = {R.drawable.gate, R.drawable.open};//定义一个int数组，用来放图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitygatelock);
        initView();//初始化UI控件
    }

    private void initView() {
        mIp=(EditText)findViewById(R.id.mEtIP);
        mPort=(EditText)findViewById(R.id.mEtPort);
        mBtnConnect=(Button)findViewById(R.id.mBt1);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        iv1 = (ImageView) findViewById(R.id.iv1);
        mBtnConnect.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mBt1:   //连接
                if (mSocket == null || !mSocket.isConnected()) {  //isConnected判断底层是否在线
                    mStrIp = mIp.getText().toString();
                    miPort = Integer.valueOf(mPort.getText().toString());
                    mConnectThread = new ConnectThread(mStrIp,miPort);
                    mConnectThread.start();
                }
                if (mSocket != null && mSocket.isConnected()) {
                    try {
                        mSocket.close();
                        mSocket = null;   //  清空mSocket
                        mBtnConnect.setText("连接");
                        Toast.makeText(Activitygatelock.this, "连接已关闭", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.button1:
                if(out!=null){
                    Activitygatelock.this.send("LED_11");  //锁开
                    iv1.setImageResource(img[1]);//gate
                }
                break;
            case R.id.button2:
                if (out!=null){
                    Activitygatelock.this.send("LED_22");  //锁关
                    iv1.setImageResource(img[0]);//gate
                }
                break;
        }
    }

    private class ConnectThread extends Thread {  //继承Thread，重写run()方法  进而实现Thread线程的使用
        private String ip;
        private int port;

        public ConnectThread(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                mSocket = new Socket(ip, port);                     //实例化API接口
                out = new PrintStream(mSocket.getOutputStream());   //得到输出流数据
                runOnUiThread(new Runnable() {  //runOnUiThread作用：当更新用户界面（UI）时返回到主线程中
                    @Override
                    public void run() {
                        mBtnConnect.setText("断开");
                        Toast.makeText(Activitygatelock.this, "连接成功", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Activitygatelock.this, "连接失败", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    public void send(final String paramString) {
        new Thread() {
            public void run() {
                try {
                    //DataOutputStream数据输出流,写到底
                    new DataOutputStream(Activitygatelock.this.mSocket.getOutputStream()).write(paramString.getBytes());
                    System.out.println("发送消息"+paramString);
                    return;
                } catch (IOException localIOException) {
                    while (true)
                        localIOException.printStackTrace();
                }
            }
        }.start();
    }

}
