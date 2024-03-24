package com.example.expensetracker;

public class MyDataSet {
    public String description;
    public double amount;
    public String category;
    public String date;
    public int image;

    public MyDataSet(int image, String description, double amount, String category, String date) {
        this.image = image;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public int getImage(){
        return  image;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }
}