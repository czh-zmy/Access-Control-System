package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Activitycipher extends AppCompatActivity {
    private Button btn_register;//确定按钮
    private EditText et_psw,et_psw_again;
    //用户名，密码，再次输入的密码的控件的获取值
    private String psw,pswAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitycipher);
        init();
    }

    private void init() {
        et_psw=findViewById(R.id.et_psw);
        et_psw_again=findViewById(R.id.et_psw_again);

        btn_register=(Button) findViewById(R.id.btn_register);
        //确定按钮
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入在相应控件中的字符串
                getEditString();
                //判断输入框内容
                if(TextUtils.isEmpty(psw)){
                    Toast.makeText(Activitycipher.this, "请输入6位数密码", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(pswAgain)){
                    Toast.makeText(Activitycipher.this, "请再次输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }else if(!psw.equals(pswAgain)){
                    Toast.makeText(Activitycipher.this, "输入两次的密码不一样", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Toast.makeText(Activitycipher.this, "成功", Toast.LENGTH_SHORT).show();
                    saveRegisterInfo(pswAgain, psw);
                    Activitycipher.this.finish();
                }
            }
        });

    }
    private void getEditString() {
        psw=et_psw.getText().toString().trim();
        pswAgain=et_psw_again.getText().toString().trim();
    }

    private void saveRegisterInfo(String pswAgain,String psw) {
        String md5Psw = Md5uTils.md5(psw);
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(pswAgain, md5Psw);
        editor.commit();

    }



}
