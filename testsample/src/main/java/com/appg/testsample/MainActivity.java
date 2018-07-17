package com.appg.testsample;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button registerBtn;
    EditText idet, pwet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("ORACLE");

        registerBtn = (Button)findViewById(R.id.register_btn);
        idet = (EditText)findViewById(R.id.register_id);
        pwet = (EditText)findViewById(R.id.register_pw);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    String result;
                    String id = idet.getText().toString();
                    String pw = pwet.getText().toString();

                    RegisterActivity task = new RegisterActivity();
                    result = task.execute(id, pw).get();
                } catch (Exception e) {
                    Log.i("DBtest", ".....ERROR.....!");
                }
            }
        });

    }
}
