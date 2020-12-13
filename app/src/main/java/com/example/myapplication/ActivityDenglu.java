package com.example.myapplication;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class ActivityDenglu extends AppCompatActivity {
    private Button btn_qr,btn_gate;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denglu);
        init();
    }

    private void init() {
        btn_qr=findViewById(R.id.btn_qr);
        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建IntentIntegrator对象
                IntentIntegrator intentIntegrator = new IntentIntegrator(ActivityDenglu.this);
                intentIntegrator.setCaptureActivity(CustomCaptureActivity.class);
                intentIntegrator.setPrompt("请将二维码放置取景框扫描");
                intentIntegrator.initiateScan();

            }
        });

        btn_gate=findViewById(R.id.btn_gate);
        btn_gate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDenglu.this,Activitycipher.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 获取解析结果
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "取消扫描", Toast.LENGTH_LONG).show();
            } else {
                Intent intent =new Intent(ActivityDenglu.this,Activitydeciphering.class);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}



