package com.example.myfamilyssuper;

public class Product {
    private String category;
    private String image;
    private String name;
    private double price;
    private double quantity; // הכמות של המוצר (ביחידות או ק"ג בהתאם לקטגוריה)

    public Product(String category, String image, String name, double price) {
        this.category = category;
        this.image = image;
        this.name = name;
        this.price = price;
        this.quantity = getDefaultQuantity(); // ברירת מחדל לפי הקטגוריה
    }

    public Product() {
        this.quantity = getDefaultQuantity();
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    // פונקציה לקביעת ברירת מחדל של כמות לפי הקטגוריה
    private double getDefaultQuantity() {
        if (category != null && (category.equals("ירקות ופירות") || category.equals("בשר ודגים"))) {
            return 0.1; // משקל מינימלי בק"ג
        } else {
            return 1; // יחידה אחת
        }
    }
}
