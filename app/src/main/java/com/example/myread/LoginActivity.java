package com.example.myread;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;
    private String trim_username, trim_password;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login_btn = findViewById(R.id.login_btn);
        TextView registertext = findViewById(R.id.register_txt);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn.setOnClickListener(v -> login());
        registertext.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void login() {
        getEditString();
        if (validateUsername() && validatePassword()){
            sendPost();
        }
    }

    private void getEditString() {
        trim_username = username.getText().toString().trim();
        trim_password = password.getText().toString().trim();
    }

    private Boolean validateUsername() {
        if (TextUtils.isEmpty(trim_username)) {
            Toast.makeText(LoginActivity.this, "Username field is empty.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        if (TextUtils.isEmpty(trim_password)) {
            Toast.makeText(LoginActivity.this, "Password field is empty.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private void sendPost() {
        try {
            URL url = null;
            try {
                url = new URL("https://10.0.2.2:2048/login");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            OkHttpClient client = getUnsafeOkHttpClient();
            assert url != null;
            final RequestBody formBody = new FormBody.Builder()
                    .add("name", trim_username)
                    .add("pass", trim_password)
                    .build();

            final Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            Thread thr = new Thread(() -> {
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show());
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class)); // FIX!!!!!!!!!!!!
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show());
                    }

                    // Get response body
                    System.out.println(response);
                } catch (IOException e) {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Can't reach server", Toast.LENGTH_SHORT).show());
                }
            });
            thr.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
