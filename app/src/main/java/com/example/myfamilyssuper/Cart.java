package com.example.myfamilyssuper;

import java.util.ArrayList;

public class Cart {
    ArrayList<Product> products;
    String cartName;
    String id;
    ArrayList<String> usersID;

    // 🔧 בנאי ריק חובה עבור Firebase
    public Cart() {
        // את השדות אפשר לאתחל אם רוצים למנוע NullPointerException
        this.products = new ArrayList<>();
        this.usersID = new ArrayList<>();
    }

    public Cart(ArrayList<Product> products, String cartName, String id, ArrayList<String> usersID) {
        this.products = products;
        this.cartName = cartName;
        this.id = id;
        this.usersID = usersID;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public String getCartName() {
        return cartName;
    }

    public void setCartName(String cartName) {
        this.cartName = cartName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getUsersID() {
        return usersID;
    }

    public void setUsersID(ArrayList<String> usersID) {
        this.usersID = usersID;
    }
}
