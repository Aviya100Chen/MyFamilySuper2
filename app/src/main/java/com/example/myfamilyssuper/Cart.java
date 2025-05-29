package com.example.myfamilyssuper;

import java.util.ArrayList;

public class Cart {
    ArrayList<Product> products;
    String cartName;
    String id;
    ArrayList<String> usersID;

    public Cart(ArrayList<Product> products, String cartName, String id, ArrayList<String> usersID) {
        this.products = products;
        this.cartName = cartName;
        this.id = id;
        this.usersID = usersID;
    }

    public Cart() {
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
