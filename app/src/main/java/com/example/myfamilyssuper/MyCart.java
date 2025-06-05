package com.example.myfamilyssuper;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyCart extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_cart);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String cartId = getIntent().getExtras().getString("cartId"); // Replace with the actual ID you're looking for

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_cart);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_home) {

                    Intent intent = new Intent(MyCart.this, Start_ScreenActivity.class);
                    intent.putExtra("cartId",cartId);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_products) {

                    Intent intent = new Intent(MyCart.this, ProductsActivity.class);
                    intent.putExtra("cartId",cartId);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference cartRef = db.collection("carts").document(cartId);

        cartRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Cart cart = documentSnapshot.toObject(Cart.class);
                if (cart != null) {
                    setUI(cart);
                }
            } else {

            }
        });


    }

    private void setUI(Cart cart) {
        recyclerView = findViewById(R.id.recyclerView_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(cart.getProducts()==null){
            cart.setProducts(new ArrayList<>());
        }
        ArrayList<Product> cartItems = cart.getProducts();
        cartAdapter = new CartAdapter(cartItems);
        recyclerView.setAdapter(cartAdapter);

        // הצגת הסכום הכולל
        totalPriceTextView = findViewById(R.id.textView_total_2);
        double totalPrice = 0;
        for (int i = 0; i < cart.getProducts().size(); i++) {
            totalPrice += cart.getProducts().get(i).getPrice();
        }
        totalPriceTextView.setText("₪" + String.format("%.2f", totalPrice));
        TextView nameT = findViewById(R.id.cart_name);
        nameT.setText(cart.getCartName());
    }
}


