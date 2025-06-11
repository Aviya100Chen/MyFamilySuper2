package com.example.myfamilyssuper;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class sign_up_screen_Activity extends AppCompatActivity {

    EditText usernameEditText;
    EditText emailEditText;
    EditText birthdateEditText;
    EditText passwordEditText;
    Button signUpButton;
    Button backButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // הפעלת מצב EdgeToEdge להצגת התוכן מקצה לקצה
        setContentView(R.layout.activity_sign_up_screen); // קישור בין הקוד לפריסת ה-XML של מסך ההרשמה

        // הגדרת מרווחים לפי אזורי מערכת (סטטוס בר, ניווט וכו')
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // חיבור לרכיבי XML
        usernameEditText = findViewById(R.id.input_name);
        emailEditText = findViewById(R.id.input_email_signup);
        birthdateEditText = findViewById(R.id.input_date);
        passwordEditText = findViewById(R.id.input_password_signup);
        signUpButton = findViewById(R.id.button);
        backButton = findViewById(R.id.back_button);

        // אתחול Firebase Authentication ו-Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // לחצן חזרה
        backButton.setOnClickListener(view -> {
            // מעבר חזרה למסך ההתחברות
            Intent intent = new Intent(sign_up_screen_Activity.this, LogInScreen_Activity.class);
            startActivity(intent);
        });

        // חלונית תאריך - הגדרת OnClickListener
        birthdateEditText.setOnClickListener(view -> showDatePicker());

        // לחצן הרשמה
        signUpButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString().trim(); // קבלת שם משתמש
            String email = emailEditText.getText().toString().trim(); // קבלת אימייל
            String birthdate = birthdateEditText.getText().toString().trim(); // קבלת תאריך לידה
            String password = passwordEditText.getText().toString().trim(); // קבלת סיסמה

            StringBuilder errorMessage = new StringBuilder(); // משתנה לאגירת הודעות שגיאה

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
                            // שמירת אימייל בזיכרון מקומי (SharedPreferences)
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user_email", email);
                            editor.apply();

                            FirebaseUser firebaseUser = mAuth.getCurrentUser(); // קבלת המשתמש שנוצר
                            if (firebaseUser != null) {
                                // יצירת Map לשמירת נתוני המשתמש במסד הנתונים
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("username", username);
                                userMap.put("email", email);
                                userMap.put("birthdate", birthdate); // שמירת תאריך הלידה

                                // שמירת הנתונים במסמך של המשתמש במסד Firestore
                                db.collection("Users")
                                        .document(firebaseUser.getUid())
                                        .set(userMap)
                                        .addOnSuccessListener(aVoid -> {
                                            // הודעת הצלחה ומעבר למסך ההתחברות
                                            Toast.makeText(this, "נרשמת בהצלחה", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(this, LogInScreen_Activity.class);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            // הודעת שגיאה אם שמירת הנתונים נכשלה
                                            Toast.makeText(this, "שגיאה בשמירת הנתונים", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            // שגיאה בתהליך ההרשמה (כגון אימייל תפוס)
                            Toast.makeText(this, "שגיאה בהרשמה: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    // פונקציה להצגת חלונית בחירת תאריך עם הגבלת השנים
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance(); // קבלת תאריך נוכחי
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    // עיצוב התאריך שנבחר בפורמט dd/MM/yyyy
                    String dateStr = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                    birthdateEditText.setText(dateStr);
                },
                year, month, day);

        // הגדרת תאריך מקסימלי ל-31.12.2025
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(2025, Calendar.DECEMBER, 31);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        // הגדרת תאריך מינימלי ל-01.01.1900
        Calendar minDate = Calendar.getInstance();
        minDate.set(1900, Calendar.JANUARY, 1);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        // הצגת חלונית בחירת התאריך
        datePickerDialog.show();
    }
}
