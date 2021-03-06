package com.example.myread;

import android.content.Context;
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
    private final Context context = GlobalApplication.getAppContext();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Put the username in the SharedPreferences, so the user is automatically logged in.
        prf = GlobalFunctions.getEncryptedSharedPreferences();
        if (prf.contains("username"))
            if (ServerConnect.getInstance().checkSession()) {
                ServerConnect.getInstance().initUser(prf.getString("username", ""));
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        spinner = findViewById(R.id.loadingIconLogin);
        Button login_btn = findViewById(R.id.login_btn);
        TextView registertext = findViewById(R.id.register_txt);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn.setOnClickListener(v -> new Thread(() -> {
            runOnUiThread(() -> login_btn.setEnabled(false));
            runOnUiThread(() -> spinner.setVisibility(View.VISIBLE));
            Looper.prepare();
            login();
            runOnUiThread(() -> spinner.setVisibility(View.INVISIBLE));
            runOnUiThread(() -> login_btn.setEnabled(true));
        }).start());

        registertext.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    /**
     * A function that logs in the user.
     */
    private void login() {
        trim_username = username.getText().toString().trim();
        trim_password = password.getText().toString().trim();
        if (validateUsername() && validatePassword()) {
            ServerConnect.Response response = sendPost();
            if (response.successful) {
                SharedPreferences.Editor editor = prf.edit();
                editor.putString("username", trim_username);
                editor.apply();
                ServerConnect.getInstance().initUser(prf.getString("username", ""));
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, context.getString(R.string.login_successful), Toast.LENGTH_SHORT).show());
            } else if (response.response.equals(context.getString(R.string.server_unreachable)))
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, context.getString(R.string.server_unreachable), Toast.LENGTH_SHORT).show());
            else
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, context.getString(R.string.login_failed), Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * A function that validates the username.
     * @return true or false.
     */
    private Boolean validateUsername() {
        if (TextUtils.isEmpty(trim_username)) {
            runOnUiThread(() -> username.setError(context.getString(R.string.username_not_entered)));
            return false;
        }
        return true;
    }

    /**
     * A function that validates the password.
     * @return true or false.
     */
    private Boolean validatePassword() {
        if (TextUtils.isEmpty(trim_password)) {
            runOnUiThread(() -> password.setError(context.getString(R.string.password_not_entered)));
            return false;
        }
        return true;
    }

    /**
     * A function that sends a post request with the username and password.
     * @return a serverconnect response.
     */
    private ServerConnect.Response sendPost() {
        final RequestBody formBody = new FormBody.Builder()
                .add("name", trim_username)
                .add("pass", trim_password)
                .build();

        return ServerConnect.getInstance().sendPost("login", formBody);
    }
}
