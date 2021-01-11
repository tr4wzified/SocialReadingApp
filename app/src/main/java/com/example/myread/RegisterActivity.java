package com.example.myread;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class RegisterActivity extends AppCompatActivity {
    private EditText username, password, confirm_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        Button register_btn = findViewById(R.id.register_btn);
        TextView logintext = findViewById(R.id.login_txt);

        register_btn.setOnClickListener(v -> registerUser(username.getText().toString().trim(), password.getText().toString().trim(), confirm_password.getText().toString().trim()));
        logintext.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void sendPost(String name, String password) {
        final RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("pass", password)
                .build();

        ServerConnect.Response response = ServerConnect.getInstance().sendPost("register", formBody);
        if (response.successful) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show());
            return;
        }

        if (response.response.equals("Unable to reach server"))
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Unable to reach server.", Toast.LENGTH_SHORT).show());
        else
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show());
    }

    private Boolean validateUsername(String reg_username) {
        if (TextUtils.isEmpty(reg_username)) {
            username.setError("Username field is empty!");
        }
        else if (usernameComplexityTest(reg_username)) {
            username.setError("Username cannot contain special characters!");
        }
        else {
            username.setError(null);
            return true;
        }
        return false;
    }

    private static Boolean passwordComplexityTest(String reg_password) {
        return Pattern.compile("((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{9,64})").matcher(reg_password).matches();
    }

    private static Boolean usernameComplexityTest(String reg_username) {
       return Pattern.compile("/^([a-z\\d]+-)*[a-z\\d]+$/i").matcher(reg_username).matches();
    }

    private Boolean validatePassword(String reg_password, String reg_username) {
        if (TextUtils.isEmpty(reg_password)) {
            password.setError("Password field is empty!");
        }
        else if (!passwordComplexityTest(reg_password)) {
            password.setError("Password must contain 9 characters with at least a number, a lower case letter, an upper case letter and a special character!");
        }
        else if (reg_password.contains(reg_username)) {
            password.setError("Password must not contain username!");
        }
        else {
            password.setError(null);
            return true;
        }
        return false;
    }

    private Boolean validatePasswordConfirm(String reg_password, String reg_passwordConfirm) {
        if (TextUtils.isEmpty(reg_passwordConfirm)) {
            Toast.makeText(RegisterActivity.this, "Please confirm your password in the second field!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return reg_passwordConfirm.equals(reg_password);
    }

    public void registerUser(String username, String password, String passwordConfirm) {
        if (!validateUsername(username) || !validatePassword(password, username) || !validatePasswordConfirm(password, passwordConfirm))
            return;

        sendPost(username, password);
    }
}
