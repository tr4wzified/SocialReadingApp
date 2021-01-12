package com.example.myread;

import android.content.Context;
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
    private final Context context = GlobalApplication.getAppContext();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        Button register_btn = findViewById(R.id.register_btn);
        TextView logintext = findViewById(R.id.login_txt);

        register_btn.setOnClickListener(v -> registerUser(
                username.getText().toString().trim(),
                password.getText().toString().trim(),
                confirm_password.getText().toString().trim()
        ));

        logintext.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    /**
     * A function that sends a post request to the server and logs in the user based on the response.
     * @param name the username.
     * @param password the password.
     */
    private void sendPost(String name, String password) {
        final RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("pass", password)
                .build();

        ServerConnect.Response response = ServerConnect.getInstance().sendPost("register", formBody);
        if (response.successful) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, context.getString(R.string.registration_successful), Toast.LENGTH_SHORT).show());
            return;
        }

        if (response.response.equals(context.getString(R.string.server_unreachable)))
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, context.getString(R.string.server_unreachable), Toast.LENGTH_SHORT).show());
        else
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, context.getString(R.string.registration_failed), Toast.LENGTH_SHORT).show());
    }

    /**
     * A function that validates the username.
     * @param reg_username the username.
     * @return true or false.
     */
    private Boolean validateUsername(String reg_username) {
        if (TextUtils.isEmpty(reg_username))
            username.setError(context.getString(R.string.username_not_entered));
        else if (usernameComplexityTest(reg_username))
            username.setError(context.getString(R.string.username_no_special_chars));
        else {
            username.setError(null);
            return true;
        }
        return false;
    }

    /**
     * A function that checks if the password matches the regex pattern.
     * @param reg_password the password
     * @return true or false.
     */
    static Boolean passwordComplexityTest(String reg_password) {
        return Pattern.compile("((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{9,64})").matcher(reg_password).matches();
    }

    static Boolean usernameComplexityTest(String reg_username) {
       return Pattern.compile("/^([a-z\\d]+-)*[a-z\\d]+$/i").matcher(reg_username).matches();
    }

    /**
     * A function that validates the password.
     * @param reg_password the password.
     * @param reg_username the username.
     * @return true or false.
     */
    private Boolean validatePassword(String reg_password, String reg_username) {
        if (TextUtils.isEmpty(reg_password))
            password.setError(context.getString(R.string.password_not_entered));
        else if (!passwordComplexityTest(reg_password))
            password.setError(context.getString(R.string.password_requirements));
        else if (reg_password.contains(reg_username))
            password.setError(context.getString(R.string.password_no_username));
        else {
            password.setError(null);
            return true;
        }
        return false;
    }

    /**
     * A function that validates the password confirmation.
     * @param reg_password the password.
     * @param reg_passwordConfirm the confirmation password.
     * @return true or false.
     */
    private Boolean validatePasswordConfirm(String reg_password, String reg_passwordConfirm) {
        if (TextUtils.isEmpty(reg_passwordConfirm)) {
            Toast.makeText(RegisterActivity.this, context.getString(R.string.request_confirm_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        return reg_passwordConfirm.equals(reg_password);
    }

    /**
     * A function that registers the user by sending a post request to the server.
     * @param username the username.
     * @param password the password.
     * @param passwordConfirm the confirmation password.
     */
    public void registerUser(String username, String password, String passwordConfirm) {
        if (!validateUsername(username) || !validatePassword(password, username) || !validatePasswordConfirm(password, passwordConfirm))
            return;

        sendPost(username, password);
    }
}
