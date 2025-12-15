package com.example.connect4app.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.connect4app.R;
import com.example.connect4app.ModeSelection.VsAISelectionActivity;
import com.example.connect4app.ModeSelection.VsPlayerSelectionActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button for vs player mode
        Button vsPlayerButton = findViewById(R.id.vsPlayerButton);
        vsPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VsPlayerSelectionActivity.class);
                startActivity(intent);
            }
        });

        // Button for vs AI mode
        Button vsAIButton = findViewById(R.id.vsAIButton);
        vsAIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VsAISelectionActivity.class);
                startActivity(intent);
            }
        });
    }
}