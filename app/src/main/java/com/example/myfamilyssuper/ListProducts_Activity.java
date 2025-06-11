package com.example.myfamilyssuper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * מסך להצגת מוצרים לפי קטגוריה.
 * נטען מה־Intent את שם הקטגוריה ואת מזהה העגלה, טוען את רשימת המוצרים ומציג אותם.
 */

public class ListProducts_Activity extends AppCompatActivity {
    private static final String COLLECTION_NAME = "Products"; // שם הקולקציה בפיירבייס
    private ArrayList<Product> products = new ArrayList<>();  // רשימת מוצרים לתצוגה
    private ProductAdapter productAdapter;
    Cart cart;
    Button backButton2;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // תמיכה בפריסת מסך מלא עם אזורים בטוחים
        setContentView(R.layout.activity_list_products);

        // התאמת תצוגה לשוליים של מערכת
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קבלת נתונים מה־Intent
        String cartId = getIntent().getExtras().getString("cartId");
        String category = getIntent().getStringExtra("category");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // הגדרת RecyclerView
        recyclerView = findViewById(R.id.recyclerView_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productAdapter = new ProductAdapter(products); // מתאם לרשימת המוצרים
        recyclerView.setAdapter(productAdapter);

        // שליפת מוצרים לפי קטגוריה מפיירבייס
        getProductsByCategory(category, new ProductRetrievalCallback() {
            @Override
            public void onSuccess(ArrayList<Product> products) {
                productAdapter.notifyDataSetChanged(); // עדכון הרשימה לאחר טעינה
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting products", Toast.LENGTH_SHORT).show();
            }
        });

        // טעינת העגלה לפי מזהה שהועבר
        DocumentReference cartRef = db.collection("carts").document(cartId);
        cartRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                cart = documentSnapshot.toObject(Cart.class);
                productAdapter.setCart(cart); // עדכון המתאם עם העגלה
            }
        });

        // כפתור חזרה לקטגוריות
        backButton2 = findViewById(R.id.back_to_category);
        backButton2.setOnClickListener(view -> {
            finish();
        });
    }

    /**
     * ממשק קריאה חזרה שמופעל לאחר טעינת מוצרים
     */
    public interface ProductRetrievalCallback {
        void onSuccess(ArrayList<Product> products);
        void onFailure(Exception e);
    }

    /**
     * טעינת מוצרים מקטגוריה מסוימת מה־Firestore
     */
    public void getProductsByCategory(String category, ProductRetrievalCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_NAME)
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Product product = document.toObject(Product.class);
                            if (product != null) {
                                products.add(product); // הוספת מוצר לרשימה
                            }
                        }
                        productAdapter.notifyDataSetChanged(); // עדכון RecyclerView
                        callback.onSuccess(products);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e);
                        Log.e("FirestoreError", "Error getting products: ", e);
                    }
                });
    }
}
