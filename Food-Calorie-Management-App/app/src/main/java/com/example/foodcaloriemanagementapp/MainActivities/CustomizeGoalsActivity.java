package com.example.foodcaloriemanagementapp.MainActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodcaloriemanagementapp.R;

public class CustomizeGoalsActivity extends AppCompatActivity {

    private EditText calorieGoalEditText;
    private static final String PREFS_NAME = "calorie_prefs";
    private static final String CALORIE_GOAL_KEY = "calorie_goal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_goals);

        // Initialize views
        calorieGoalEditText = findViewById(R.id.calorieGoalEditText);
        Button saveGoalButton = findViewById(R.id.saveGoalButton);

        // Load existing goal
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String existingGoal = prefs.getString(CALORIE_GOAL_KEY, "");
        calorieGoalEditText.setText(existingGoal);

        // Set up save button click listener
        saveGoalButton.setOnClickListener(v -> {
            String goal = calorieGoalEditText.getText().toString();

            if (!goal.isEmpty()) {
                // Save the goal in SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(CALORIE_GOAL_KEY, goal);
                editor.apply();

                // Pass the goal back to MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("calorie_goal", goal);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}