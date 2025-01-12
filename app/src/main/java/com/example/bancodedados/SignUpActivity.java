package com.example.bancodedados;

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
        setupListeners();
    }

    private void setupUI() {
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        togglePasswordButton = findViewById(R.id.togglePasswordButton);
        toggleConfirmPasswordButton = findViewById(R.id.toggleConfirmPasswordButton);
        createAccountButton = findViewById(R.id.createAccountButton);
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

    private void setupListeners() {
        togglePasswordButton.setOnClickListener(view -> passwordVisibility.togglePasswordVisibility(passwordInput, togglePasswordButton));
        toggleConfirmPasswordButton.setOnClickListener(view -> passwordVisibility.togglePasswordVisibility(confirmPasswordInput, toggleConfirmPasswordButton));
        createAccountButton.setOnClickListener(view -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            signUpService.createAccount(email, password, confirmPassword, username, this);
        });
    }
}
