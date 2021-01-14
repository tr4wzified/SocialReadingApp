package com.example.myread.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;

import com.example.myread.GlobalApplication;
import com.example.myread.GlobalFunctions;
import com.example.myread.LoginActivity;
import com.example.myread.MainActivity;
import com.example.myread.R;
import com.example.myread.ServerConnect;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {
    SwitchMaterial dataSaver;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean dataSaverEnabled = GlobalFunctions.getEncryptedSharedPreferences().getBoolean("dataSaver", false);
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        dataSaver = rootView.findViewById(R.id.switch_datasaver);
        dataSaver.setChecked(dataSaverEnabled);
        dataSaver.setOnClickListener(v -> toggleDataSaver());

        return rootView;
    }

    public void toggleDataSaver() {
        SharedPreferences prf = GlobalFunctions.getEncryptedSharedPreferences();
        SharedPreferences.Editor editor = prf.edit();
        editor.putBoolean("dataSaver", dataSaver.isChecked());
        editor.apply();
    }
}