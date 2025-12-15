package com.example.connect4app.CustomizeUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.connect4app.R;

public class CustomizeUser2 extends AppCompatActivity {
    private static final String PLAYER_2_NAME = "PLAYER_TWO_NAME";
    private static final String PLAYER_2_AVATAR = "PLAYER_TWO_AVATAR";
    private static final int PLAYER_CHOSEN_AVATAR = R.drawable.avatar1;

    String playerName = "Player 2";
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
        setContentView(R.layout.activity_customize_user2);
        // Initializing the ViewPager Object
        mViewPager = (ViewPager)findViewById(R.id.viewPagerMain2);
        // Initializing the ViewPagerAdapter
        mViewPagerAdapter = new ViewPagerAdapterUser(CustomizeUser2.this, images);
        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);

        EditText nameSelectText = findViewById(R.id.nameSelectText2);
        Button saveSettings = findViewById(R.id.saveSettingsUser2);

        // Set to previous settings
        Intent existingIntent = getIntent();

        if(existingIntent.hasExtra(PLAYER_2_NAME))
        {
            playerName = existingIntent.getStringExtra("PLAYER_TWO_NAME");
            nameSelectText.setText(playerName);
        }

        if(existingIntent.hasExtra(PLAYER_2_AVATAR))
        {
            playerAvatar = existingIntent.getIntExtra("PLAYER_TWO_AVATAR", PLAYER_CHOSEN_AVATAR);
            imagePosition = searchImagesArray(playerAvatar);

            mViewPager.setCurrentItem(imagePosition);
        }

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePosition = mViewPager.getCurrentItem();
                playerAvatar = images[imagePosition];

                Intent intent = new Intent();
                intent.putExtra("PLAYER_TWO_NAME", nameSelectText.getText().toString());
                intent.putExtra("PLAYER_TWO_AVATAR", playerAvatar);
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