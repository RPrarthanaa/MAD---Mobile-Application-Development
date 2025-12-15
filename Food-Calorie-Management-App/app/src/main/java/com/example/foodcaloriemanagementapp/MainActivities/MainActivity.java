package com.example.foodcaloriemanagementapp.MainActivities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.foodcaloriemanagementapp.DatabaseManagement.AppDatabase;
import com.example.foodcaloriemanagementapp.DatabaseManagement.MealDao;
import com.example.foodcaloriemanagementapp.MealActivities.AddMealActivity;
import com.example.foodcaloriemanagementapp.MealActivities.ViewMealsActivity;
import com.example.foodcaloriemanagementapp.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String CALORIE_GOAL_KEY = "calorie_goal_key";
    private static final String PREFS_NAME = "calorie_prefs";
    private static final String LAST_TRACKED_DATE_KEY = "last_tracked_date";
    private static final String PREVIOUS_DAY_KEY = "previous_day";
    private static final int REQUEST_CODE_GOAL = 1;
    private TextView calorieSummaryTextView;
    private int dailyCalorieGoal = 2000;
    private MealDao mealDao;
    private CircularProgressIndicator circularProgressIndicator;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        calorieSummaryTextView = findViewById(R.id.calorieSummaryTextView);
        circularProgressIndicator = findViewById(R.id.circularProgressIndicator);
        Button customizeGoalsButton = findViewById(R.id.customizeGoalsButton);
        Button addMealButton = findViewById(R.id.addMealButton);
        Button viewMealsButton = findViewById(R.id.viewMealsButton);

        // Initialize Room database
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        mealDao = db.mealDao();

        // Restore the calorie goal if available
        if (savedInstanceState != null) {
            dailyCalorieGoal = savedInstanceState.getInt(CALORIE_GOAL_KEY, 2000);
        }

        // Check if a new day has started
        checkForNewDay();

        // Set initial max for the progress indicator
        circularProgressIndicator.setMax(dailyCalorieGoal);

        // Initial display for calorie summary
        showInitialCalorieSummary();

        LiveData<Double> todayCaloriesLiveData = mealDao.getTodayTotalCalories(getCurrentDate());
        todayCaloriesLiveData.observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double currentCalories) {
                if (currentCalories == null) {
                    currentCalories = 0.0;
                }
                updateProgress(currentCalories);
            }
        });

        // Set up button click listeners
        customizeGoalsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CustomizeGoalsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_GOAL);
        });

        addMealButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddMealActivity.class);
            startActivity(intent);
        });

        viewMealsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewMealsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GOAL && resultCode == RESULT_OK) {
            if (data != null) {
                String goal = data.getStringExtra("calorie_goal");
                if (goal != null && !goal.isEmpty()) {
                    try {
                        dailyCalorieGoal = Integer.parseInt(goal);
                        circularProgressIndicator.setMax(dailyCalorieGoal);
                        LiveData<Double> todayCaloriesLiveData = mealDao.getTodayTotalCalories(getCurrentDate());
                        Double currentCalories = todayCaloriesLiveData.getValue();
                        if (currentCalories == null) {
                            currentCalories = 0.0;
                        }
                        updateProgress(currentCalories);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Checks if it is a new day and resets calorie counter
    private void checkForNewDay() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String lastTrackedDate = prefs.getString(LAST_TRACKED_DATE_KEY, null);
        String todayDate = getCurrentDate();

        if (lastTrackedDate == null || !lastTrackedDate.equals(todayDate)) {
            // Save the current day as the previous day
            prefs.edit().putString(PREVIOUS_DAY_KEY, lastTrackedDate).apply();

            // Update the last tracked date to today
            prefs.edit().putString(LAST_TRACKED_DATE_KEY, todayDate).apply();

            // Perform database operations on a background thread
            executor.execute(() -> {
                // Reset daily meals for the new day
                mealDao.deleteMealsByDate(todayDate);
                runOnUiThread(this::resetCalorieCounter);
            });
        }
    }

    private void showInitialCalorieSummary() {
        LiveData<Double> todayCaloriesLiveData = mealDao.getTodayTotalCalories(getCurrentDate());
        todayCaloriesLiveData.observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double currentCalories) {
                if (currentCalories == null) {
                    currentCalories = 0.0;
                }
                updateProgress(currentCalories);
            }
        });
    }

    // Update the TextView with current calorie count
    private void updateProgress(Double currentCalories) {
        int caloriesInt = currentCalories.intValue();
        calorieSummaryTextView.setText(caloriesInt + " cal / " + dailyCalorieGoal + " cal");

        // Determine color based on the current progress
        int color;
        double progressPercentage = (currentCalories / dailyCalorieGoal) * 100;
        if (progressPercentage <= 50) {
            color = getResources().getColor(R.color.calorie_green, getTheme());
        } else if (progressPercentage > 50 && progressPercentage <= 100) {
            color = getResources().getColor(R.color.calorie_orange, getTheme());
        } else {
            color = getResources().getColor(R.color.calorie_red, getTheme());
        }

        circularProgressIndicator.setIndicatorColor(color);
        animateProgress(currentCalories.intValue());
    }

    // Set the animation on the actual progress value (calories)
    private void animateProgress(int targetProgress) {
        ObjectAnimator animator = ObjectAnimator.ofInt(circularProgressIndicator, "progress", targetProgress);
        animator.setDuration(1000); // 1-second duration
        animator.start();
    }

    // Returns the current date in "yyyy-MM-dd" format
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(new Date());
    }

    private void resetCalorieCounter() {
        calorieSummaryTextView.setText("0 cal / " + dailyCalorieGoal + " cal");
        circularProgressIndicator.setProgress(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showInitialCalorieSummary();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CALORIE_GOAL_KEY, dailyCalorieGoal);
    }
}