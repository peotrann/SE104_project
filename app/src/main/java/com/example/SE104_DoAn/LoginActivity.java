package com.example.SE104_DoAn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView forgotPasswordText, signUpText;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "registeredEmail";
    private static final String KEY_PASSWORD = "registeredPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Kiểm tra trạng thái đăng nhập
        if (sharedPreferences.getBoolean(KEY_LOGGED_IN, false)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Khởi tạo các view
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        forgotPasswordText = findViewById(R.id.forgot_password_text);
        signUpText = findViewById(R.id.sign_up_text);

        // Xử lý sự kiện nhấn nút đăng nhập
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateLogin(email, password)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_LOGGED_IN, true);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý quên mật khẩu
        forgotPasswordText.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng quên mật khẩu chưa được triển khai", Toast.LENGTH_SHORT).show();
        });

        // Xử lý đăng ký
        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateLogin(String email, String password) {
        String registeredEmail = sharedPreferences.getString(KEY_EMAIL, null);
        String registeredPassword = sharedPreferences.getString(KEY_PASSWORD, null);

        // Kiểm tra với dữ liệu đã đăng ký, nếu chưa có thì dùng giá trị mặc định
        if (registeredEmail != null && registeredPassword != null) {
            return email.equals(registeredEmail) && password.equals(registeredPassword);
        }
        // Giá trị mặc định (cho lần đầu)
        return email.equals("user@example.com") && password.equals("password123");
    }
}