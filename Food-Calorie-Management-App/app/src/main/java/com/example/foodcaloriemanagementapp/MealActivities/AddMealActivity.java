package com.example.foodcaloriemanagementapp.MealActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodcaloriemanagementapp.DatabaseManagement.AppDatabase;
import com.example.foodcaloriemanagementapp.DatabaseManagement.Meal;
import com.example.foodcaloriemanagementapp.DatabaseManagement.MealDao;
import com.example.foodcaloriemanagementapp.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddMealActivity extends AppCompatActivity {
    ImageView imageView;
    EditText foodNameEditText, portionSizeEditText, caloriesEditText;
    RadioGroup mealTypeRadioGroup;
    Uri imageUri = null;
    String imageUrl = null;
    Bitmap image;
    int option = 0; // Variable to determine which image is being used

    private MealDao mealDao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private ProgressDialog progressDialog;

    // Request code to identify result from SearchForFoodActivity
    private static final int SEARCH_FOOD_REQUEST_CODE = 100;

    // Initialize the ActivityResultLauncher for picking images from Gallery
    ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imageView.setImageURI(imageUri); // display the image
                }
            }
    );

    // Initialize the ActivityResultLauncher for capturing images from Camera
    ActivityResultLauncher<Intent> thumbNailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    image = (Bitmap) data.getExtras().get("data");
                    if (image != null) {
                        imageView.setImageBitmap(image); // display the image
                    }
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        imageView = findViewById(R.id.mealImageView);
        foodNameEditText = findViewById(R.id.foodNameEditText);
        portionSizeEditText = findViewById(R.id.portionSizeEditText);
        caloriesEditText = findViewById(R.id.caloriesEditText);
        Button logMealButton = findViewById(R.id.logMealButton);
        mealTypeRadioGroup = findViewById(R.id.mealTypeRadioGroup);
        Button captureButton = findViewById(R.id.takePhotoButton);
        Button selectFromGalleryButton = findViewById(R.id.selectFromGalleryButton);
        Button searchFoodButton = findViewById(R.id.searchFoodButton);
        Button backButton = findViewById(R.id.backButton);

        // Initialize Room database
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        mealDao = db.mealDao();

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Photo...");
        progressDialog.setCancelable(false);

        // Restore previous state when orientation changes
        if (savedInstanceState != null) {
            option = savedInstanceState.getInt("OPTION");
            if (option == 1) { // Image bitmap
                image = savedInstanceState.getParcelable("BITMAP");
                imageView.setImageBitmap(image);
            } else {
                String uriString = savedInstanceState.getString("imageUri");
                if (uriString != null) { // Image Uri
                    imageUri = Uri.parse(uriString);
                    imageView.setImageURI(imageUri);
                }
            }
        }

        // Log Meal Button
        logMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logMeal();
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                option = 1;
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                thumbNailLauncher.launch(intent);
            }
        });

        // Select from Gallery Button
        selectFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                option = 2;
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            }
        });

        // Search food details from API
        searchFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddMealActivity.this, SearchForFoodActivity.class);
                startActivityForResult(intent, SEARCH_FOOD_REQUEST_CODE);  // Request result from search activity
            }
        });

        // Exit page
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    // Receive the data back from SearchForFoodActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if this is the result from SearchForFoodActivity
        if (requestCode == SEARCH_FOOD_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            // Retrieve the name from the intent
            String name = data.getStringExtra("foodName");

            // Retrieve the calories from the Intent
            double calories = data.getDoubleExtra("calories", 0);

            // Retrieve the portion size from the intent
            double portionSize = data.getDoubleExtra("portionSize", 100);

            // Display the information in the EditText
            caloriesEditText.setText(String.valueOf(calories));
            foodNameEditText.setText(name);
            portionSizeEditText.setText(String.valueOf(portionSize));

            // Show a toast to notify the user
            Toast.makeText(this, "Calories retrieved: " + calories, Toast.LENGTH_SHORT).show();
        }
    }

    // Log the food details
    private void logMeal()
    {
        String foodName = foodNameEditText.getText().toString();
        String portionSizeStr = portionSizeEditText.getText().toString();
        String caloriesStr = caloriesEditText.getText().toString();
        int selectedMealTypeId = mealTypeRadioGroup.getCheckedRadioButtonId();

        // Validate all fields are filled
        if (TextUtils.isEmpty(foodName) || TextUtils.isEmpty(portionSizeStr) ||
                TextUtils.isEmpty(caloriesStr) || selectedMealTypeId == -1) {
            Toast.makeText(this, "Please fill out all fields and select a meal type", Toast.LENGTH_SHORT).show();
            return;
        }

        double mealPortionSize;
        double mealCalories;
        try { // Convert strings to doubles
            mealPortionSize = Double.parseDouble(portionSizeStr);
            mealCalories = Double.parseDouble(caloriesStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for portion size and calories", Toast.LENGTH_SHORT).show(); // Fixed typo
            return;
        }

        // Obtain meal type from Radio Button text
        RadioButton selectedMealTypeButton = findViewById(selectedMealTypeId);
        String mealType = selectedMealTypeButton.getText().toString();

        // Check if an image is selected
        if (imageUri == null && image == null) {
            // Log meal without image if no image is captured or selected from gallery
            String currentDate = getCurrentDate();
            String displayDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(new Date());
            Meal meal = new Meal(foodName, mealPortionSize, mealCalories, mealType, null, currentDate, displayDate);
            insertMeal(meal);
            Toast.makeText(this, "Meal logged!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            uploadImageAndSaveMeal(foodName, mealPortionSize, mealCalories, mealType, imageUri);
        }
    }

    // Upload image to cloud server
    private void uploadImageAndSaveMeal(String name, double portionSize, double calories, String mealType, Uri uri) {
        progressDialog.show();

        // Create a unique image name that includes the meal details
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.US).format(new Date());
        String formattedPortionSize = String.format(Locale.US, "%.0f", portionSize);
        String formattedCalories = String.format(Locale.US, "%.0f", calories);
        String imageName = name + "_" + timeStamp + "_" + formattedPortionSize + "g_" + mealType + "_" + formattedCalories + "cal.jpg";

        // Initialize Firebase Storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("meal_images/" + imageName);

        // Convert image to byte array
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = null;
            if (option == 0)
                return;
            else if (option == 1) { // if image from camera
                bitmap = image;
            }
            else if (option == 2) { // if image from gallery
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] data = baos.toByteArray();

            // Upload the image
            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnProgressListener(snapshot -> {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setMessage("Uploading Photo: " + (int) progress + "%");
            }).addOnSuccessListener(taskSnapshot -> {
                // Get the download URL
                storageRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    imageUrl = uri1.toString();

                    // Get current and display dates
                    String currentDate = getCurrentDate();
                    String displayDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(new Date());

                    // Create new meal object and insert into database
                    Meal meal = new Meal(name, portionSize, calories, mealType, imageUrl, currentDate, displayDate);
                    insertMeal(meal);
                    progressDialog.dismiss();
                    Toast.makeText(this, "Meal logged with photo!", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to retrieve image URL", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
            });

        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertMeal(Meal meal) {
        executor.execute(() -> {
            mealDao.insertMeal(meal);
        });
    }

    // Returns the current date in "yyyy-MM-dd" format for storing in the database
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(new Date());
    }

    // Save state
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("OPTION", option);
        if (image != null && option == 1) { // if image is Bitmap
            outState.putParcelable("BITMAP", image);
        } else if (imageUri != null) { // if image is Uri
            outState.putString("imageUri", imageUri.toString());
        }
    }
}