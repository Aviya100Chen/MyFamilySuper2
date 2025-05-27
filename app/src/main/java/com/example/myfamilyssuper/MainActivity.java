package com.example.myfamilyssuper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handler כדי להמתין 4 שניות ולאחר מכן לעבור למסך הבא
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // מעבר ל-SecondActivity אחרי 4 שניות
                Intent intent = new Intent(MainActivity.this, LogInScreen.class);
                startActivity(intent);
                finish();  // מסיים את הפעילות הנוכחית כדי למנוע חזרה אליה
            }
        }, 4000);  // 4000 מילי-שניות = 4 שניות
    }
}
