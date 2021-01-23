package com.example.myread.ui.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myread.ChangePasswordDialog;
import com.example.myread.DeleteAccountDialog;
import com.example.myread.GlobalFunctions;
import com.example.myread.LoginActivity;
import com.example.myread.R;
import com.example.myread.ServerConnect;
import com.example.myread.models.User;
import com.google.android.material.switchmaterial.SwitchMaterial;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class SettingsFragment extends Fragment {
    SwitchMaterial dataSaver;
    private DeleteAccountDialog deleteAccountDialog;
    private ChangePasswordDialog changePasswordDialog;
    private final SharedPreferences prf = GlobalFunctions.getEncryptedSharedPreferences();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean dataSaverEnabled = GlobalFunctions.getEncryptedSharedPreferences().getBoolean("dataSaver", false);
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        Button delButton = rootView.findViewById(R.id.deleteAccount);
        Button changePasswordButton = rootView.findViewById(R.id.changePassword);
        dataSaver = rootView.findViewById(R.id.switch_datasaver);
        dataSaver.setChecked(dataSaverEnabled);
        dataSaver.setOnClickListener(v -> toggleDataSaver());

        delButton.setOnClickListener(v -> {
            deleteAccountDialog = new DeleteAccountDialog();
            deleteAccountDialog.setTargetFragment(this, 1);
            deleteAccountDialog.show(getParentFragmentManager(), "deleteDialog");
        });

        changePasswordButton.setOnClickListener(v -> {
            changePasswordDialog = new ChangePasswordDialog();
            changePasswordDialog.setTargetFragment(this, 2);
            changePasswordDialog.show(getParentFragmentManager(), "changePassDialog");
        });

        return rootView;
    }

    public void toggleDataSaver() {
        SharedPreferences prf = GlobalFunctions.getEncryptedSharedPreferences();
        SharedPreferences.Editor editor = prf.edit();
        editor.putBoolean("dataSaver", dataSaver.isChecked());
        editor.apply();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            final Dialog dialogView = deleteAccountDialog.getDialog();
            assert dialogView != null;
            final EditText confirmDeletePassword = dialogView.findViewById(R.id.confirm_delete_password);
            final String password = confirmDeletePassword.getText().toString();

            if (GlobalFunctions.passwordCheck(password) && sendDelPost(User.getInstance().name, password).successful) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                prf.edit().clear().apply();
                User.getInstance().destroy();
                getActivity().finish();
                Toast.makeText(getActivity(), "This account has been deleted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Failed to delete this account.", Toast.LENGTH_SHORT).show();
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            final Dialog dialogView = changePasswordDialog.getDialog();
            assert dialogView != null;
            EditText oldPass = dialogView.findViewById(R.id.changePassOldPass);
            EditText newPass = dialogView.findViewById(R.id.changePassNewPass);
            EditText repeatNewPass = dialogView.findViewById(R.id.changePassNewPassRepeat);

            String oldPassString = oldPass.getText().toString();
            String newPassString = newPass.getText().toString();
            String RepeatNewPassString = repeatNewPass.getText().toString();

            if (!newPassString.equals(RepeatNewPassString)) {
                Toast.makeText(getActivity(), "New passwords don't match!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (GlobalFunctions.passwordCheck(newPassString) && sendChangePassPost(User.getInstance().name, oldPassString, newPassString).successful) {
                Toast.makeText(getActivity(), "Password has been changed!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Failed to change password.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ServerConnect.Response sendDelPost(String name, String password) {
        final RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("pass", password)
                .build();

        return ServerConnect.getInstance().sendPost("user/" + User.getInstance().name + "/delete", formBody);
    }

    private ServerConnect.Response sendChangePassPost(String name, String oldPassword, String newPassword) {
        final RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("old_pass", oldPassword)
                .add("new_pass", newPassword)
                .build();

        return ServerConnect.getInstance().sendPost("user/" + User.getInstance().name + "/change_password", formBody);
    }
}