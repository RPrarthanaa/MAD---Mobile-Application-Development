package com.example.connect4app.ModeSelection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.connect4app.CustomizeGame.CustomizeGameSettings;
import com.example.connect4app.CustomizeUser.CustomizeUser1;
import com.example.connect4app.CustomizeUser.CustomizeUser2;
import com.example.connect4app.GameBoard.GameBoard;
import com.example.connect4app.GameBoard.GameplayActivity;
import com.example.connect4app.Main.MainActivity;
import com.example.connect4app.R;

public class VsPlayerSelectionActivity extends AppCompatActivity {

    int currentCustomizationTarget;

    // Board customization variables
    String discColorOne, discColorTwo;
    int[] gridSize;

    // Player customization variables
    private static final int PLAYER_CHOSEN_AVATAR = 0;
    int playerAvatar1, playerAvatar2;
    String playerName1, playerName2;

    GameBoard gameBoard;
    boolean isPlayerOneTurn;
    boolean isGameEnded;
    boolean isEmpty;

    ActivityResultLauncher<Intent> detailActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        if (currentCustomizationTarget == 1) {
                            gridSize = intent.getIntArrayExtra(getString(R.string.GRID_SIZE));
                            discColorOne = intent.getStringExtra(getString(R.string.PLAYER1_DISC));
                            discColorTwo = intent.getStringExtra(getString(R.string.PLAYER2_DISC));
                        }
                        else if (currentCustomizationTarget == 2)
                        {
                            playerAvatar1 = intent.getIntExtra(getString(R.string.player1Avatar), PLAYER_CHOSEN_AVATAR);
                            playerName1 = intent.getStringExtra(getString(R.string.player1Label));
                        }
                        else if (currentCustomizationTarget == 3)
                        {
                            playerAvatar2 = intent.getIntExtra(getString(R.string.player2Avatar), PLAYER_CHOSEN_AVATAR);
                            playerName2 = intent.getStringExtra(getString(R.string.player2Label));
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vs_player_selection);

        Button customizePlayer1Button = findViewById(R.id.customizePlayer1Button);
        Button customizePlayer2Button = findViewById(R.id.customizePlayer2Button);
        Button customizeGameSettingsButton = findViewById(R.id.customizeGameButton);
        Button playGameButton = findViewById(R.id.playButton);
        Button backButton = findViewById(R.id.button);

        setDefaultValues();

        //so that preferences can be saved if the orientation of the screen is changed
        if(savedInstanceState != null)
        {
            gridSize = savedInstanceState.getIntArray(getString(R.string.GRID_SIZE));
            discColorOne = savedInstanceState.getString(getString(R.string.PLAYER1_DISC));
            discColorTwo = savedInstanceState.getString(getString(R.string.PLAYER2_DISC));
            playerName1 = savedInstanceState.getString(getString(R.string.player1Label));
            playerName2 = savedInstanceState.getString(getString(R.string.player2Label));
            playerAvatar1 = savedInstanceState.getInt(getString(R.string.player1Avatar));
            playerAvatar2 = savedInstanceState.getInt(getString(R.string.player2Avatar));
            gameBoard = savedInstanceState.getParcelable(getString(R.string.game_board));
            isPlayerOneTurn = savedInstanceState.getBoolean(getString(R.string.isPlayer1Turn));
            isGameEnded = savedInstanceState.getBoolean("GAME_ENDED");
        }

        Intent existingIntent = getIntent();
        if (existingIntent != null) {
            launchExistingIntent(existingIntent);
        }

        // Back button listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VsPlayerSelectionActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Customize player 1 button listener
        customizePlayer1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCustomizationTarget = 2;
                Intent intent = new Intent(VsPlayerSelectionActivity.this, CustomizeUser1.class);
                intent.putExtra(getString(R.string.player1Label), playerName1);
                intent.putExtra(getString(R.string.player1Avatar), playerAvatar1);
                detailActivityLauncher.launch(intent);
            }
        });

        // Customize player 2 button listener
        customizePlayer2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCustomizationTarget = 3;
                Intent intent = new Intent(VsPlayerSelectionActivity.this, CustomizeUser2.class);
                intent.putExtra(getString(R.string.player2Label), playerName2);
                intent.putExtra(getString(R.string.player2Avatar), playerAvatar2);
                detailActivityLauncher.launch(intent);
            }
        });

        // Customize game settings button listener
        customizeGameSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCustomizationTarget = 1;
                Intent intent = new Intent(VsPlayerSelectionActivity.this, CustomizeGameSettings.class);
                if (gridSize != null) {
                    intent.putExtra(getString(R.string.GRID_SIZE), gridSize);
                    intent.putExtra(getString(R.string.PLAYER1_DISC), discColorOne);
                    intent.putExtra(getString(R.string.PLAYER2_DISC), discColorTwo);
                    intent.putExtra("IS_EMPTY", isEmpty);
                }
                detailActivityLauncher.launch(intent);
            }
        });

        // Play game button listener
        playGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VsPlayerSelectionActivity.this, GameplayActivity.class);

                intent.putExtra(getString(R.string.GRID_SIZE), gridSize);
                intent.putExtra(getString(R.string.PLAYER1_DISC), discColorOne);
                intent.putExtra(getString(R.string.PLAYER2_DISC), discColorTwo);
                intent.putExtra(getString(R.string.player1Label), playerName1);
                intent.putExtra(getString(R.string.player2Label), playerName2);
                intent.putExtra(getString(R.string.player1Avatar), playerAvatar1);
                intent.putExtra(getString(R.string.player2Avatar), playerAvatar2);

                if (gameBoard != null) {
                    intent.putExtra(getString(R.string.game_board), gameBoard);
                    intent.putExtra(getString(R.string.isPlayer1Turn), isPlayerOneTurn);
                    intent.putExtra("GAME_ENDED", isGameEnded);
                }

                detailActivityLauncher.launch(intent);
            }
        });
    }

    // Set default board values
    private void setDefaultValues() {

        // Board customization variables
        discColorOne = "Red" ;
        discColorTwo = "Blue";
        gridSize = new int[]{7, 6};
        currentCustomizationTarget = 0;

        // Player customization variables
        playerAvatar1 = R.drawable.avatar1;
        playerAvatar2 = R.drawable.avatar2;
        playerName1 = "Player One";
        playerName2 = "Player Two";
    }

    private void launchExistingIntent(Intent existingIntent) {
        if(existingIntent.hasExtra(getString(R.string.GRID_SIZE)))
            gridSize = existingIntent.getIntArrayExtra(getString(R.string.GRID_SIZE));
        if (existingIntent.hasExtra(getString(R.string.PLAYER1_DISC)))
            discColorOne = existingIntent.getStringExtra(getString(R.string.PLAYER1_DISC));
        if (existingIntent.hasExtra(getString(R.string.PLAYER2_DISC)))
            discColorTwo = existingIntent.getStringExtra(getString(R.string.PLAYER2_DISC));
        if (existingIntent.hasExtra(getString(R.string.player1Label)))
            playerName1 = existingIntent.getStringExtra(getString(R.string.player1Label));
        if (existingIntent.hasExtra(getString(R.string.player2Label)))
            playerName2 = existingIntent.getStringExtra(getString(R.string.player2Label));
        if (existingIntent.hasExtra(getString(R.string.player1Avatar)))
            playerAvatar1 = existingIntent.getIntExtra(getString(R.string.player1Avatar), playerAvatar1);
        if (existingIntent.hasExtra(getString(R.string.player2Avatar)))
            playerAvatar2 = existingIntent.getIntExtra(getString(R.string.player2Avatar), playerAvatar2);
        if(existingIntent.hasExtra(getString(R.string.game_board))) {
            gameBoard = existingIntent.getParcelableExtra(getString(R.string.game_board));
            isPlayerOneTurn = existingIntent.getBooleanExtra(getString(R.string.isPlayer1Turn), true);
            isGameEnded = existingIntent.getBooleanExtra("GAME_ENDED", false);
        }
        isEmpty = existingIntent.getBooleanExtra("IS_EMPTY", true);
    }

    //saving preferences in case of screen orientation change
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(getString(R.string.GRID_SIZE),gridSize);
        outState.putString(getString(R.string.PLAYER1_DISC), discColorOne);
        outState.putString(getString(R.string.PLAYER2_DISC), discColorTwo);
        outState.putString(getString(R.string.player1Label), playerName1);
        outState.putString(getString(R.string.player2Label), playerName2);
        outState.putInt(getString(R.string.player1Avatar), playerAvatar1);
        outState.putInt(getString(R.string.player2Avatar), playerAvatar2);
        outState.putParcelable(getString(R.string.game_board), gameBoard);
        outState.putBoolean(getString(R.string.isPlayer1Turn), isPlayerOneTurn);
        outState.putBoolean("GAME_ENDED", isGameEnded);
    }
}