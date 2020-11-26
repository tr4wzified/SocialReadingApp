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
            System.out.println("Doet het het?");
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
                URL url = null;
                try {
                    url = new URL("https://172.0.0.1:2048/register");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection client = null;
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
                try {
                    assert url != null;
                    client = (HttpURLConnection) url.openConnection();

                    client.setRequestMethod("POST");
                    client.setRequestProperty("name", trim_username);
                    client.setRequestProperty("pass", trim_password);
                    client.setDoOutput(true);
                    client.setReadTimeout(10000);
                    client.setConnectTimeout(15000);
                    client.connect();


//                        DataOutputStream wr = new DataOutputStream(client.getOutputStream());
//                        wr.writeBytes(sbParams);
//                        wr.flush();
//                        wr.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    InputStream in = new BufferedInputStream(client.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("test", "result from server: " + result.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (client != null) {
                        client.disconnect();
                    }
                }
            }
        });

    }

    private void getEditString() {
        trim_username = username.getText().toString().trim();
        trim_password = password.getText().toString().trim();
        trim_confirm_password = confirm_password.getText().toString().trim();
    }
}
