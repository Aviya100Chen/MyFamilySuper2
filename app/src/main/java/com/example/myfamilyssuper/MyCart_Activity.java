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

public class MyCart_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceTextView;
    private ArrayList<Product> cartItems;
    private String cartId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_cart);

        // טיפול בהתאמת שוליים למכשירים שונים (לשוליים של מערכת ההפעלה)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קבלת מזהה העגלה (cartId) מה-Intent
        cartId = getIntent().getExtras().getString("cartId");

        // הגדרת הניווט התחתון
        setupBottomNavigation();

        // טעינת העגלה ממסד הנתונים (Firebase Firestore)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference cartRef = db.collection("carts").document(cartId);

        cartRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Cart cart = documentSnapshot.toObject(Cart.class);
                if (cart != null) {
                    setUI(cart); // עדכון ממשק המשתמש עם נתוני העגלה
                }
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_cart);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Intent intent = null;

                // מעבר בין מסכים לפי הפריט שנבחר בניווט התחתון
                if (itemId == R.id.menu_home) {
                    intent = new Intent(MyCart_Activity.this, Start_Screen_Activity.class);
                } else if (itemId == R.id.menu_products) {
                    intent = new Intent(MyCart_Activity.this, Products_Activity.class);
                }

                if (intent != null) {
                    intent.putExtra("cartId", cartId); // העברת מזהה העגלה למסך הבא
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    private void setUI(Cart cart) {
        recyclerView = findViewById(R.id.recyclerView_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        totalPriceTextView = findViewById(R.id.textView_total_2);

        // במידה ואין מוצרים בעגלה, אתחול הרשימה
        if (cart.getProducts() == null) {
            cart.setProducts(new ArrayList<>());
        }

        cartItems = cart.getProducts();

        // יצירת Adapter עם cartId וממשק שמעדכן את סך המחיר הכולל
        cartAdapter = new CartAdapter(cartItems, cartId, new CartAdapter.OnCartChangedListener() {
            @Override
            public void onCartChanged(double newTotal) {
                totalPriceTextView.setText("₪" + String.format("%.2f", newTotal)); // עדכון סכום כולל
            }
        });

        recyclerView.setAdapter(cartAdapter);

        // הצגת סכום ראשוני של העגלה
        updateTotalPrice();

        // הצגת שם העגלה
        TextView nameT = findViewById(R.id.cart_name);
        nameT.setText(cart.getCartName());
    }

    private void updateTotalPrice() {
        double totalPrice = 0;

        // חישוב סכום כולל של המוצרים בעגלה לפי כמות ומחיר
        for (Product product : cartItems) {
            totalPrice += product.getPrice() * product.getQuantity();
        }

        totalPriceTextView.setText("₪" + String.format("%.2f", totalPrice)); // תצוגת מחיר סופי
    }
}
