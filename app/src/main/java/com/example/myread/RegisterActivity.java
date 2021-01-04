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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class RegisterActivity extends AppCompatActivity {
    private EditText username, password, confirm_password;
    SharedPreferences prf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        prf = GlobalApplication.getEncryptedSharedPreferences();
        //prf = getSharedPreferences("user_details",MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.collection_name);
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

    private Boolean sendPost(String name, String password) {
        final RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("pass", password)
                .build();

        ServerConnect.Response response = ServerConnect.getInstance().sendPost("register", formBody);
        if (response.successful) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show());
            return true;
        }

        if (response.response.equals("Unable to reach server"))
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Can't reach server", Toast.LENGTH_SHORT).show());
        else
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration unsuccessful.", Toast.LENGTH_SHORT).show());
        return false;
    }

    private Boolean validateUsername(String reg_username) {
        if (TextUtils.isEmpty(reg_username)) {
            Toast.makeText(RegisterActivity.this, "Username field is empty.", Toast.LENGTH_SHORT).show();
            return false;
        }

        username.setError(null);
        return true;
    }

    static Boolean passwordComplexityTest(String reg_password)
    {
        Matcher matcher = Pattern.compile("((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{9,64})").matcher(reg_password);
        return matcher.matches();
    }

    private Boolean validatePassword(String reg_password) {
        if (TextUtils.isEmpty(reg_password)) {
            Toast.makeText(RegisterActivity.this, "Password field is empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!passwordComplexityTest(reg_password)) {
            password.setError("Password must contain 8 characters with at least 1 number, 1 lower case letter, 1 upper case letter and 1 special character");
            return false;
        }
        password.setError(null);
        return true;
    }

    private Boolean validatePasswordConfirm(String reg_password, String reg_passwordConfirm) {
        if (TextUtils.isEmpty(reg_passwordConfirm)) {
            Toast.makeText(RegisterActivity.this, "Confirm Password field is empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return reg_passwordConfirm.equals(reg_password);
    }

    public Boolean registerUser(String username, String password, String passwordConfirm) {
        if (!validateUsername(username) || !validatePassword(password) || !validatePasswordConfirm(password, passwordConfirm))
            return false;

        return sendPost(username, password);
    }
}
