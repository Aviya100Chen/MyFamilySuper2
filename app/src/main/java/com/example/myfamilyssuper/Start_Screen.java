package com.example.myfamilyssuper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Start_Screen extends AppCompatActivity {

    ArrayList<Cart> myCarts = new ArrayList<>();
    Spinner mySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button productsButton = findViewById(R.id.product_button);

        productsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // מעבר למסך בשם products
                Intent intent = new Intent(Start_Screen.this, ProductsActivity.class);
                startActivity(intent);
            }
        });
        Button myCart_buttonButton = findViewById(R.id.myCart_button);

        myCart_buttonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // מעבר למסך בשם myCart
                Intent intent = new Intent(Start_Screen.this, MyCart.class);
                startActivity(intent);
            }
        });


        private void readCart() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            ArrayList<Cart> cartList = new ArrayList<>();

            db.collection("carts")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Cart cart = document.toObject(Cart.class);
                            cartList.add(cart);
                        }
                        filterCart(cartList);
                    });

        }

        private void filterCart (ArrayList < Cart > cartList) {
            myCarts = new ArrayList<>();
            ArrayList<String> myCartsNames = new ArrayList<>();
            for (int i = 0; i < cartList.size(); i++) {
                for (int j = 0; j < cartList.get(i).getUsersID().size(); j++) {
                    if (cartList.get(i).getUsersID().get(j).equals(FirebaseAuth.getInstance().getUid())) {
                        myCarts.add(cartList.get(i));
                        myCartsNames.add(cartList.get(i).getCartName());
                    }
                }
            }
            if (myCarts.size() == 0) {
                myCartsNames.add("אין עגלות");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, // context (Activity)
                    android.R.layout.simple_spinner_item, // layout for each item
                    myCartsNames // your data
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mySpinner.setAdapter(adapter);
        }

        public void addCart (View view){
            TextInputEditText name = findViewById(R.id.cart_name_et);
            if (name.getText().toString().isEmpty()) {
                Toast.makeText(this, "חסר שם", Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<String> users = new ArrayList<>();
            users.add(FirebaseAuth.getInstance().getUid());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference cartRef = db.collection("carts").document();
            String generatedId = cartRef.getId();

            Cart cart = new Cart(null, name.getText().toString(), generatedId, users);
            cartRef.set(cart);
            Intent intent = new Intent(Start_Screen.this, Start_Screen.class);
            startActivity(intent);
            finish();

        }
    }
}
