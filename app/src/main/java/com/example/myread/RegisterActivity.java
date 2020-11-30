package com.example.myread;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.*;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity {
    private String trim_username, trim_password, trim_confirm_password;
    private EditText username, password, confirm_password;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }

    private void init() {
        Button register_btn = (Button)findViewById(R.id.register_btn);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);

        register_btn.setOnClickListener(v -> {
            getEditString();

            if (TextUtils.isEmpty(trim_username)) {
                Toast.makeText(RegisterActivity.this, "Username field is empty.", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(trim_password)) {
                Toast.makeText(RegisterActivity.this, "Password field is empty.", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(trim_confirm_password)) {
                Toast.makeText(RegisterActivity.this, "Confirm Password field is empty.", Toast.LENGTH_SHORT).show();
            } else if (!trim_password.equals(trim_confirm_password)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            } else{
                URL baseurl = null;
                try {
                    baseurl = new URL("https://192.168.2.23:2048");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                OkHttpClient client = new OkHttpClient();
//                    HashMap<String, String> params;
//                    params.
//                    StringBuilder sbParams = new StringBuilder();
//                    int i = 0;
//
//                    for (String key : params.keySet()) {
//                        try {
//                            if (i !=0) {
//                                sbParams.append("&");
//                            }
//                            sbParams.append(key).append("=").append(URLEncoder.encode(params.get(key), "UTF-8"));
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                        i++
//                    }
                assert baseurl != null;
                final RequestBody formBody = new FormBody.Builder()
                        .add("name", trim_username)
                        .add("pass", trim_password)
                        .build();

                final Request request = new Request.Builder()
                        .url(baseurl + "/register")
                        .post(formBody)
                        .build();

                Thread thr = new Thread(() -> {
                    try (Response response = client.newCall(request).execute()) {

                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }

                        // Get response body
                        System.out.println(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                thr.start();
            }
        });

    }

    private void getEditString() {
        trim_username = username.getText().toString().trim();
        trim_password = password.getText().toString().trim();
        trim_confirm_password = confirm_password.getText().toString().trim();
    }


}
