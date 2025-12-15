package com.example.foodcaloriemanagementapp.DatabaseManagement;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMeal(Meal meal);

    @Query("SELECT * FROM meals WHERE dateAdded = :todayDate")
    LiveData<List<Meal>> getTodayMeals(String todayDate);

    @Query("SELECT SUM(calories) FROM meals WHERE dateAdded = :todayDate")
    LiveData<Double> getTodayTotalCalories(String todayDate);

    @Query("SELECT * FROM meals ORDER BY dateAdded DESC")
    LiveData<List<Meal>> getAllMeals();

    @Query("SELECT * FROM meals WHERE dateAdded = :date")
    LiveData<List<Meal>> getMealsByDate(String date);

    @Query("SELECT SUM(calories) FROM meals WHERE dateAdded = :date")
    LiveData<Double> getTotalCaloriesByDate(String date);

    @Query("DELETE FROM meals WHERE dateAdded = :date")
    void deleteMealsByDate(String date);
}
