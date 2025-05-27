package com.example.myfamilyssuper;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Activity להצגת מוצרים לפי קטגוריה.
 * שונה שם המחלקה מ-list_products ל-ListProducts בהתאם לקונבנציה בג'אווה.
 * אין שינוי בלוגיקה – רק קריאה לשם Activity מסודרת.
 */
public class ListProducts extends AppCompatActivity {
    private static final String TAG = "GetProductData";
    private static final String COLLECTION_NAME = "Products";
    private static final String DOCUMENT_ID = "2C5VncQZvZBMSuIPXht6";

    private ArrayList<Product> products = new ArrayList<>();
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_products);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קבלת קטגוריה מה־Intent
        String category = getIntent().getStringExtra("category");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // הגדרת RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView_products);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        productAdapter = new ProductAdapter(products);
        recyclerView.setAdapter(productAdapter);

        // שליפת מוצרים לפי קטגוריה
        getProductsByCategory(category, new ProductRetrievalCallback() {
            @Override
            public void onSuccess(ArrayList<Product> products) {
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ממשק להחזרת מוצרים באופן אסינכרוני
    public interface ProductRetrievalCallback {
        void onSuccess(ArrayList<Product> products);
        void onFailure(Exception e);
    }

    // פונקציית שליפה מפיירבייס לפי קטגוריה
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
                                products.add(product);
                            }
                        }
                        productAdapter.notifyDataSetChanged();
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
