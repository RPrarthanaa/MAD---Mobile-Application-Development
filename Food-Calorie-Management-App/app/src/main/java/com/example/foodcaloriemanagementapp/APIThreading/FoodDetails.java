package com.example.foodcaloriemanagementapp.APIThreading;

public class FoodDetails {
    private String foodName;
    private double calories;
    private double servingSize;
    private double totalFat;
    private double saturatedFat;
    private double protein;
    private double sodium;
    private double potassium;
    private double cholesterol;
    private double totalCarbohydrates;
    private double fiber;
    private double sugar;

    public FoodDetails(String foodName, double calories, double servingSize, double totalFat,
                       double saturatedFat, double protein, double sodium, double potassium, double cholesterol,
                       double totalCarbohydrates, double fiber, double sugar)
    {
        this.foodName = foodName;
        this.calories = calories;
        this.servingSize = servingSize;
        this.totalFat = totalFat;
        this.saturatedFat = saturatedFat;
        this.protein = protein;
        this.sodium = sodium;
        this.potassium = potassium;
        this.cholesterol = cholesterol;
        this.totalCarbohydrates = totalCarbohydrates;
        this.fiber = fiber;
        this.sugar = sugar;
    }

    public FoodDetails()
    {
        this.foodName = "";
        this.calories = 0;
        this.servingSize = 0;
        this.totalFat = 0;
        this.saturatedFat = 0;
        this.protein = 0;
        this.sodium = 0;
        this.potassium = 0;
        this.cholesterol = 0;
        this.totalCarbohydrates = 0;
        this.fiber = 0;
        this.sugar = 0;
    }

    // Getters
    public String getFoodName()
    {
        return foodName;
    }

    public double getCalories()
    {
        return calories;
    }

    public double getServingSize()
    {
        return servingSize;
    }

    public double getTotalFat()
    {
        return totalFat;
    }

    public double getSaturatedFat()
    {
        return saturatedFat;
    }

    public double getProtein()
    {
        return protein;
    }

    public double getSodium()
    {
        return sodium;
    }

    public double getPotassium()
    {
        return potassium;
    }

    public double getCholesterol()
    {
        return cholesterol;
    }

    public double getTotalCarbohydrates()
    {
        return totalCarbohydrates;
    }

    public double getFiber()
    {
        return fiber;
    }

    public double getSugar()
    {
        return sugar;
    }

    // Setters
    public void setFoodName(String foodName)
    {
        this.foodName = foodName;
    }

    public void setCalories(double calories)
    {
        this.calories = calories;
    }

    public void setServingSize(double servingSize)
    {
        this.servingSize = servingSize;
    }

    public void setTotalFat(double totalFat)
    {
        this.totalFat = totalFat;
    }

    public void setSaturatedFat(double saturatedFat)
    {
        this.saturatedFat = saturatedFat;
    }

    public void setProtein(double protein)
    {
        this.protein = protein;
    }

    public void setSodium(double sodium)
    {
        this.sodium = sodium;
    }

    public void setPotassium(double potassium)
    {
        this.potassium = potassium;
    }

    public void setCholesterol(double cholesterol)
    {
        this.cholesterol = cholesterol;
    }

    public void setTotalCarbohydrates(double totalCarbohydrates)
    {
        this.totalCarbohydrates = totalCarbohydrates;
    }

    public void setFiber(double fiber)
    {
        this.fiber = fiber;
    }

    public void setSugar(double sugar)
    {
        this.sugar = sugar;
    }
}
