package com.example.foodcaloriemanagementapp.MealActivities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodcaloriemanagementapp.APIThreading.APISearchThread;
import com.example.foodcaloriemanagementapp.APIThreading.FoodDetails;
import com.example.foodcaloriemanagementapp.APIThreading.SearchResponseViewModel;
import com.example.foodcaloriemanagementapp.R;

public class SearchForFoodActivity extends AppCompatActivity {
    SearchResponseViewModel sViewModel;
    FoodDetails foodDetails;
    ProgressBar progressBar;
    String searchFood;
    String portionSize = "100g";
    String query;
    private static final String API_URL = "https://api.calorieninjas.com/v1/nutrition?query=";
    private static final String API_KEY = "UofzgRAEUWjxaaCe5q7yaA==vpJZG1TmBe8OQFnu";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_food);
        sViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(SearchResponseViewModel.class);

        progressBar = findViewById(R.id.progressBar);
        EditText searchFoodText = findViewById(R.id.searchFoodText);
        Button searchButton = findViewById(R.id.searchButton);
        EditText portionSizeText = findViewById(R.id.portionSizeText);
        Button selectButton = findViewById(R.id.selectButton);

        progressBar.setVisibility(View.INVISIBLE);

        TextView foodNameTextView = findViewById(R.id.foodNameTextView);
        TextView caloriesTextView = findViewById(R.id.caloriesTextView);
        TextView servingSizeTextView = findViewById(R.id.servingSizeTextView);
        TextView fatTotalTextView = findViewById(R.id.fatTotalTextView);
        TextView saturatedFatTextView = findViewById(R.id.saturatedFatTextView);
        TextView proteinTextView = findViewById(R.id.proteinTextView);
        TextView sodiumTextView = findViewById(R.id.sodiumTextView);
        TextView potassiumTextView = findViewById(R.id.potassiumTextView);
        TextView cholesterolTextView = findViewById(R.id.cholesterolTextView);
        TextView carbohydrateTotalTextView = findViewById(R.id.carbohydrateTotalTextView);
        TextView fiberTextView = findViewById(R.id.fiberTextView);
        TextView sugarTextView = findViewById(R.id.sugarTextView);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchFood = searchFoodText.getText().toString();

                //check if the portion field isn't empty
                if(!TextUtils.isEmpty(portionSizeText.getText().toString()))
                {
                    portionSize = portionSizeText.getText().toString();
                }
                query = portionSize + " " + searchFood;

                // Initialize foodDetails before passing it to the thread
                foodDetails = new FoodDetails();

                try {
                    APISearchThread searchThread = new APISearchThread(query, SearchForFoodActivity.this, sViewModel, foodDetails);
                    progressBar.setVisibility(View.VISIBLE);
                    searchThread.start();
                }
                catch (RuntimeException e)
                {
                    Toast.makeText(SearchForFoodActivity.this, "Error during food item search",Toast.LENGTH_LONG).show();
                }
            }
        });

        sViewModel.response.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(SearchForFoodActivity.this, sViewModel.getResponse(),Toast.LENGTH_LONG).show();

                if (foodDetails != null) {
                    foodNameTextView.setText(foodDetails.getFoodName());
                    caloriesTextView.setText(String.valueOf(foodDetails.getCalories()));
                    servingSizeTextView.setText(String.valueOf(foodDetails.getServingSize()));
                    fatTotalTextView.setText(String.valueOf(foodDetails.getTotalFat()));
                    saturatedFatTextView.setText(String.valueOf(foodDetails.getSaturatedFat()));
                    proteinTextView.setText(String.valueOf(foodDetails.getProtein()));
                    sodiumTextView.setText(String.valueOf(foodDetails.getSodium()));
                    potassiumTextView.setText(String.valueOf(foodDetails.getPotassium()));
                    cholesterolTextView.setText(String.valueOf(foodDetails.getCholesterol()));
                    carbohydrateTotalTextView.setText(String.valueOf(foodDetails.getTotalCarbohydrates()));
                    fiberTextView.setText(String.valueOf(foodDetails.getFiber()));
                    sugarTextView.setText(String.valueOf(foodDetails.getSugar()));
                } else {
                    Toast.makeText(SearchForFoodActivity.this, "No data available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to pass the calories back to AddMealActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("foodName", foodDetails.getFoodName());
                resultIntent.putExtra("portionSize", foodDetails.getServingSize());
                resultIntent.putExtra("calories", foodDetails.getCalories());
                setResult(RESULT_OK, resultIntent);  // Set result as OK and pass the intent
                finish();  // Close the activity and return to AddMealActivity
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (foodDetails != null) {
            outState.putString("foodName", foodDetails.getFoodName());
            outState.putDouble("calories", foodDetails.getCalories());
            outState.putDouble("portionSize", foodDetails.getServingSize());
            outState.putDouble("totalFat", foodDetails.getTotalFat());
            outState.putDouble("saturatedFat", foodDetails.getSaturatedFat());
            outState.putDouble("protein", foodDetails.getProtein());
            outState.putDouble("sodium", foodDetails.getSodium());
            outState.putDouble("potassium", foodDetails.getPotassium());
            outState.putDouble("cholesterol", foodDetails.getCholesterol());
            outState.putDouble("carbohydrates", foodDetails.getTotalCarbohydrates());
            outState.putDouble("fiber", foodDetails.getFiber());
            outState.putDouble("sugar", foodDetails.getSugar());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        foodDetails = new FoodDetails();
        foodDetails.setFoodName(savedInstanceState.getString("foodName"));
        foodDetails.setCalories(savedInstanceState.getDouble("calories"));
        foodDetails.setServingSize(savedInstanceState.getDouble("portionSize"));
        foodDetails.setTotalFat(savedInstanceState.getDouble("totalFat"));
        foodDetails.setSaturatedFat(savedInstanceState.getDouble("saturatedFat"));
        foodDetails.setProtein(savedInstanceState.getDouble("protein"));
        foodDetails.setSodium(savedInstanceState.getDouble("sodium"));
        foodDetails.setPotassium(savedInstanceState.getDouble("potassium"));
        foodDetails.setCholesterol(savedInstanceState.getDouble("cholesterol"));
        foodDetails.setTotalCarbohydrates(savedInstanceState.getDouble("carbohydrates"));
        foodDetails.setFiber(savedInstanceState.getDouble("fiber"));
        foodDetails.setSugar(savedInstanceState.getDouble("sugar"));

        // Update UI elements with restored data
        updateUIWithFoodDetails();
    }

    private void updateUIWithFoodDetails() {
        if (foodDetails == null) {
            return; // Exit if foodDetails is null
        }

        TextView foodNameTextView = findViewById(R.id.foodNameTextView);
        TextView caloriesTextView = findViewById(R.id.caloriesTextView);
        TextView servingSizeTextView = findViewById(R.id.servingSizeTextView);
        TextView fatTotalTextView = findViewById(R.id.fatTotalTextView);
        TextView saturatedFatTextView = findViewById(R.id.saturatedFatTextView);
        TextView proteinTextView = findViewById(R.id.proteinTextView);
        TextView sodiumTextView = findViewById(R.id.sodiumTextView);
        TextView potassiumTextView = findViewById(R.id.potassiumTextView);
        TextView cholesterolTextView = findViewById(R.id.cholesterolTextView);
        TextView carbohydrateTotalTextView = findViewById(R.id.carbohydrateTotalTextView);
        TextView fiberTextView = findViewById(R.id.fiberTextView);
        TextView sugarTextView = findViewById(R.id.sugarTextView);

        foodNameTextView.setText(foodDetails.getFoodName());
        caloriesTextView.setText(String.valueOf(foodDetails.getCalories()));
        servingSizeTextView.setText(String.valueOf(foodDetails.getServingSize()));
        fatTotalTextView.setText(String.valueOf(foodDetails.getTotalFat()));
        saturatedFatTextView.setText(String.valueOf(foodDetails.getSaturatedFat()));
        proteinTextView.setText(String.valueOf(foodDetails.getProtein()));
        sodiumTextView.setText(String.valueOf(foodDetails.getSodium()));
        potassiumTextView.setText(String.valueOf(foodDetails.getPotassium()));
        cholesterolTextView.setText(String.valueOf(foodDetails.getCholesterol()));
        carbohydrateTotalTextView.setText(String.valueOf(foodDetails.getTotalCarbohydrates()));
        fiberTextView.setText(String.valueOf(foodDetails.getFiber()));
        sugarTextView.setText(String.valueOf(foodDetails.getSugar()));
    }
}
