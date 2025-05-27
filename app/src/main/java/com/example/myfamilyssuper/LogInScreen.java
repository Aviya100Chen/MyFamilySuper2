package com.example.myfamilyssuper;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInScreen extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginButton, buttonSignIn, buttonSignUp;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in_screen);

        // התאמה לגודל מסך
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // חיבור ל- Firebase
        mAuth = FirebaseAuth.getInstance();

        // קישור בין הקוד לאלמנטים ב-XML
        usernameEditText = findViewById(R.id.username1);
        passwordEditText = findViewById(R.id.password1);
        buttonSignIn = findViewById(R.id.button_V);
        buttonSignUp = findViewById(R.id.button_signup);

        // לחצן התחברות עם אימות
        buttonSignIn.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LogInScreen.this, "שם המשתמש והסיסמא שגויים. נסה שנית", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent i = new Intent(this, Start_Screen.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(LogInScreen.this, "שם המשתמש והסיסמא שגויים. נסה שנית", Toast.LENGTH_SHORT).show();
                        }
                    });
        });



        // מעבר למסך הרשמה
        buttonSignUp.setOnClickListener(view -> {
            Intent i = new Intent(this, sign_up_screen.class);
            startActivity(i);
        });
    }
}
