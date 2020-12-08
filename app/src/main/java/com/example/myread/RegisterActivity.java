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


public class RegisterActivity extends AppCompatActivity {
    private EditText username, password, confirm_password;
    private TextView logintext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        Button register_btn = findViewById(R.id.register_btn);
        logintext = findViewById(R.id.login_txt);

        register_btn.setOnClickListener(v -> registerUser(username.getText().toString().trim(), password.getText().toString().trim(), confirm_password.getText().toString().trim()));
        logintext.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        registrationTest();
    }


    private void sendPost(String name, String password) {
        try {
            URL url = null;
            try {
                url = new URL("https://10.0.2.2:2048/register");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            OkHttpClient client = getUnsafeOkHttpClient();
            assert url != null;
            final RequestBody formBody = new FormBody.Builder()
                    .add("name", name)
                    .add("pass", password)
                    .build();

            final Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            Thread thr = new Thread(() -> {
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show());
                    } else {
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show());
                    }

                    // Get response body
                    System.out.println(response);
                } catch (IOException e) {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Can't reach server", Toast.LENGTH_SHORT).show());
                }
            });
            thr.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



//    private void getEditString() {
//        trim_username = username.getText().toString().trim();
//        trim_password = password.getText().toString().trim();
//        trim_confirm_password = confirm_password.getText().toString().trim();
//    }

    private Boolean validateUsername(String reg_username) {
        if (TextUtils.isEmpty(reg_username)) {
            Toast.makeText(RegisterActivity.this, "Username field is empty.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }

    private Boolean validatePassword(String reg_password) {
        Matcher matcher = Pattern.compile("((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{9,64})").matcher(reg_password);

        if (TextUtils.isEmpty(reg_password)) {
            Toast.makeText(RegisterActivity.this, "Password field is empty.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!matcher.matches()) {
            password.setError("Password must contain 8 characters with at least 1 number, 1 lower case letter, 1 upper case letter and 1 special character");
            return false;
        } else {
            password.setError(null);
            return true;
//            password.setErrorEnabled(false);
        }
    }

    private Boolean validatePasswordConfirm(String reg_password, String reg_passwordConfirm) {
        if (TextUtils.isEmpty(reg_passwordConfirm)) {
            Toast.makeText(RegisterActivity.this, "Confirm Password field is empty.", Toast.LENGTH_SHORT).show();
            return false;
        } else return reg_passwordConfirm.equals(reg_password);
    }

    public void registerUser(String username, String password, String passwordConfirm) {
        //        getEditString();
        if (!validateUsername(username) | !validatePassword(password) | !validatePasswordConfirm(password, passwordConfirm)) {
            System.out.println("User information failed validation: " + username);
        } else {
            sendPost(username, password);
            System.out.println("User information passed validation: " + username);
        }
    }

    private OkHttpClient getUnsafeOkHttpClient(){
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

    public void registrationTest() {
        List<String> name = new ArrayList<>();
        List<String> pass = new ArrayList<>();
        List<String> pass_con = new ArrayList<>();

        name.add("JaccoKees");
        name.add("Willem");
        name.add("Trawzified");
        name.add("Hendrik");
        name.add("AltLocky32");
        name.add("Robin234234");
        name.add("Jacsquare");
        name.add("joostvanhengelen");

        pass.add("Kees12aaaa");
        pass.add("Willem12@3aaaa");
        pass.add("Trawy987!aaaa");
        pass.add("wachtwoordaaaa");
        pass.add("Sperzi3Boonaaaa!");
        pass.add("RobinXD!3aaa");
        pass.add("squarielolaa3");
        pass.add("Hengelenmaaraaalol");

        pass_con.add("Kees12aaaa");
        pass_con.add("Willem12@3aaaa");
        pass_con.add("Trawy987!aaaa");
        pass_con.add("wachtwoordaaaa");
        pass_con.add("Sperzi3Boonaaaa!");
        pass_con.add("RobinXD!33aaa");
        pass_con.add("squarielolaa3");
        pass_con.add("Hengelenmaaraaalol");

        for (int i = 0; i < name.size(); i++) {
            String username = name.get(i);
            String password = pass.get(i);
            String pass_confirm = pass_con.get(i);
            registerUser(username, password, pass_confirm);

            System.out.println();
        }
    }
}
