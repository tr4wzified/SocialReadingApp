package com.example.myread;

import android.os.Bundle;
import android.os.Looper;
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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.concurrent.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
        Button register_btn = findViewById(R.id.register_btn);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);

        register_btn.setOnClickListener(v -> {
            //set trimmed vars
            getEditString();

            //check for errors in user input
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
                    url = new URL("https://10.0.2.2:2048/register");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                //Create http client that allows self signed certificates
                OkHttpClient client = getUnsafeOkHttpClient();
                assert url != null;

                //build request inside
                final RequestBody formBody = new FormBody.Builder()
                        .add("name", trim_username)
                        .add("pass", trim_password)
                        .build();

                //build actual request
                final Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                //start new thread for networking purposes
                Thread thr = new Thread(() -> {
                    try (Response response = client.newCall(request).execute()) {

                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }
                        else {
                            //Redirect to start screen
                        }

                        System.out.println(response);
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Can't reach server.", Toast.LENGTH_SHORT).show());
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

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {

                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) { }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) { }
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

            builder.hostnameVerifier((hostname, session) -> true);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
