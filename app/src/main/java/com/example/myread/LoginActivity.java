package com.example.myread;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;
    private String trim_username, trim_password;
    SharedPreferences prf;
    private ProgressBar spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        prf = GlobalApplication.getEncryptedSharedPreferences();
        if (prf.contains("username")) {
            if (ServerConnect.getInstance().checkSession()) {
                System.out.println("Account already detected, going to main");
                ServerConnect.getInstance().initUser(prf.getString("username", ""));
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        spinner = (ProgressBar) findViewById(R.id.loadingIconLogin);
        Button login_btn = findViewById(R.id.login_btn);
        TextView registertext = findViewById(R.id.register_txt);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn.setOnClickListener(v -> new Thread(() -> {
            runOnUiThread(() -> spinner.setVisibility(View.VISIBLE));
            Looper.prepare();
            login();
            runOnUiThread(() -> spinner.setVisibility(View.INVISIBLE));
        }).start());


        registertext.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void login() {
        getEditString();
        if (validateUsername() && validatePassword()) {
            ServerConnect.Response response = sendPost();
            if (response.successful) {
                SharedPreferences.Editor editor = prf.edit();
                editor.putString("username", trim_username);
                editor.apply();
                ServerConnect.getInstance().initUser(prf.getString("username", ""));
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show());
            } else if (response.response.equals("Unable to reach server"))
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Can't reach server", Toast.LENGTH_SHORT).show());
            else
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login unsuccessful.", Toast.LENGTH_SHORT).show());
        }
    }

    private void getEditString() {
        trim_username = username.getText().toString().trim();
        trim_password = password.getText().toString().trim();
    }

    private Boolean validateUsername() {
        if (TextUtils.isEmpty(trim_username)) {
            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Username field is empty.", Toast.LENGTH_SHORT).show());
            return false;
        } else {
            return true;
        }
    }

    private Boolean validatePassword() {
        if (TextUtils.isEmpty(trim_password)) {
            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Password field is empty.", Toast.LENGTH_SHORT).show());
            return false;
        } else {
            return true;
        }
    }

    private ServerConnect.Response sendPost() {
        final RequestBody formBody = new FormBody.Builder()
                .add("name", trim_username)
                .add("pass", trim_password)
                .build();

        ServerConnect.Response response = ServerConnect.getInstance().sendPost("login", formBody);
        System.out.println(response.response);
        return response;
    }
}
