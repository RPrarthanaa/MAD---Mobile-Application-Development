package com.example.connect4app.CustomizeGame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.connect4app.R;
import java.util.Arrays;

public class CustomizeGameSettings extends AppCompatActivity {
    int[] gridSize = new int[] {7, 6};
    private String GRID_SIZE;
    private String DISC_COLOR_ONE;
    private String DISC_COLOR_TWO;
    String colorStrPlayer1 = "Red", colorStrPlayer2 = "Blue";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_game);

        GRID_SIZE = getString(R.string.GRID_SIZE);
        DISC_COLOR_ONE = getString(R.string.PLAYER1_DISC);
        DISC_COLOR_TWO = getString(R.string.PLAYER2_DISC);

        Button saveSettingsBtn = findViewById(R.id.saveSettingsButton);
        RadioGroup radioGroup = findViewById(R.id.gridSizeRadioGroup);
        RadioButton standardRadioBtn = findViewById(R.id.radioStandard);
        RadioGroup radioGroup1 = findViewById(R.id.colorGridPlayer1).findViewById(R.id.discColorRadioGroup);
        RadioGroup radioGroup2 = findViewById(R.id.colorGridPlayer2).findViewById(R.id.discColorRadioGroup);
        RadioButton[] radioButtonsArrayOne = populateButtonArray(radioGroup1);
        RadioButton[] radioButtonsArrayTwo = populateButtonArray(radioGroup2);
        RadioButton redPlayer1 = radioGroup1.findViewById(R.id.radioRed);
        RadioButton bluePlayer2 = radioGroup2.findViewById(R.id.radioBlue);

        // Set default colors for players
        redPlayer1.setChecked(true);
        bluePlayer2.setChecked(true);

        // Set default size as large
        standardRadioBtn.setChecked(true);

        // Set to previous settings
        Intent existingIntent = getIntent();
        if (existingIntent.hasExtra(GRID_SIZE)) {
            gridSize = existingIntent.getIntArrayExtra(GRID_SIZE);
            if (Arrays.equals(gridSize, new int[] {6, 5}))
                radioGroup.check(R.id.radioSmall);
            else if (Arrays.equals(gridSize, new int[] {8, 7}))
                radioGroup.check(R.id.radioLarge);
        }
        if (existingIntent.hasExtra(DISC_COLOR_ONE)) {
            colorStrPlayer1 = existingIntent.getStringExtra(DISC_COLOR_ONE);
            setPreviousRadioButtons(radioButtonsArrayOne, colorStrPlayer1);
        }
        if (existingIntent.hasExtra(DISC_COLOR_TWO)) {
            colorStrPlayer2 = existingIntent.getStringExtra(DISC_COLOR_TWO);
            setPreviousRadioButtons(radioButtonsArrayTwo, colorStrPlayer2);
        }

        boolean empty = false;
        if (existingIntent.hasExtra("IS_EMPTY")) {
            empty = existingIntent.getBooleanExtra("IS_EMPTY", true);
        }
        if(!empty) {
            findViewById(R.id.radioStandard).setEnabled(false);
            findViewById(R.id.radioLarge).setEnabled(false);
            findViewById(R.id.radioSmall).setEnabled(false);
        }

        // Select grid size
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked) {
                if (checked == R.id.radioSmall)
                    gridSize = new int[]{6, 5};
                else if (checked == R.id.radioLarge)
                    gridSize = new int[]{8, 7};
                else if (checked == R.id.radioStandard)
                    gridSize = new int[]{7, 6};
            }
        });

        // Select color for Player 1
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedButton = findViewById(i);
                colorStrPlayer1 = checkedButton.getText().toString();
                if (colorStrPlayer1.equals(colorStrPlayer2)) {
                    Toast.makeText(CustomizeGameSettings.this, "Players Can't Be Same Color", Toast.LENGTH_SHORT).show();
                    redPlayer1.setChecked(true);
                }
            }
        });

        // Select color for Player 2
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedButton = findViewById(i);
                colorStrPlayer2 = checkedButton.getText().toString();
                if (colorStrPlayer2.equals(colorStrPlayer1)) {
                    Toast.makeText(CustomizeGameSettings.this, "Players Can't Be Same Color", Toast.LENGTH_SHORT).show();
                    bluePlayer2.setChecked(true);
                }
            }
        });

        // Save settings button listener
        saveSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //move back to previous view
                Intent intent = new Intent();
                intent.putExtra(GRID_SIZE, gridSize);
                intent.putExtra(DISC_COLOR_ONE, colorStrPlayer1);
                intent.putExtra(DISC_COLOR_TWO, colorStrPlayer2);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private RadioButton[] populateButtonArray(RadioGroup radioGrp) {
        return new RadioButton[]{radioGrp.findViewById(R.id.radioRed),
                radioGrp.findViewById(R.id.radioBlue),
                radioGrp.findViewById(R.id.radioGreen),
                radioGrp.findViewById(R.id.radioYellow),
                radioGrp.findViewById(R.id.radioPurple),
                radioGrp.findViewById(R.id.radioOrange),
                radioGrp.findViewById(R.id.radioPink),
                radioGrp.findViewById(R.id.radioBrown),
                radioGrp.findViewById(R.id.radioPalePink)};
    }

    // Revert back to the previously checked radio button for each players' color
    private void setPreviousRadioButtons(RadioButton[] buttonArray, String colorString) {
        for (int i = 0; i < 9; i++) {
            RadioButton radioBtn = buttonArray[i];
            if (radioBtn.getText().toString().equals(colorString)) {
                buttonArray[i].setChecked(true);
                break;
            }
        }
    }
}
