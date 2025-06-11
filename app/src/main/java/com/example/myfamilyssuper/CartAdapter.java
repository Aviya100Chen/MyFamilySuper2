package com.example.myfamilyssuper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private ArrayList<Product> cartItems; // רשימת המוצרים בעגלה
    private OnCartChangedListener cartChangedListener; // ממשק להאזנה לעדכון סכום כולל
    private String cartId; // מזהה העגלה במסד הנתונים

    // בנאי
    public CartAdapter(ArrayList<Product> cartItems, String cartId, OnCartChangedListener listener) {
        this.cartItems = cartItems;
        this.cartId = cartId;
        this.cartChangedListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // יצירת View לכל פריט עגלה על בסיס activity_cart_item.xml
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_cart_item, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartItems.get(position);

        // הצגת פרטי המוצר
        holder.nameTextView.setText(product.getName());
        holder.categoryTextView.setText(product.getCategory());
        holder.priceTextView.setText(String.format("%.2f ₪", product.getPrice()));

        // בדיקה אם מדובר בקטגוריה שנמדדת בק"ג
        boolean isKg = product.getCategory().equalsIgnoreCase("ירקות ופירות") ||
                product.getCategory().equalsIgnoreCase("בשר ודגים");

        // הצגת כמות בהתאם לקטגוריה
        String quantityStr = isKg
                ? String.format("%.1f ק\"ג", product.getQuantity())
                : String.format("%.0f יח'", product.getQuantity());

        // הצגת מחיר כולל למוצר (מחיר x כמות)
        holder.totalPriceTextView.setText(String.format("סה\"כ: %.2f ₪", product.getPrice() * product.getQuantity()));

        // טיפול בלחיצה על כפתור המחיקה
        holder.deleteImageView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Product removedProduct = cartItems.get(pos);

                // הסרה מרשימת המוצרים המקומית
                cartItems.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, cartItems.size());

                // עדכון במסד הנתונים (Firestore)
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference cartRef = db.collection("carts").document(cartId);
                cartRef.update("products", cartItems);

                // חישוב מחיר כולל חדש ושליחה למאזין
                if (cartChangedListener != null) {
                    double total = 0;
                    for (Product p : cartItems) {
                        total += p.getPrice() * p.getQuantity();
                    }
                    cartChangedListener.onCartChanged(total);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size(); // מספר הפריטים בעגלה
    }

    // מחזיק View עבור כל פריט ברשימה
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, categoryTextView, priceTextView, totalPriceTextView;
        ImageView deleteImageView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textView_name2_cart);
            categoryTextView = itemView.findViewById(R.id.textView_category2_cart);
            priceTextView = itemView.findViewById(R.id.textView_price2_cart);
            totalPriceTextView = itemView.findViewById(R.id.textView_totalprice2_cart);
            deleteImageView = itemView.findViewById(R.id.delete);
        }
    }

    // ממשק שמאפשר למסך שמכיל את העגלה להגיב לשינויים (לדוג' עדכון סכום כולל)
    public interface OnCartChangedListener {
        void onCartChanged(double newTotal);
    }
}
