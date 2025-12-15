package com.example.foodcaloriemanagementapp.APIThreading;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APISearchThread extends Thread {
    private final FoodDetails foodDetails;
    private final String baseUrl;
    private final SearchResponseViewModel viewModel;
    private final String searchkey;
    private final Activity uiActivity;
    public APISearchThread(String searchKey, Activity uiActivity, SearchResponseViewModel viewModel, FoodDetails foodDetails) {
        searchkey = searchKey;
        this.uiActivity = uiActivity;
        baseUrl ="https://api.calorieninjas.com/v1/nutrition?query=" + searchKey;
        RemoteUtilities remoteUtilities = RemoteUtilities.getInstance(uiActivity);
        this.viewModel = viewModel;
        this.foodDetails = foodDetails;
    }

    public void run() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("X-Api-Key", "UofzgRAEUWjxaaCe5q7yaA==vpJZG1TmBe8OQFnu")
                .build();

        try (Response response = client.newCall(request).execute()) {

            // Check if the response is successful (status code 200)
            if (response.isSuccessful() && response.body() != null) {
                // Extract the response body and set it in the ViewModel
                String responseData = response.body().string();

                JSONObject jsonResponse = new JSONObject(responseData);

                // Get the "items" array
                JSONArray itemsArray = jsonResponse.getJSONArray("items");

                // Get the first item in the array (assuming the array has at least one item)
                if (itemsArray.length() > 0) {
                    JSONObject foodItem = itemsArray.getJSONObject(0);

                    foodDetails.setFoodName(foodItem.getString("name"));
                    foodDetails.setCalories(foodItem.getDouble("calories"));
                    foodDetails.setServingSize(foodItem.getDouble("serving_size_g"));
                    foodDetails.setTotalFat(foodItem.getDouble("fat_total_g"));
                    foodDetails.setSaturatedFat(foodItem.getDouble("fat_saturated_g"));
                    foodDetails.setProtein(foodItem.getDouble("protein_g"));
                    foodDetails.setSodium(foodItem.getDouble("sodium_mg"));
                    foodDetails.setPotassium(foodItem.getDouble("potassium_mg"));
                    foodDetails.setCholesterol(foodItem.getDouble("cholesterol_mg"));
                    foodDetails.setTotalCarbohydrates(foodItem.getDouble("carbohydrates_total_g"));
                    foodDetails.setFiber(foodItem.getDouble("fiber_g"));
                    foodDetails.setSugar(foodItem.getDouble("sugar_g"));

                    // Set the formatted response to the ViewModel
                    viewModel.setResponse("search complete");
                }
                else
                {
                    foodDetails.setFoodName(searchkey);
                    foodDetails.setCalories(0);
                    foodDetails.setServingSize(0);
                    foodDetails.setTotalFat(0);
                    foodDetails.setSaturatedFat(0);
                    foodDetails.setProtein(0);
                    foodDetails.setSodium(0);
                    foodDetails.setPotassium(0);
                    foodDetails.setCholesterol(0);
                    foodDetails.setTotalCarbohydrates(0);
                    foodDetails.setFiber(0);
                    foodDetails.setSugar(0);

                    viewModel.setResponse("No such food items found");
                }
            }
            else
            {
                // Set the formatted response to the ViewModel
                viewModel.setResponse("unsuccessful API response");
            }
        }
        catch(IOException | JSONException e)
        {
            // Set the formatted response to the ViewModel
            viewModel.setResponse("Unexpected Error. Please check wifi Connection");

        }
    }
}
