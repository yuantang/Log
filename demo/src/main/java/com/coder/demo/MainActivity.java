package com.coder.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.coder.tlog.TLog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mButton1,mButton2,mButton3,mButton4,mButton5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton1= (Button) findViewById(R.id.text1);
        mButton2= (Button) findViewById(R.id.text2);
        mButton3= (Button) findViewById(R.id.text3);
        mButton4= (Button) findViewById(R.id.text4);
        mButton5= (Button) findViewById(R.id.text5);

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButton5.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text1:
                TLog.e("============onClick=============");
                break;
            case R.id.text2:
                String json="{\"error\":1000,\"msg\":\"The user name cannot be the only empty.\"}";
                TLog.json("============json============="+json);
                break;
            case R.id.text3:
                String xml="<note>\n" +
                        "  <to>Tove</to>\n" +
                        "  <from>Jani</from>\n" +
                        "  <heading>Reminder</heading>\n" +
                        "  <body>Don't forget me this weekend!</body>\n" +
                        "</note>";
                TLog.xml("============xml============="+xml);
                break;
            case R.id.text4:
                String[] num=new String[]{"12","23","34"};
                String  mub4=num[4];
                break;
            case R.id.text5:
                TLog.sendToCustomerService(this,"1076027819@qq.com","yuantang126@126.com","发送日志信息","ddddddd","test");
                break;

        }
    }}
