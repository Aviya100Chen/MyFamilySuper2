package com.example.myfamilyssuper;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Products_Activity extends AppCompatActivity {

    String cartId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // מאפשר שימוש בתצוגת מסך מלאה עם התאמה לגבולות
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_products);

        // הגדרת מרווחים עבור תצוגת קצוות (לסטטוס בר וכדומה)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קבלת מזהה העגלה שהועבר מהמסך הקודם
        cartId = getIntent().getExtras().getString("cartId");

        // הגדרת סרגל ניווט תחתון
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_products); // מסמן את המסך הנוכחי כברירת מחדל

        // הגדרת פעולות בלחיצה על אייקונים בתפריט התחתון
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_home) {
                    // מעבר למסך הבית עם העברת cartId
                    Intent intent = new Intent(Products_Activity.this, Start_Screen_Activity.class);
                    intent.putExtra("cartId", cartId);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_cart) {
                    // מעבר לעגלת הקניות עם העברת cartId
                    Intent intent = new Intent(Products_Activity.this, MyCart_Activity.class);
                    intent.putExtra("cartId", cartId);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        // כפתור קטגוריה: ירקות ופירות
        Button fruit_vege = findViewById(R.id.fruit_vege);
        fruit_vege.setOnClickListener(view -> {
            Intent i = new Intent(this, ListProducts_Activity.class);
            i.putExtra("category", "ירקות ופירות");
            i.putExtra("cartId", cartId);
            startActivity(i);
        });

        // כפתור קטגוריה: מוצרי חלב וביצים
        Button milk_eggs = findViewById(R.id.milk_eggs);
        milk_eggs.setOnClickListener(view -> {
            Intent i = new Intent(this, ListProducts_Activity.class);
            i.putExtra("category", "מוצרי חלב וביצים");
            i.putExtra("cartId", cartId);
            startActivity(i);
        });

        // כפתור קטגוריה: בשר ודגים
        Button meat_fish = findViewById(R.id.meat_fish);
        meat_fish.setOnClickListener(view -> {
            Intent i = new Intent(this, ListProducts_Activity.class);
            i.putExtra("category", "בשר ודגים");
            i.putExtra("cartId", cartId);
            startActivity(i);
        });

        // כפתור קטגוריה: לחם וקטניות
        Button breads_legumes = findViewById(R.id.breads_legumes);
        breads_legumes.setOnClickListener(view -> {
            Intent i = new Intent(this, ListProducts_Activity.class);
            i.putExtra("category", "לחם וקטניות");
            i.putExtra("cartId", cartId);
            startActivity(i);
        });

        // כפתור קטגוריה: משקאות
        Button drinks = findViewById(R.id.drinks);
        drinks.setOnClickListener(view -> {
            Intent i = new Intent(this, ListProducts_Activity.class);
            i.putExtra("category", "משקאות");
            i.putExtra("cartId", cartId);
            startActivity(i);
        });

        // כפתור קטגוריה: חטיפים וממתקים
        Button candy_snacks = findViewById(R.id.candy_snacks);
        candy_snacks.setOnClickListener(view -> {
            Intent i = new Intent(this, ListProducts_Activity.class);
            i.putExtra("category", "חטיפים וממתקים");
            i.putExtra("cartId", cartId);
            startActivity(i);
        });
    }
}
