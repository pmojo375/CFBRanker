package com.mojo;

/**
 * Created by Mojsiejenko on 10/6/15.
 */
public class Game {

    private double scoreDifferential = 0;
    private int winningTeam;
    private int losingTeam;
    private String date;
    private int week;
    private String[] gameData;
    private boolean gameCounts = false;
    private boolean isHome = false;
    private boolean isWinner = false;

    Game(int week, String date, int winningTeam, int losingTeam, int winningScore, int losingScore, boolean isHome, String[] gameData, boolean isWinner, boolean isFBS) {
        this.gameData = gameData;
        this.winningTeam = winningTeam;
        this.losingTeam = losingTeam;
        this.isWinner = isWinner;
        this.week = week;
        this.date = date;

        gameCounts = isFBS;
    }

    public double getScoreDifferential() {
        return scoreDifferential;
    }

    public int getWeek() {
        return week;
    }

    public boolean wasWin() {
        return isWinner;
    }

    public boolean wasHome() {
        return isHome;
    }

    public boolean wasFBS() {
        return gameCounts;
    }

    public int getOpponent() {
        if(isWinner) {
            return losingTeam;
        } else {
            return winningTeam;
        }
    }

    public String[] getGameData() {
        return gameData;
    }
}

