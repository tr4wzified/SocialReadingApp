package com.example.myread;

import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.regex.Pattern;

public class GlobalFunctions {
    private static SharedPreferences prf;

    public static boolean asciip(String s) {
        return s.chars().allMatch(c -> c < 128);
    }

    public static boolean createCollectionRegex(String s) {
        final Pattern sPattern
                = Pattern.compile("^[a-zA-Z0-9 ]*$");

        return sPattern.matcher(s).matches();
    }

    /**
     * A function that returns the sharedpreferences.
     *
     * @return the sharedpreferences.
     */
    public static SharedPreferences getEncryptedSharedPreferences() {
        if (prf != null)
            return prf;

        KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                .build();

        try {
            MasterKey masterKey = new MasterKey.Builder(GlobalApplication.getAppContext()).setKeyGenParameterSpec(spec).build();
            prf = EncryptedSharedPreferences.create(GlobalApplication.getAppContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS, masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return prf;
    }
}
