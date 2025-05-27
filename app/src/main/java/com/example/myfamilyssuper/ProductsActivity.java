package com.example.myfamilyssuper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProductsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_products);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button fruit_vege = (Button)findViewById(R.id.fruit_vege);
        fruit_vege.setOnClickListener(view -> {
            Intent i = new Intent(this, ListProducts.class);
            i.putExtra("category","ירקות ופירות");
            startActivity(i);
        });


        Button milk_eggs = (Button)findViewById(R.id.milk_eggs);
        milk_eggs.setOnClickListener(view -> {
            Intent i = new Intent(this, ListProducts.class);
            i.putExtra("category","מוצרי חלב וביצים");
            startActivity(i);
        });

        Button meat_fish = (Button)findViewById(R.id.meat_fish);
        meat_fish.setOnClickListener(view -> {
            Intent i = new Intent(this, ListProducts.class);
            i.putExtra("category","בשר ודגים");
            startActivity(i);
        });




    }
}