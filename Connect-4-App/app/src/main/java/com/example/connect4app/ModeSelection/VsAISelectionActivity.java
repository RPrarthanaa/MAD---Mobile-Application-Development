package com.example.connect4app.ModeSelection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.connect4app.CustomizeGame.CustomizeGameAiSettings;
import com.example.connect4app.CustomizeUser.CustomizeUser1;
import com.example.connect4app.GameBoard.GameBoard;
import com.example.connect4app.GameBoard.GameplayActivity;
import com.example.connect4app.Main.MainActivity;
import com.example.connect4app.R;
import java.util.Random;

public class VsAISelectionActivity extends AppCompatActivity {

    int currentCustomizationTarget;

    // Board customization variables
    String discColorOne, discColorTwo;
    int[] gridSize;

    // Player customization variables
    private static final int PLAYER_CHOSEN_AVATAR = 0;
    int playerAvatar1, aiAvatar;
    String playerName1, aiName;

    GameBoard gameBoard;
    boolean isPlayerOneTurn;
    boolean isGameEnded;
    boolean isEmpty;

    int[] aiAvatarsList = {R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3, R.drawable.avatar4,
            R.drawable.avatar5, R.drawable.avatar6, R.drawable.avatar7, R.drawable.avatar8};

    String[] aiNamesList = {"Captain Connect", "Fourmageddon", "Connects McFourface", "The Blockinator",
            "Gridlock Guru", "Connectalicious", "Dr. Disc", "Gridzilla"};

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
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vs_ai_selection);

        Button customizePlayer1Button = findViewById(R.id.customizePlayerButton);
        Button customizeGameSettingsButton = findViewById(R.id.customizeGameAiButton);
        Button playGameButton = findViewById(R.id.playButtonAi);
        Button backButton = findViewById(R.id.backButtonAi);

        setDefaultValues();
        setRandomAi();

        //so that preferences can be saved if the orientation of the screen is changed
        if(savedInstanceState != null)
        {
            gridSize = savedInstanceState.getIntArray(getString(R.string.GRID_SIZE));
            discColorOne = savedInstanceState.getString(getString(R.string.PLAYER1_DISC));
            discColorTwo = savedInstanceState.getString(getString(R.string.PLAYER2_DISC));
            playerName1 = savedInstanceState.getString(getString(R.string.player1Label));
            playerAvatar1 = savedInstanceState.getInt(getString(R.string.player1Avatar));
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
                Intent intent = new Intent(VsAISelectionActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Customize player 1 button listener
        customizePlayer1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCustomizationTarget = 2;
                Intent intent = new Intent(VsAISelectionActivity.this, CustomizeUser1.class);
                intent.putExtra(getString(R.string.player1Label), playerName1);
                intent.putExtra(getString(R.string.player1Avatar), playerAvatar1);
                detailActivityLauncher.launch(intent);
            }
        });

        // Customize player 2 button listener
        customizeGameSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCustomizationTarget = 1;
                Intent intent = new Intent(VsAISelectionActivity.this, CustomizeGameAiSettings.class);
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
                Intent intent = new Intent(VsAISelectionActivity.this, GameplayActivity.class);

                boolean isVsAI = true;

                intent.putExtra(getString(R.string.ai_flag), isVsAI);
                intent.putExtra(getString(R.string.GRID_SIZE), gridSize);
                intent.putExtra(getString(R.string.PLAYER1_DISC), discColorOne);
                intent.putExtra(getString(R.string.PLAYER2_DISC), discColorTwo);
                intent.putExtra(getString(R.string.player1Label), playerName1);
                intent.putExtra(getString(R.string.player2Label), aiName);
                intent.putExtra(getString(R.string.player1Avatar), playerAvatar1);
                intent.putExtra(getString(R.string.player2Avatar), aiAvatar);

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
        playerName1 = "Player One";
    }

    // Set random AI
    private void setRandomAi()
    {
        int randAvatar = new Random().nextInt(aiAvatarsList.length);
        int randName = new Random().nextInt(aiNamesList.length);

        aiAvatar = aiAvatarsList[randAvatar];
        aiName = aiNamesList[randName];
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
//        if (existingIntent.hasExtra(getString(R.string.player2Label)))
//            aiName = existingIntent.getStringExtra(getString(R.string.player2Label));
        if (existingIntent.hasExtra(getString(R.string.player1Avatar)))
            playerAvatar1 = existingIntent.getIntExtra(getString(R.string.player1Avatar), playerAvatar1);
//        if (existingIntent.hasExtra(getString(R.string.player2Avatar)))
//            aiAvatar = existingIntent.getIntExtra(getString(R.string.player2Avatar), aiAvatar);
        if(existingIntent.hasExtra(getString(R.string.game_board))) {
            gameBoard = existingIntent.getParcelableExtra(getString(R.string.game_board));
            isPlayerOneTurn = existingIntent.getBooleanExtra(getString(R.string.isPlayer1Turn), true);
            isGameEnded = existingIntent.getBooleanExtra("GAME_ENDED", false);
        }
        isEmpty = existingIntent.getBooleanExtra("IS_EMPTY", true);
    }

    // Saving preferences in case of screen orientation change
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(getString(R.string.GRID_SIZE), gridSize);
        outState.putString(getString(R.string.PLAYER1_DISC), discColorOne);
        outState.putString(getString(R.string.PLAYER2_DISC), discColorTwo);
        outState.putString(getString(R.string.player1Label), playerName1);
        outState.putInt(getString(R.string.player1Avatar), playerAvatar1);
        outState.putParcelable(getString(R.string.game_board), gameBoard);
        outState.putBoolean(getString(R.string.isPlayer1Turn), isPlayerOneTurn);
        outState.putBoolean("GAME_ENDED", isGameEnded);
    }
}