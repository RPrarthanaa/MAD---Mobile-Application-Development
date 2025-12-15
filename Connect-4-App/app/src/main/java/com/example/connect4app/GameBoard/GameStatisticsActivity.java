package com.example.connect4app.GameBoard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.connect4app.R;
import java.util.Locale;

public class GameStatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_statistics);

        TextView winsView = findViewById(R.id.winValue_1);
        TextView lossesView = findViewById(R.id.lossValue_1);
        TextView gamesPlayedView = findViewById(R.id.totalGamesPlayedVal_1);
        TextView winPercentageView = findViewById(R.id.winPercentageVal_1);
        Button returnButton = findViewById(R.id.returnButton_1);

        Intent intent = getIntent();
        //int player = intent.getIntExtra("player", 1);
        int wins = intent.getIntExtra("wins", 0);
        int losses = intent.getIntExtra("losses", 0);
        int gamesPlayed = intent.getIntExtra("gamesPlayed", 0);

        // Calculate win percentage
        float winPercentage = gamesPlayed > 0 ? ((float) wins / gamesPlayed) * 100 : 0;

        // Display stats
        winsView.setText(String.valueOf(wins));
        lossesView.setText(String.valueOf(losses));
        gamesPlayedView.setText(String.valueOf(gamesPlayed));
        winPercentageView.setText(String.format(Locale.getDefault(), "%.2f%%", winPercentage));

        // Return button functionality
        returnButton.setOnClickListener(v -> finish());
    }
}