package com.example.foodcaloriemanagementapp.DatabaseManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.foodcaloriemanagementapp.R;

import java.util.ArrayList;
import java.util.List;

public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.MealViewHolder> {

    private List<Meal> meals = new ArrayList<>();
    private final Context context;

    public MealsAdapter(Context context) {
        this.context = context;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mealView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(mealView);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.foodNameTextView.setText(meal.getName());
        holder.portionSizeTextView.setText("Portion: " + meal.getPortionSize());
        holder.caloriesTextView.setText("Calories: " + meal.getCalories());
        holder.mealTypeTextView.setText("Type: " + meal.getMealType());
        holder.dateAddedTextView.setText("Added on: " + meal.getDisplayDate());

        if (meal.getImageUrl() != null && !meal.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(meal.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.mealImageView);
        } else {
            holder.mealImageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView, portionSizeTextView, caloriesTextView, mealTypeTextView, dateAddedTextView;
        ImageView mealImageView;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            portionSizeTextView = itemView.findViewById(R.id.portionSizeTextView);
            caloriesTextView = itemView.findViewById(R.id.caloriesTextView);
            mealTypeTextView = itemView.findViewById(R.id.mealTypeTextView);
            dateAddedTextView = itemView.findViewById(R.id.dateAddedTextView);
            mealImageView = itemView.findViewById(R.id.mealImageView);
        }
    }
}
