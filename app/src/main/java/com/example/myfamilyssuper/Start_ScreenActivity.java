package com.example.myfamilyssuper;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class Start_ScreenActivity extends AppCompatActivity {

    ArrayList<Cart> myCarts = new ArrayList<>();
    ArrayList<Cart> cartList = new ArrayList<>();
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
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mySpinner = findViewById(R.id.spinner);
        readCart();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_products) {
                    if(myCarts.size() == 0){
                        Toast.makeText(Start_ScreenActivity.this,"יש ליצור עגלה", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Intent intent = new Intent(Start_ScreenActivity.this, ProductsActivity.class);
                    intent.putExtra("cartId",myCarts.get(mySpinner.getSelectedItemPosition()).getId());
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_cart) {
                    if(myCarts.size() == 0){
                        Toast.makeText(Start_ScreenActivity.this,"יש ליצור עגלה", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Intent intent = new Intent(Start_ScreenActivity.this, MyCart.class);
                    intent.putExtra("cartId",myCarts.get(mySpinner.getSelectedItemPosition()).getId());
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_about) {
            // Show "About" dialog
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_about);
            dialog.show();
            return true;

        } else if (id == R.id.menu_log_out) {
            // Handle logout
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LogInScreen.class));
            finishAffinity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void readCart () {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("carts").get().addOnSuccessListener(queryDocumentSnapshots -> {
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
        Intent intent = new Intent(Start_ScreenActivity.this, Start_ScreenActivity.class);
        startActivity(intent);
        finish();

    }

    public void shareClick(View view) {
        if(myCarts.size() == 0){
            Toast.makeText(Start_ScreenActivity.this,"יש ליצור עגלה", Toast.LENGTH_SHORT).show();
            return;
        }
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.share_dialog);
        Button button = dialog.findViewById(R.id.button_submit_share);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText emailT = dialog.findViewById(R.id.email_share);
                String email = emailT.getText().toString();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            boolean happend = false;
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                Map<String, Object> userData = document.getData();
                                String userEmail = (String) userData.get("email");
                                if(!userEmail.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                    if(userEmail.equals(email)){
                                        if(!myCarts.get(mySpinner.getSelectedItemPosition()).getUsersID().contains(email)){
                                            happend = true;
                                            myCarts.get(mySpinner.getSelectedItemPosition()).getUsersID().add(document.getId());
                                            Toast.makeText(Start_ScreenActivity.this,"העגלה שותפה בהצלחה", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            happend = true;
                                            Toast.makeText(Start_ScreenActivity.this,"המשתמש הזה כבר משותף עם העגלה הזו", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                            }
                            if(happend){
                                DocumentReference cartRef = db.collection("carts").document(myCarts.get(mySpinner.getSelectedItemPosition()).getId());
                                cartRef.set(myCarts.get(mySpinner.getSelectedItemPosition()));
                            }
                            else{
                                Toast.makeText(Start_ScreenActivity.this,"האימייל לא קיים", Toast.LENGTH_SHORT).show();
                            }

                            // Do something with allUsers list (e.g., update UI)
                        });
            }
        });

        dialog.show();
    }



}


