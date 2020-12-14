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
    private TextView logintext;
    private Button register_btn;
    SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        if (pref.contains("username")) {
            System.out.println("Account already detected, going to main");
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        register_btn = findViewById(R.id.register_btn);
        logintext = findViewById(R.id.login_txt);

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

        ServerConnect.Response response = ServerConnect.getInstance().sendPost("/register", formBody);
        if (response.successful) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show());
        }
        else if (response.response.equals("Unable to reach server"))
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Can't reach server", Toast.LENGTH_SHORT).show());
        else
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration unsuccessful.", Toast.LENGTH_SHORT).show());
    }

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
