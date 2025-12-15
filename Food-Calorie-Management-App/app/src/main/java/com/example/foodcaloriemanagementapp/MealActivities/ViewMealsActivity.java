package com.example.foodcaloriemanagementapp.MealActivities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodcaloriemanagementapp.DatabaseManagement.AppDatabase;
import com.example.foodcaloriemanagementapp.DatabaseManagement.Meal;
import com.example.foodcaloriemanagementapp.DatabaseManagement.MealDao;
import com.example.foodcaloriemanagementapp.DatabaseManagement.MealsAdapter;
import com.example.foodcaloriemanagementapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewMealsActivity extends AppCompatActivity {

    private MealsAdapter adapter;
    private MealDao mealDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meals);

        Button viewPreviousDayButton = findViewById(R.id.viewPreviousDayButton);
        Button backButton = findViewById(R.id.backButton);
        RecyclerView recyclerView = findViewById(R.id.mealsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MealsAdapter(this);
        recyclerView.setAdapter(adapter);

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        mealDao = db.mealDao();

        // Display today's meals by default
        loadMealsForDate(getCurrentDate());

        viewPreviousDayButton.setOnClickListener(v -> {
            // Load meals for the previous day
            SharedPreferences prefs = getSharedPreferences("calorie_prefs", MODE_PRIVATE);
            String previousDay = prefs.getString("previous_day", null);
            if (previousDay != null) {
                loadMealsForDate(previousDay);
            } else {
                Toast.makeText(this, "No data for the previous day", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    // Load meals depending on date
    private void loadMealsForDate(String date) {
        mealDao.getMealsByDate(date).observe(this, new Observer<List<Meal>>() {
            @Override
            public void onChanged(List<Meal> meals) {
                adapter.setMeals(meals);
            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(new Date());
    }
}
