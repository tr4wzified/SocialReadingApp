package com.example.myread;

import android.content.Intent;
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
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
        Button register_btn = (Button)findViewById(R.id.register_btn);
        register_btn.setOnClickListener(v -> {
            registerUser();
        });

//        init();
    }
    private void register() {
        Button register_btn = (Button)findViewById(R.id.register_btn);

        register_btn.setOnClickListener(v -> {
            registerUser();
        });
    }


//    private void init() {
//        Button register_btn = (Button)findViewById(R.id.register_btn);
//        username = (EditText) findViewById(R.id.username);
//        password = (EditText) findViewById(R.id.password);
//        confirm_password = (EditText) findViewById(R.id.confirm_password);
//
//        register_btn.setOnClickListener(v -> {
//            getEditString();
//
//            if (TextUtils.isEmpty(trim_username)) {
//                Toast.makeText(RegisterActivity.this, "Username field is empty.", Toast.LENGTH_SHORT).show();
//            } else if (TextUtils.isEmpty(trim_password)) {
//                Toast.makeText(RegisterActivity.this, "Password field is empty.", Toast.LENGTH_SHORT).show();
//            } else if (TextUtils.isEmpty(trim_confirm_password)) {
//                Toast.makeText(RegisterActivity.this, "Confirm Password field is empty.", Toast.LENGTH_SHORT).show();
//            } else if (!trim_password.equals(trim_confirm_password)) {
//                Toast.makeText(RegisterActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
//            } else{
//                URL baseurl = null;
//                try {
//                    baseurl = new URL("https://10.0.2.2:2048");
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//                OkHttpClient client = getUnsafeOkHttpClient();
////                    HashMap<String, String> params;
////                    params.
////                    StringBuilder sbParams = new StringBuilder();
////                    int i = 0;
////
////                    for (String key : params.keySet()) {
////                        try {
////                            if (i !=0) {
////                                sbParams.append("&");
////                            }
////                            sbParams.append(key).append("=").append(URLEncoder.encode(params.get(key), "UTF-8"));
////                        } catch (UnsupportedEncodingException e) {
////                            e.printStackTrace();
////                        }
////                        i++
////                    }
//                assert baseurl != null;
//                final RequestBody formBody = new FormBody.Builder()
//                        .add("name", trim_username)
//                        .add("pass", trim_password)
//                        .build();
//
//                final Request request = new Request.Builder()
//                        .url(baseurl + "/register")
//                        .post(formBody)
//                        .build();
//
//                Thread thr = new Thread(() -> {
//                    try (Response response = client.newCall(request).execute()) {
//                        if (response.isSuccessful()) {
//                        } else {
//                            throw new IOException("Unexpected code " + response);
//                        }
//
//                        // Get response body
//                        System.out.println(response);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                });
//                thr.start();
//                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//            }
//        });
//
//    }

    private void sendPost() {
        try {
            URL baseurl = null;
            try {
                baseurl = new URL("https://10.0.2.2:2048");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            OkHttpClient client = getUnsafeOkHttpClient();
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
                    if (response.isSuccessful()) {
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }

                    // Get response body
                    System.out.println(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thr.start();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {

                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws
                                CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws
                                CertificateException {
                        }
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

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void getEditString() {
        trim_username = username.getText().toString().trim();
        trim_password = password.getText().toString().trim();
        trim_confirm_password = confirm_password.getText().toString().trim();
    }

    private Boolean validateUsername() {
        if (TextUtils.isEmpty(trim_username)) {
            Toast.makeText(RegisterActivity.this, "Username field is empty.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            username.setError(null);
//            username.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        Matcher matcher = Pattern.compile("((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{4,20})").matcher(trim_password);

        if (TextUtils.isEmpty(trim_password)) {
            Toast.makeText(RegisterActivity.this, "Password field is empty.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!matcher.matches()) {
            password.setError("Password must contain 8 characters with at least 1 number, 1 lower case letter, 1 upper case letter, 1 special character");
            return false;
        } else {
            password.setError(null);
            return true;
//            password.setErrorEnabled(false);
        }
    }

    private Boolean validatePasswordConfirm() {
        String pass = trim_password;
        String pass_con = trim_confirm_password;
        if (TextUtils.isEmpty(trim_confirm_password)) {
            Toast.makeText(RegisterActivity.this, "Confirm Password field is empty.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (pass_con.equals(pass)) {
            return true;
        }
        return false;
    }

    public void registerUser() {
        getEditString();
        if (!validateUsername() | !validatePassword() | !validatePasswordConfirm()) {
            return;
        } else {
            sendPost();
        }
    }



}
