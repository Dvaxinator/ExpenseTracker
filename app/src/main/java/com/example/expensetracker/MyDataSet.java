package com.example.expensetracker;

public class MyDataSet {
    public String description;
    public double amount;
    public String category;
    public String date;
    public String key;

    public MyDataSet() {
    }

    public MyDataSet(String description, double amount, String category, String date) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.key = "";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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