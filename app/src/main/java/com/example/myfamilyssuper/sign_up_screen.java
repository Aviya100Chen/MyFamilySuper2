package com.example.myfamilyssuper;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class sign_up_screen extends AppCompatActivity {

    EditText usernameEditText, emailEditText, birthdateEditText, passwordEditText;
    Button signUpButton, backButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // חיבור לרכיבי XML
        usernameEditText = findViewById(R.id.editTextText2);
        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        birthdateEditText = findViewById(R.id.editTextDate);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        signUpButton = findViewById(R.id.button);
        backButton = findViewById(R.id.back_button);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // לחצן חזרה
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(sign_up_screen.this, LogInScreen.class);
            startActivity(intent);
        });

        // חלונית תאריך - הגדרת OnClickListener
        birthdateEditText.setOnClickListener(view -> showDatePicker());

        // לחצן הרשמה
        signUpButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String birthdate = birthdateEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            StringBuilder errorMessage = new StringBuilder();

            // בדיקה אם כל הנתונים הוזנו נכון
            if (TextUtils.isEmpty(username)) {
                errorMessage.append("אנא הכנס שם משתמש\n");
            }

            if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                errorMessage.append("אימייל לא תקין\n");
            }

            if (TextUtils.isEmpty(birthdate)) {
                errorMessage.append("אנא הכנס תאריך לידה\n");
            }

            if (TextUtils.isEmpty(password) || password.length() < 6) {
                errorMessage.append("סיסמה חייבת להכיל לפחות 6 תווים\n");
            }

            // אם יש שגיאות, הצג הודעת שגיאה
            if (errorMessage.length() > 0) {
                Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_SHORT).show();
                return;
            }

            // הרשמה ל-Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("username", username);
                                userMap.put("email", email);
                                userMap.put("birthdate", birthdate); // שמירת תאריך הלידה

                                db.collection("users")
                                        .document(firebaseUser.getUid())
                                        .set(userMap)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "נרשמת בהצלחה", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(this, LogInScreen.class);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "שגיאה בשמירת הנתונים", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(this, "שגיאה בהרשמה: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    // פונקציה להצגת חלונית בחירת תאריך עם הגבלת השנים
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    String dateStr = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                    birthdateEditText.setText(dateStr);
                },
                year, month, day);

        // הגדרת תאריך מקסימלי ל-31.12.2025
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(2025, Calendar.DECEMBER, 31);  // 31/12/2025
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        // הגדרת תאריך מינימלי ל-01.01.1900
        Calendar minDate = Calendar.getInstance();
        minDate.set(1900, Calendar.JANUARY, 1);  // 01/01/1900
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        // הצגת התאריך
        datePickerDialog.show();
    }
}

