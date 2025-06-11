package com.example.myfamilyssuper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class LogInScreen_Activity extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;
    Button buttonSignUp;
    Button buttonSignIn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // הפעלת מצב EdgeToEdge להצגת התוכן בקצה המסך
        setContentView(R.layout.activity_log_in_screen); // קישור לקובץ ה-XML של מסך ההתחברות

        // התאמה לגודל מסך
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            // אם המשתמש כבר מחובר, מעבר אוטומטי למסך הבית
            Intent i = new Intent(this, Start_Screen_Activity.class);
            startActivity(i);
        }

        // חיבור ל- Firebase
        mAuth = FirebaseAuth.getInstance();
        // קישור בין הקוד לאלמנטים ב-XML
        usernameEditText = findViewById(R.id.input_email);
        passwordEditText = findViewById(R.id.input_password);
        buttonSignIn = findViewById(R.id.button_V);
        buttonSignUp = findViewById(R.id.button_signup);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE); // גישה להגדרות ששמרו מקומית
        String savedEmail = sharedPreferences.getString("user_email", ""); // שליפת האימייל האחרון שהוזן
        usernameEditText.setText(savedEmail); // הצגת האימייל האחרון שהוזן בשדה

        // לחצן התחברות עם אימות
        buttonSignIn.setOnClickListener(view -> {

            String username = usernameEditText.getText().toString().trim(); // קבלת שם משתמש
            String password = passwordEditText.getText().toString().trim(); // קבלת סיסמה

            if (username.isEmpty() || password.isEmpty()) {
                // אם אחד מהשדות ריק - הצגת שגיאה
                Toast.makeText(LogInScreen_Activity.this, "שם המשתמש והסיסמא שגויים. נסה שנית", Toast.LENGTH_SHORT).show();
                return;
            }

            // ניסיון התחברות עם Firebase Auth
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // התחברות הצליחה - שמירת האימייל בהגדרות
                            SharedPreferences sharedPreferences1 = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences1.edit();
                            editor.putString("user_email", username);  // שמירת שם המשתמש לכניסות עתידיות
                            editor.apply();
                            FirebaseUser user = mAuth.getCurrentUser(); // קבלת המשתמש המחובר
                            Intent i = new Intent(this, Start_Screen_Activity.class); // מעבר למסך הראשי
                            startActivity(i);
                            finish(); // סיום הפעילות הנוכחית כך שלא ניתן לחזור אליה
                        } else {
                            // התחברות נכשלה - הצגת הודעת שגיאה
                            Toast.makeText(LogInScreen_Activity.this, "שם המשתמש והסיסמא שגויים. נסה שנית", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // מעבר למסך הרשמה
        buttonSignUp.setOnClickListener(view -> {
            Intent i = new Intent(this, sign_up_screen_Activity.class); // פתיחת מסך הרשמה
            startActivity(i);
        });
    }
}

