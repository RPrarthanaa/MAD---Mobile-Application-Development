package com.example.connect4app.GameBoard;
/*
    SOURCE for Parcelable Interface:
    Website: Stack OverFlow
    Last Modified On: June 25, 2018
    URL: https://stackoverflow.com/questions/28392946/adding-parcelable-to-an-interface-class-for-custom-objects
 */

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import java.util.*;

public class GameBoard implements Parcelable {

    public enum Piece {
        PLAYER_1,
        PLAYER_2
    }

    // Fields for tracking stats
    private int player1Wins = 0;
    private int player2Wins = 0;
    private int player1Losses = 0;
    private int player2Losses = 0;
    private int player1GamesPlayed = 0;
    private int player2GamesPlayed = 0;

    private List<Move> moveHistory = new ArrayList<>();
    private final List<List<Piece>> columns;
    private final int rows;

    public GameBoard(int columns, int rows) {
        this.rows = rows;
        this.columns = new ArrayList<>();

        for (int i = 0; i < columns; ++i) {
            this.columns.add(new ArrayList<>());
        }
    }

    protected GameBoard(Parcel parcel) {
        rows = parcel.readInt();
        int columnCount = parcel.readInt();
        columns = new ArrayList<>(columnCount);

        for (int i = 0; i < columnCount; i++) {
            List<Piece> column = new ArrayList<>();
            parcel.readList(column, Piece.class.getClassLoader());
            columns.add(column);
        }

        moveHistory = new ArrayList<>();
        parcel.readList(moveHistory, Move.class.getClassLoader());

        player1Wins = parcel.readInt();
        player2Wins = parcel.readInt();
        player1Losses = parcel.readInt();
        player2Losses = parcel.readInt();
        player1GamesPlayed = parcel.readInt();
        player2GamesPlayed = parcel.readInt();
    }

    // Get num of rows
    public int getRows() {
        return rows;
    }

    // Get num of cols
    public int getColumns() {
        return columns.size();
    }

    // Get cell position
    public Piece getCell(int x, int y) {
        assert (x >= 0 && x < getColumns());
        assert (y >= 0 && y < getRows());

        List<Piece> column = columns.get(x);

        if (column.size() > y) {
            return column.get(y);
        } else {
            return null;
        }
    }

    // Return the current available row in the column
    public int getAvailableRowInColumn(int columnIndex) {
        List<Piece> column = columns.get(columnIndex);
        if (column.size() >= rows) {
            throw new IllegalArgumentException("That column is full");
        }
        return column.size();
    }

    // Make a move
    public boolean move(int x, Piece player) {
        List<Piece> column = columns.get(x);

        // Check if column is full
        if (column.size() >= this.rows) {
            throw new IllegalArgumentException("That column is full");
        }
        column.add(player);

        moveHistory.add(new Move(x, column.size() - 1, player)); // Add move to history

        boolean win = checkWin(x, column.size() - 1, player); // Check win condition

        if (win) {
            if (player == Piece.PLAYER_1) {
                player1Wins++;
                player2Losses++;
            } else {
                player2Wins++;
                player1Losses++;
            }
            player1GamesPlayed++;
            player2GamesPlayed++;
        } else if (isDraw()) {
            player1GamesPlayed++;
            player2GamesPlayed++;
        }

        return win;
    }

    // Undo last move
    public Move undoLastMove() {
        if (moveHistory.isEmpty()) {
            return null;
        }
        Move lastMove = moveHistory.remove(moveHistory.size() - 1);
        List<Piece> column = columns.get(lastMove.column);
        column.remove(column.size() - 1);
        return lastMove;
    }

    // Check if possible to undo
    public boolean canUndo() {
        return !moveHistory.isEmpty();
    }

    // Win condition checks
    private boolean checkWin(int x, int y, Piece player) {
        // Check for win in all 4 directions: horizontal, vertical, and two diagonals
        return checkDirection(x, y, 1, 0, player) ||  // Horizontal
                checkDirection(x, y, 0, 1, player) ||  // Vertical
                checkDirection(x, y, 1, 1, player) ||  // Diagonal \
                checkDirection(x, y, 1, -1, player);   // Diagonal /
    }

    // Check for circles in a given direction, both forward and backward
    private boolean checkDirection(int x, int y, int xStep, int yStep, Piece player) {
        int count = 1;  // Start with current piece

        // Check in positive direction (forward)
        count += countConsecutive(x, y, xStep, yStep, player);

        // Check in negative direction (backward)
        count += countConsecutive(x, y, -xStep, -yStep, player);

        // Return true if found 4 or more consecutive pieces
        return count >= 4;
    }

    // Count consecutive pieces in a given direction
    private int countConsecutive(int x, int y, int xStep, int yStep, Piece player) {
        int count = 0;

        // Move in the direction specified by xStep and yStep
        for (int i = 1; i < 4; i++) {
            int newX = x + i * xStep;
            int newY = y + i * yStep;

            // Check if the new position is valid and has the same player's piece
            if (newX >= 0 && newX < getColumns() && newY >= 0 && newY < getRows() && getCell(newX, newY) == player) {
                count++;
            } else {
                break;  // Stop counting if we hit an empty cell or different player's piece
            }
        }

        return count;
    }

    // Check for Draws
    public boolean isDraw() {
        for (List<Piece> column : columns) {
            if (column.size() < rows) {
                return false; // There is still room for moves
            }
        }
        return true; // All columns are full, draw
    }

    // Move class to store each move
    public static class Move implements Parcelable{
        public final int column;
        public final int row;
        public final Piece player;

        public Move(int column, int row, Piece player) {
            this.column = column;
            this.row = row;
            this.player = player;
        }

        protected Move(Parcel parcel) {
            column = parcel.readInt();
            row = parcel.readInt();
            player = Piece.values()[parcel.readInt()];
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeInt(column);
            dest.writeInt(row);
            dest.writeInt(player.ordinal());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Move> CREATOR = new Creator<Move>() {
            @Override
            public Move createFromParcel(Parcel in) {
                return new Move(in);
            }

            @Override
            public Move[] newArray(int size) {
                return new Move[size];
            }
        };
    }

    // Clears board
    public void clearBoard() {
        for (List<Piece> column : columns) {
            column.clear();  // Empty each column
        }
        moveHistory.clear();  // Clear the move history
    }

    // Get player 1's win percentage
    public double getPlayer1WinPercentage() {
        return player1GamesPlayed == 0 ? 0 : ((double) player1Wins / player1GamesPlayed) * 100;
    }

    // Get player 2's win percentage
    public double getPlayer2WinPercentage() {
        return player2GamesPlayed == 0 ? 0 : ((double) player2Wins / player2GamesPlayed) * 100;
    }

    // Get player 1 stats
    public int getPlayer1Wins() {
        return player1Wins;
    }

    public int getPlayer1Losses() {
        return player1Losses;
    }

    public int getPlayer1GamesPlayed() {
        return player1GamesPlayed;
    }

    // Get player 2 stats
    public int getPlayer2Wins() {
        return player2Wins;
    }

    public int getPlayer2Losses() {
        return player2Losses;
    }

    public int getPlayer2GamesPlayed() {
        return player2GamesPlayed;
    }

    // Reset stats when exiting to the main activity
    public void resetStats() {
        player1Wins = 0;
        player2Wins = 0;
        player1Losses = 0;
        player2Losses = 0;
        player1GamesPlayed = 0;
        player2GamesPlayed = 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(rows);
        dest.writeInt(columns.size());

        for (List<Piece> column : columns) {
            dest.writeList(column);
        }

        dest.writeList(moveHistory);
        dest.writeInt(player1Wins);
        dest.writeInt(player2Wins);
        dest.writeInt(player1Losses);
        dest.writeInt(player2Losses);
        dest.writeInt(player1GamesPlayed);
        dest.writeInt(player2GamesPlayed);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GameBoard> CREATOR = new Creator<GameBoard>() {
        @Override
        public GameBoard createFromParcel(Parcel in) {
            return new GameBoard(in);
        }

        @Override
        public GameBoard[] newArray(int size) {
            return new GameBoard[size];
        }
    };

    public GameBoard copyGameBoard(int columns, int rows) {
        // Create a new GameBoard instance with the different dimensions
        GameBoard copiedGameBoard = new GameBoard(columns, rows);

        // Copy player stats
        copiedGameBoard.player1Wins = this.player1Wins;
        copiedGameBoard.player2Wins = this.player2Wins;
        copiedGameBoard.player1Losses = this.player1Losses;
        copiedGameBoard.player2Losses = this.player2Losses;
        copiedGameBoard.player1GamesPlayed = this.player1GamesPlayed;
        copiedGameBoard.player2GamesPlayed = this.player2GamesPlayed;

        // Copy columns
        if (columns < this.getColumns()) { // original board is larger
            for (int i = 0; i < columns; i++) {
                List<Piece> originalColumn = this.columns.get(i);
                List<Piece> copiedColumn = new ArrayList<>(originalColumn); // Deep copy column
                copiedGameBoard.columns.set(i, copiedColumn);
            }
        } else { // original board is smaller
            for (int i = 0; i < this.getColumns(); i++) {
                List<Piece> originalColumn = this.columns.get(i);
                List<Piece> copiedColumn = new ArrayList<>(originalColumn); // Deep copy column
                copiedGameBoard.columns.set(i, copiedColumn);
            }
        }

        copiedGameBoard.moveHistory = new ArrayList<>(this.moveHistory); // Deep copy move history

        return copiedGameBoard;
    }
}