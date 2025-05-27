package com.example.myfamilyssuper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private ArrayList<Product> cartItems;

    public CartAdapter(ArrayList<Product> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_cart_item, parent, false); // העיצוב החדש שלך
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartItems.get(position);

        holder.nameTextView.setText(product.getName());
        holder.categoryTextView.setText(product.getCategory());
        holder.priceTextView.setText(String.format("%.2f ₪", product.getPrice()));

        boolean isKg = product.getCategory().equalsIgnoreCase("ירקות ופירות") ||
                product.getCategory().equalsIgnoreCase("בשר ודגים");

        String quantityStr = isKg
                ? String.format("%.1f ק\"ג", product.getQuantity())
                : String.format("%.0f יח'", product.getQuantity());

        holder.totalPriceTextView.setText(String.format("סה\"כ: %.2f ₪", product.getPrice() * product.getQuantity()));

        // אם בעתיד תשתמשי בתמונות - אפשר לשים כאן
        // holder.imageView.setImageResource(...);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, categoryTextView, priceTextView, totalPriceTextView;
        ImageView imageView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textView_name2_cart);
            categoryTextView = itemView.findViewById(R.id.textView_category2_cart);
            priceTextView = itemView.findViewById(R.id.textView_price2_cart);
            totalPriceTextView = itemView.findViewById(R.id.textView_totalprice2_cart);
            imageView = itemView.findViewById(R.id.imageView_cart);
        }
    }
}
