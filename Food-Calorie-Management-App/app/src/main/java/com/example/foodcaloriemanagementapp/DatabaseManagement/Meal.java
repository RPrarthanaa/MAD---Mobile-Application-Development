package com.example.foodcaloriemanagementapp.DatabaseManagement;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meals")
public class Meal {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private double portionSize;
    private double calories;
    private String mealType;
    private String imageUrl; // URL from Firebase Storage
    private String dateAdded; // Track date
    private String displayDate;

    public Meal(String name, double portionSize, double calories, String mealType, String imageUrl, String dateAdded, String displayDate) {
        this.name = name;
        this.portionSize = portionSize;
        this.calories = calories;
        this.mealType = mealType;
        this.imageUrl = imageUrl;
        this.dateAdded = dateAdded;
        this.displayDate = displayDate;
    }

    // Getters and Setters for all fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPortionSize() { return portionSize; }
    public void setPortionSize(double portionSize) { this.portionSize = portionSize; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }

    public String getDisplayDate() { return displayDate; }
    public void setDisplayDate(String displayDate) { this.displayDate = displayDate; }
}
