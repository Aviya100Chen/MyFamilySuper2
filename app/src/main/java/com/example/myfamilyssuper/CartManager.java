package com.example.myfamilyssuper;

import java.util.ArrayList;

public class CartManager {
    private static CartManager instance;
    private ArrayList<Product> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Product product) {
        cartItems.add(product);
    }

    public ArrayList<Product> getCartItems() {
        return new ArrayList<>(cartItems); // מחזיר עותק כדי למנוע שינוי ישיר מבחוץ
    }

    public void clearCart() {
        cartItems.clear();
    }

    public void removeFromCart(Product product) {
        cartItems.remove(product);
    }

    public double getTotalPrice() {
        double total = 0;
        for (Product p : cartItems) {
            total += p.getPrice() * p.getQuantity();
        }
        return total;
    }
}
