package com.example.bancodedados.utils;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.bancodedados.R;

public class PasswordVisibility {

    private boolean isPasswordVisible = false;
    public void togglePasswordVisibility(EditText passwordField, ImageButton toggleButton) {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            toggleButton.setImageResource(R.drawable.ic_visibility_on);
        } else {
            passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            toggleButton.setImageResource(R.drawable.ic_visibility_off);
        }
        passwordField.setSelection(passwordField.getText().length());
    }
}

