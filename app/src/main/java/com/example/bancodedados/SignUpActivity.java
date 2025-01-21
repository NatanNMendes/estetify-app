package com.example.bancodedados;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.bancodedados.services.SignUpService;
import com.example.bancodedados.utils.Navigation;
import com.example.bancodedados.utils.PasswordVisibility;

public class SignUpActivity extends AppCompatActivity {
    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button createAccountButton;
    private ImageButton togglePasswordButton, toggleConfirmPasswordButton;
    private PasswordVisibility passwordVisibility;
    private Navigation navigation;
    private SignUpService signUpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        passwordVisibility = new PasswordVisibility();
        navigation = new Navigation(this);
        signUpService = new SignUpService();

        setupUI();
        setupWindow();
    }

    private void setupUI() {
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        togglePasswordButton = findViewById(R.id.togglePasswordButton);
        toggleConfirmPasswordButton = findViewById(R.id.toggleConfirmPasswordButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        setupListeners();
    }

    private void setupWindow() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (username.isEmpty()) {
            usernameInput.setError("Nome de usuário é obrigatório");
            usernameInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            isValid = false;
        } else {
            usernameInput.setError(null);
            usernameInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        }

        if (email.isEmpty()) {
            emailInput.setError("Email é obrigatório");
            emailInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            isValid = false;
        } else {
            emailInput.setError(null);
            emailInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        }

        if (password.isEmpty()) {
            passwordInput.setError("Senha é obrigatória");
            passwordInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));

            isValid = false;
        } else if (password.length() < 6) {
            passwordInput.setError("A senha deve ter pelo menos 6 caracteres");
            passwordInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            isValid = false;
        } else {
            passwordInput.setError(null);
            passwordInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.setError("Confirmação de senha é obrigatória");
            confirmPasswordInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordInput.setError("As senhas não coincidem");
            confirmPasswordInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            isValid = false;
        } else {
            confirmPasswordInput.setError(null);
            confirmPasswordInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        }

        return isValid;
    }



    private void setupListeners() {
        togglePasswordButton.setOnClickListener(view -> passwordVisibility.togglePasswordVisibility(passwordInput, togglePasswordButton));
        toggleConfirmPasswordButton.setOnClickListener(view -> passwordVisibility.togglePasswordVisibility(confirmPasswordInput, toggleConfirmPasswordButton));
        createAccountButton.setOnClickListener(view -> {
            if (validateInputs()) {
                String username = usernameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String confirmPassword = confirmPasswordInput.getText().toString().trim();

                signUpService.createAccount(email, password, confirmPassword, username, this);
            }
        });
    }
}
