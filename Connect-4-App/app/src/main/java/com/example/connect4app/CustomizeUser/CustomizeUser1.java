package com.example.connect4app.CustomizeUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.connect4app.R;

public class CustomizeUser1 extends AppCompatActivity {
    private static final String PLAYER_1_NAME = "PLAYER_ONE_NAME";
    private static final String PLAYER_1_AVATAR = "PLAYER_ONE_AVATAR";
    private static final int PLAYER_CHOSEN_AVATAR = R.drawable.avatar1;

    String playerName = "Player 1";
    int playerAvatar = R.drawable.avatar1;
    int imagePosition = 0;

    // Creating object of ViewPager
    ViewPager mViewPager;

    // Images array
    int[] images = {R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3, R.drawable.avatar4,
            R.drawable.avatar5, R.drawable.avatar6, R.drawable.avatar7, R.drawable.avatar8};

    // Creating Object of ViewPagerAdapter
    ViewPagerAdapterUser mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_user1);
        // Initializing the ViewPager Object
        mViewPager = (ViewPager)findViewById(R.id.viewPagerMain1);
        // Initializing the ViewPagerAdapter
        mViewPagerAdapter = new ViewPagerAdapterUser(CustomizeUser1.this, images);
        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);

        EditText nameSelectText = findViewById(R.id.nameSelectText1);
        Button saveSettings = findViewById(R.id.saveSettingsUser1);

        // Set to previous settings
        Intent existingIntent = getIntent();

        if(existingIntent.hasExtra(PLAYER_1_NAME))
        {
            playerName = existingIntent.getStringExtra("PLAYER_ONE_NAME");
            nameSelectText.setText(playerName);
        }

        if(existingIntent.hasExtra(PLAYER_1_AVATAR))
        {
            playerAvatar = existingIntent.getIntExtra("PLAYER_ONE_AVATAR", PLAYER_CHOSEN_AVATAR);
            imagePosition = searchImagesArray(playerAvatar);

            mViewPager.setCurrentItem(imagePosition);
        }

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePosition = mViewPager.getCurrentItem();
                playerAvatar = images[imagePosition];

                Intent intent = new Intent();
                intent.putExtra("PLAYER_ONE_NAME", nameSelectText.getText().toString());
                intent.putExtra("PLAYER_ONE_AVATAR", playerAvatar);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private int searchImagesArray(int target)
    {
        for (int i = 0; i < images.length; i++) {
            if (images[i] == target) {
                return i;
            }
        }
        return 0;
    }
}