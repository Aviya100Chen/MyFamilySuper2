package com.example.myfamilyssuper;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private ArrayList<Product> products;
    private Cart cart;

    public ProductAdapter(ArrayList<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // יצירת View חדש עבור כל מוצר בעזרת LayoutInflater
        View productview = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_item, parent, false);
        return new ProductViewHolder(productview);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // קישור המוצר הנוכחי למחזיק התצוגה
        Product currentProduct = products.get(position);
        Context context = holder.itemView.getContext();

        holder.categoryTextView.setText(currentProduct.getCategory());
        holder.nameTextView.setText(currentProduct.getName());
        holder.priceTextView.setText(Double.toString(currentProduct.getPrice()));

        // הגדרת יחידת מדידה לפי קטגוריה
        boolean isKg = isCategoryMeasuredInKg(currentProduct.getCategory());
        holder.updateQuantityDisplay(isKg);

        // לחצן הגדלת כמות
        holder.increaseButton.setOnClickListener(v -> {
            holder.increaseQuantity(isKg);
        });

        // לחצן הקטנת כמות
        holder.decreaseButton.setOnClickListener(v -> {
            if (!holder.decreaseQuantity(isKg)) {
                Toast.makeText(context, "לא ניתן לבחור כמות הקטנה מ-0.1", Toast.LENGTH_SHORT).show();
            }
        });

        // שינוי כמות בלחיצה על טקסט הכמות
        holder.quantityTextView.setOnClickListener(v -> {
            showQuantityInputDialog(context, holder, isKg);
        });

        // לחיצה על "הוסף לעגלה"
        holder.addToCartButton.setOnClickListener(v -> {
            if ((isKg && holder.quantity < 0.1) || (!isKg && holder.quantity < 1)) {
                Toast.makeText(context, "לא ניתן להוסיף מוצר לעגלה ללא בחירת כמות מתאימה", Toast.LENGTH_SHORT).show();
                return;
            }

            // יצירת עותק של המוצר עם כמות מתאימה
            Product productToAdd = new Product(
                    currentProduct.getCategory(),
                    currentProduct.getName(),
                    currentProduct.getPrice()
            );
            productToAdd.setQuantity(holder.quantity);

            // הוספת המוצר לעגלה ועדכון בפיירבייס
            if(cart == null){
                Toast.makeText(context, "נסה שנית", Toast.LENGTH_SHORT).show();
            }
            else{
                if(cart.getProducts() == null){
                    cart.setProducts(new ArrayList<>());
                }
                cart.getProducts().add(productToAdd);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference cartRef = db.collection("carts").document(cart.getId());
                cartRef.set(cart);
                Toast.makeText(context, "המוצר נוסף לעגלה", Toast.LENGTH_SHORT).show();
                holder.addToCartButton.setBackgroundColor(Color.parseColor("#2196F3"));
            }

        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    // בדיקה אם הקטגוריה נמדדת בק"ג
    private boolean isCategoryMeasuredInKg(String category) {
        return category.equalsIgnoreCase("ירקות ופירות") || category.equalsIgnoreCase("בשר ודגים");
    }

    // תיבת דיאלוג להזנת כמות ידנית
    private void showQuantityInputDialog(Context context, ProductViewHolder holder, boolean isKg) {
        final EditText input = new EditText(context);
        input.setInputType(isKg ? InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                : InputType.TYPE_CLASS_NUMBER);
        input.setHint("הכנס כמות " + (isKg ? "בק\"ג" : "ביחידות"));

        new AlertDialog.Builder(context)
                .setTitle("שינוי כמות")
                .setView(input)
                .setPositiveButton("אישור", (dialog, which) -> {
                    String value = input.getText().toString().trim();
                    try {
                        double quantity = Double.parseDouble(value);
                        if ((isKg && quantity >= 0.1) || (!isKg && quantity >= 1 && quantity == Math.floor(quantity))) {
                            holder.quantity = quantity;
                            holder.updateQuantityDisplay(isKg);
                        } else {
                            Toast.makeText(context, "כמות לא תקינה", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "יש להזין מספר חוקי", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryTextView;
        public TextView nameTextView;
        public TextView priceTextView;
        public TextView quantityTextView;
        public TextView increaseButton;
        public TextView decreaseButton;
        public TextView addToCartButton;

        public double quantity = 0.1;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // קישור בין משתני המחלקה לרכיבי ה-XML
            categoryTextView = itemView.findViewById(R.id.textView_category_2);
            nameTextView = itemView.findViewById(R.id.textView_name_2);
            priceTextView = itemView.findViewById(R.id.textView_price_2);
            quantityTextView = itemView.findViewById(R.id.tv_quantity);
            increaseButton = itemView.findViewById(R.id.btn_increase);
            decreaseButton = itemView.findViewById(R.id.btn_decrease);
            addToCartButton = itemView.findViewById(R.id.btn_add_to_cart);
        }

        // עדכון טקסט תצוגת הכמות לפי סוג היחידה
        public void updateQuantityDisplay(boolean isKg) {
            if (isKg) {
                quantityTextView.setText(String.format("%.1f ק\"ג", quantity));
            } else {
                quantityTextView.setText(String.format("%.0f יח'", quantity));
                quantity = Math.floor(quantity);
            }
        }

        // הגדלת כמות לפי סוג היחידה
        public void increaseQuantity(boolean isKg) {
            quantity += isKg ? 0.1 : 1;
            updateQuantityDisplay(isKg);
        }

        // הקטנת כמות אם אפשר לפי מגבלות
        public boolean decreaseQuantity(boolean isKg) {
            double min = isKg ? 0.1 : 1;
            double step = isKg ? 0.1 : 1;
            if (quantity - step >= min) {
                quantity -= step;
                updateQuantityDisplay(isKg);
                return true;
            } else {
                return false;
            }
        }
    }
}


