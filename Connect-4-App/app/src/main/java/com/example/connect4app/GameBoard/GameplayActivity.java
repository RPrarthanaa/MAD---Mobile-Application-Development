package com.example.connect4app.GameBoard;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.connect4app.CustomizeGame.ColorHelper;
import com.example.connect4app.Main.MainActivity;
import com.example.connect4app.R;
import com.example.connect4app.ModeSelection.VsAISelectionActivity;
import com.example.connect4app.ModeSelection.VsPlayerSelectionActivity;
import java.util.Random;

public class GameplayActivity extends AppCompatActivity {
    private static int ROW_COUNT;
    private static int COLUMN_COUNT;
    private boolean gameEnded = false; // Track if the game has ended
    private boolean isPlayerOneTurn = true; // Track turn
    private int playerOneColor, playerTwoColor, avatarOne, avatarTwo;
    private String playerOneName, playerTwoName, playerOneColorStr, playerTwoColorStr;
    private static GameBoard gameBoard; //Initialize with Maximum GameBoard
    private ImageView[][] gridCells; // Track UI cells
    private Button player1StatsButton, player2StatsButton, undoButton, settingsButton;
    boolean isVsAI; // Check if in vs AI mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_board);

        GridLayout gridLayout = findViewById(R.id.grid_layout);
        TextView notificationMessage = findViewById(R.id.notification_message);

        // Initialize the player and game characteristics
        Intent existingIntent = getIntent();
        if (existingIntent != null) {
            isVsAI = existingIntent.getBooleanExtra(getString(R.string.ai_flag), false); // Get flag for checking vs AI mode
            launchMenuForStart(existingIntent); // Apply modified customization settings
        }

        // Initialize board
        gridCells = new ImageView[ROW_COUNT][COLUMN_COUNT];
        gameBoard = new GameBoard(COLUMN_COUNT, ROW_COUNT);

        // Set up the visual grid
        setupGridLayout();

        // Set up the previous game board if any
        if (existingIntent != null)
            launchMenuOnPause(existingIntent);

        // Display Player Turn
        notificationMessage.setText(getString(R.string.playerTurn, getCurrentPlayerName()));

        // Set up event handlers for each grid block
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COLUMN_COUNT; col++) {
                ImageView imageView = gridCells[row][col];

                final int column = col;
                imageView.setOnClickListener(v -> {
                    if (!gameEnded) { // Only handle moves if the game is not ended
                        handlePlayerMove(column, notificationMessage);
                    }
                });
                gridCells[row][col] = imageView; // Store ImageView reference in grid
                gridLayout.addView(imageView);
            }
        }

        // Reset game button
        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(v -> {
            resetGame(notificationMessage);
        });

        // Undo move button
        undoButton = findViewById(R.id.undo_button);
        undoButton.setOnClickListener(v -> {
            undoLastMove(notificationMessage);
        });

        // Exit button - exit to main menu
        Button exitButton = findViewById(R.id.backButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameBoard.resetStats(); //clear stats
                gameBoard.clearBoard(); //clear board
                Intent intent = new Intent(GameplayActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set up stats buttons and listeners
        player1StatsButton = findViewById(R.id.player1_stats_button);
        player2StatsButton = findViewById(R.id.player2_stats_button);

        player1StatsButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameplayActivity.this, GameStatisticsActivity.class);
            intent.putExtra("player", 1);
            intent.putExtra("wins", gameBoard.getPlayer1Wins());
            intent.putExtra("losses", gameBoard.getPlayer1Losses());
            intent.putExtra("gamesPlayed", gameBoard.getPlayer1GamesPlayed());
            startActivity(intent);
        });

        player2StatsButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameplayActivity.this, GameStatisticsActivity.class);
            intent.putExtra("player", 2);
            intent.putExtra("wins", gameBoard.getPlayer2Wins());
            intent.putExtra("losses", gameBoard.getPlayer2Losses());
            intent.putExtra("gamesPlayed", gameBoard.getPlayer2GamesPlayed());
            startActivity(intent);
        });

        // Settings Button
        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            Intent intent;
            if (isVsAI) // Open activities specific to player mode
                intent = new Intent(GameplayActivity.this, VsAISelectionActivity.class);
            else
                intent = new Intent(GameplayActivity.this, VsPlayerSelectionActivity.class);

            // Stores values in intent
            intent.putExtra(getString(R.string.PLAYER1_DISC), playerOneColorStr);
            intent.putExtra(getString(R.string.PLAYER2_DISC), playerTwoColorStr);
            intent.putExtra(getString(R.string.player1Label),playerOneName);
            intent.putExtra(getString(R.string.player2Label),playerTwoName);
            intent.putExtra(getString(R.string.player1Avatar), avatarOne);
            intent.putExtra(getString(R.string.player2Avatar), avatarTwo);
            intent.putExtra(getString(R.string.GRID_SIZE), new int[]{COLUMN_COUNT, ROW_COUNT});
            intent.putExtra(getString(R.string.game_board), gameBoard);
            intent.putExtra(getString(R.string.isPlayer1Turn), isPlayerOneTurn);
            intent.putExtra("GAME_ENDED", gameEnded);
            intent.putExtra("IS_EMPTY", !isColored());

            startActivity(intent);
        });
    }

    // Handle player moves
    private void handlePlayerMove(int column, TextView notificationMessage) {
        GameBoard.Piece currentPlayerPiece = isPlayerOneTurn ? GameBoard.Piece.PLAYER_1 : GameBoard.Piece.PLAYER_2;

        try {
            int row = gameBoard.getAvailableRowInColumn(column);
            boolean win = gameBoard.move(column, currentPlayerPiece);

            Drawable circle = getDrawable(R.drawable.white_circle);
            assert circle != null;
            circle.setTint(isPlayerOneTurn ? playerOneColor : playerTwoColor);
            gridCells[ROW_COUNT - 1 - row][column].setImageDrawable(circle);

            if (win) {
                notificationMessage.setText(getString(R.string.playerWin, getCurrentPlayerName()));
                gameEnded = true; // Game ended
                undoButton.setEnabled(false);
                settingsButton.setEnabled(false);
                showStatsButtons(); // Show stats button on game end
            } else if (gameBoard.isDraw()) {
                notificationMessage.setText(getString(R.string.game_is_draw));
                gameEnded = true;
                undoButton.setEnabled(false);
                settingsButton.setEnabled(false);
                showStatsButtons();
            } else {
                isPlayerOneTurn = !isPlayerOneTurn;
                notificationMessage.setText(getString(R.string.playerTurn, getCurrentPlayerName()));
                if (!isPlayerOneTurn && isVsAI) {
                    makeAITurn(notificationMessage); // AI's turn
                }
            }
        } catch (IllegalArgumentException e) {
            // Column full or invalid move
        }
    }

    // Let AI make move
    private void makeAITurn(TextView notificationMessage) {
        if (gameEnded) return;

        // Select random column to make move in
        new Handler().postDelayed(() -> {
            Random random = new Random();
            int column;
            do {
                column = random.nextInt(gameBoard.getColumns());
            } while (!canMakeMove(column));

            // Make the AI's move with 2 second delay
            handlePlayerMove(column, notificationMessage);
        }, 2000);
    }

    // Check if moves are possible
    private boolean canMakeMove(int column) {
        try {
            gameBoard.getAvailableRowInColumn(column);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Undo last move
    private void undoLastMove(TextView notificationMessage) {
        if (gameBoard.canUndo()) {
            GameBoard.Move lastMove = gameBoard.undoLastMove();
            if (lastMove != null) {
                gridCells[ROW_COUNT - 1 - lastMove.row][lastMove.column].setImageResource(R.drawable.circle); // Reset to empty

                // Switch turn to the previous player
                isPlayerOneTurn = !isPlayerOneTurn;
                notificationMessage.setText(getString(R.string.playerTurn, getCurrentPlayerName()));
                gameEnded = false; // The game is not ended after undo

                // If it's the AI's turn now, make the AI move again
                if (!isPlayerOneTurn && isVsAI) {
                    makeAITurn(notificationMessage);
                }
            }
        }
    }

    // Reset game to start
    private void resetGame(TextView notificationMessage) {
        gameBoard.clearBoard();
        isPlayerOneTurn = true;
        gameEnded = false;

        undoButton.setEnabled(true);
        settingsButton.setEnabled(true);
        hideStatsButtons();
        notificationMessage.setText(getString(R.string.playerTurn, playerOneName));

        // Clear all grid cells
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COLUMN_COUNT; col++) {
                gridCells[row][col].setImageResource(R.drawable.circle); // Reset to empty
            }
        }
    }

    // Intent retrieve
    private void launchMenuForStart(Intent existingIntent) {

        // Set the grid size
        int[] rowxcol = existingIntent.getIntArrayExtra(getString(R.string.GRID_SIZE));
        if (rowxcol != null) {
            ROW_COUNT = rowxcol[1];
            COLUMN_COUNT = rowxcol[0];
        }

        // Set the disc color of Player 1
        playerOneColorStr = existingIntent.getStringExtra(getString(R.string.PLAYER1_DISC));
        playerOneColor = getColor(ColorHelper.getColorResource(playerOneColorStr, 1));
        TextView txtView1 = findViewById(R.id.player1_color);
        txtView1.setText(playerOneColorStr);

        // Set the disc color of Player 2
        playerTwoColorStr = existingIntent.getStringExtra(getString(R.string.PLAYER2_DISC));
        playerTwoColor = getColor(ColorHelper.getColorResource(playerTwoColorStr, 2));
        TextView txtView2 = findViewById(R.id.player2_color);
        txtView2.setText(playerTwoColorStr);

        // Set the names of Players 1 and 2
        TextView label1 = findViewById(R.id.player1_name);
        TextView label2 = findViewById(R.id.player2_name);
        playerOneName = existingIntent.getStringExtra(getString(R.string.player1Label));
        playerTwoName = existingIntent.getStringExtra(getString(R.string.player2Label));
        label1.setText(playerOneName);
        label2.setText(playerTwoName);

        // Set the avatars of Players 1 and 2
        ImageView avatar1 = findViewById(R.id.player1_avatar);
        avatarOne = existingIntent.getIntExtra(getString(R.string.player1Avatar), 1);
        avatar1.setImageResource(avatarOne);
        ImageView avatar2 = findViewById(R.id.player2_avatar);
        avatarTwo = existingIntent.getIntExtra(getString(R.string.player2Avatar), 1);
        avatar2.setImageResource(avatarTwo);
    }

    // Holds previous gameBoard state
    private void launchMenuOnPause(Intent existingIntent) {
        if (existingIntent.hasExtra(getString(R.string.game_board))) {
            GameBoard gameBd = existingIntent.getParcelableExtra(getString(R.string.game_board));
            assert gameBd != null;
            gameBoard = gameBd.copyGameBoard(COLUMN_COUNT, ROW_COUNT); // Get copy of gameBd

            isPlayerOneTurn = existingIntent.getBooleanExtra(getString(R.string.isPlayer1Turn), true);
            gameEnded = existingIntent.getBooleanExtra("GAME_ENDED", true);
            updateBoardUI(); // Restore previous state of GameBoard
        }
    }

    // Show stats button
    private void showStatsButtons() {
        player1StatsButton.setVisibility(View.VISIBLE);
        player2StatsButton.setVisibility(View.VISIBLE);
    }

    // Hide stats button
    private void hideStatsButtons() {
        player1StatsButton.setVisibility(View.INVISIBLE);
        player2StatsButton.setVisibility(View.INVISIBLE);
    }

    // Update UI
    private void updateBoardUI() {
        TextView notificationMessage = findViewById(R.id.notification_message);
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COLUMN_COUNT; col++) {
                GameBoard.Piece piece = gameBoard.getCell(col, row);
                if (piece != null) { // Initialize previously placed discs
                    Drawable circle = getDrawable(R.drawable.white_circle);
                    assert circle != null;
                    circle.setTint(piece == GameBoard.Piece.PLAYER_1 ? playerOneColor : playerTwoColor);
                    gridCells[ROW_COUNT-1-row][col].setImageDrawable(circle);
                }
            }
        }

        // Updates UI on orientation change
        if (gameEnded) {
            notificationMessage.setText(getString(R.string.playerWin, getCurrentPlayerName()));
            undoButton.setEnabled(false);
            settingsButton.setEnabled(false);
            showStatsButtons();
        } else {
            if (!isPlayerOneTurn) {
                notificationMessage.setText(getString(R.string.playerTurn, getCurrentPlayerName()));
            }
        }
    }

    // Returns label of current player
    private String getCurrentPlayerName() {
        return isPlayerOneTurn ? playerOneName : playerTwoName;
    }

    // Checks if the game board is empty
    private boolean isColored() {
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COLUMN_COUNT; col++) {
                GameBoard.Piece piece = gameBoard.getCell(col, row);
                if (piece != null) {
                    return true; // a piece exists
                }
            }
        }
        return false; // There are no pieces
    }

    // State preserving
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save game board
        outState.putParcelable("gameBoard", gameBoard);
        outState.putBoolean("isPlayerOneTurn", isPlayerOneTurn);
        outState.putBoolean("gameEnded", gameEnded);
        outState.putInt("playerOneColor", playerOneColor);
        outState.putInt("playerTwoColor", playerTwoColor);
        outState.putString("playerOneName", playerOneName);
        outState.putString("playerTwoName", playerTwoName);
        outState.putBoolean("isVsAI", isVsAI);
    }

    // State restoring
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore game state
        gameBoard = savedInstanceState.getParcelable("gameBoard");
        isPlayerOneTurn = savedInstanceState.getBoolean("isPlayerOneTurn");
        gameEnded = savedInstanceState.getBoolean("gameEnded");
        playerOneColor = savedInstanceState.getInt("playerOneColor");
        playerTwoColor = savedInstanceState.getInt("playerTwoColor");
        playerOneName = savedInstanceState.getString("playerOneName");
        playerTwoName = savedInstanceState.getString("playerTwoName");
        isVsAI = savedInstanceState.getBoolean("isVsAI");

        updateBoardUI();
    }

    // Assigns cellSize based on the gridSize and initialises the grid
    private void setupGridLayout() {
        int cellSize;
        if (ROW_COUNT == 5)
            cellSize = getResources().getDimensionPixelSize(R.dimen.cell_size_small);
        else if (ROW_COUNT == 7)
            cellSize = getResources().getDimensionPixelSize(R.dimen.cell_size_large);
        else
            cellSize = getResources().getDimensionPixelSize(R.dimen.cell_size_standard);

        // Set up grid
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COLUMN_COUNT; col++) {
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.circle);  // Start with empty circles

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                params.width = cellSize;
                params.height = cellSize;
                if (ROW_COUNT == 7)
                    params.setMargins(3, 3, 3, 3);  // Margins between cells
                else
                    params.setMargins(5, 5, 5, 5);  // Margins between cells
                imageView.setLayoutParams(params);

                gridCells[row][col] = imageView;
            }
        }
    }
}