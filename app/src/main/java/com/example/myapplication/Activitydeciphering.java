package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

public class Activitydeciphering extends AppCompatActivity {
    private AliPayEditText editText;
    private String pswAgain,psw,spPsw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitydeciphering);
        initData();
        initView();


    }

    private void initView() {
        editText = (AliPayEditText) findViewById(R.id.edittext);
        editText.setOnInputListener(new AliPayEditText.OnInputListener() {
            @Override
            public void onInput(String text, int currentLength) {
                editText.setEditLineColor(Color.parseColor("#000000"));
            }

            @Override
            public void onFinish(String text) {
                pswAgain=editText.getText().toString().trim();
                psw=editText.getText().toString().trim();
                String md5Psw = Md5uTils.md5(psw);
                spPsw = readPsw(pswAgain);

                if (md5Psw.equals(spPsw)) {
                    Toast.makeText(Activitydeciphering.this, "密码正确", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(Activitydeciphering.this,Activitygatelock.class);
                    startActivity(intent);
                } else {
                    editText.setEditLineColor(Color.parseColor("#ff4500"));
                    editText.vibrate();
                }
            }
        });
    }

    private String readPsw(String pswAgain){
        //getSharedPreferences("loginInfo",MODE_PRIVATE);
        //"loginInfo",mode_private; MODE_PRIVATE表示可以继续写入
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        //sp.getString() userName, "";
        return sp.getString(pswAgain , "");
    }

    private void initData() {
    }


}
