package com.mojo;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;

/**
 * Created by Mojsiejenko on 10/6/15.
 */
public class Team {

    public int name;
    private double colleyRanking;
    private boolean is_fbs = false;
    private double wins = 0;
    private double losses = 0;
    private int h_wins = 0;
    private int h_loses = 0;
    private int a_wins = 0;
    private int a_loses = 0;
    private double win_percentage = 0;
    private double colleyWinPercentage = 0;
    private double sos;
    private int stadium_cap;
    private ArrayList<Game> games = new ArrayList<Game>();
    private ArrayList<Integer> schedule = new ArrayList<Integer>();
    private double scoreDifferentialFactor = 0;
    private double totalOffYards = 0;
    private double totalDefYards = 0;
    private int winPercentageRank;
    private int colleyRank;
    private int sosRank;

    // constructor for team name and index is automatically assigned
    Team(int name) {
        this.name = name;
    }

    public void addGame(int week, String date, int winningTeam, int losingTeam, int winningScore, int losingScore, boolean homeGame, String[] gameData, boolean isWinner, boolean isFBS) {
        games.add(new Game(week, date, winningTeam, losingTeam, winningScore, losingScore, homeGame, gameData, isWinner, isFBS));

        computeRecord();
        computeRecordColley();
    }

    public int getName() {
        return name;
    }

    public void setFBS() {
        is_fbs = true;
    }

    public boolean isFBS() {
        return is_fbs;
    }

    public double getRawSos() {
        return sos;
    }

    public void setColleyRanking(double colleyRanking) {
        this.colleyRanking = colleyRanking;
    }

    public double getColleyRanking() {
        return colleyRanking;
    }

    // excludes FCS teams and is run every time a game is added
    public void computeRecord() {

        wins = 0;
        losses = 0;
        a_wins = 0;
        a_loses = 0;
        h_wins = 0;
        h_loses = 0;

        for(int i = 0; i  < games.size(); i++) {
            if(games.get(i).wasWin() && games.get(i).wasFBS()) {
                wins = wins + 1;
                if(games.get(i).wasHome()) {
                    h_wins++;
                } else {
                    a_wins++;
                }
            } else if(!games.get(i).wasWin() && games.get(i).wasFBS()) {
                losses++;
                if(games.get(i).wasHome()) {
                    h_loses++;
                } else {
                    a_loses++;
                }
            }
        }

        if((wins + losses) > 0) {
            win_percentage = wins/(wins + losses);
        }
    }

    public void computeAverageGameYardage() {

        double gameFactor = 0;

        for(Game game: games) {
            if(game.wasWin() && !game.wasHome()) {
                gameFactor = gameFactor + 1;
            }

            if(game.wasWin()) {
                totalOffYards = Double.parseDouble(game.getGameData()[19]) + totalOffYards;
                totalDefYards = Double.parseDouble(game.getGameData()[40]) + totalDefYards;
            } else {
                totalOffYards = Double.parseDouble(game.getGameData()[40]) + totalOffYards;
                totalDefYards = Double.parseDouble(game.getGameData()[19]) + totalDefYards;
            }
        }

        totalOffYards = totalOffYards/games.size();
        totalDefYards = totalDefYards/games.size();

        System.out.println(getName() + " - " + totalOffYards + " - " + totalDefYards);

    }

    public void factorScoreDiff(ArrayList teamsMaster) {

        ArrayList<Team> teams = teamsMaster;
        double scoreDiff = 0;
        double teamSos = 0;

        if(isFBS()) {
            // for each opponent on your schedule
            for (int i = 0; i < games.size(); i++) {
                scoreDiff = games.get(i).getScoreDifferential();
                for(Team team : teams) {
                    if(team.getName() == (games.get(i).getOpponent())) {
                        teamSos = team.getRawSos();
                    }
                }

                scoreDifferentialFactor = scoreDifferentialFactor + (teamSos*scoreDiff);
            }
        }
    }

    public void computeRecordColley() {

        wins = 0;
        losses = 0;
        a_wins = 0;
        a_loses = 0;
        h_wins = 0 ;
        h_loses = 0;

        for(int i = 0; i  < games.size(); i++) {
            if(games.get(i).wasWin() && games.get(i).wasFBS()) {
                wins++;
                if(games.get(i).wasHome()) {
                    h_wins++;
                } else {
                    a_wins++;
                }
            } else if(!games.get(i).wasWin() && games.get(i).wasFBS()) {
                losses++;
                if(games.get(i).wasHome()) {
                    h_loses++;
                } else {
                    a_loses++;
                }
            }
        }

        if((wins + losses) > 0) {
            colleyWinPercentage = (wins + 1)/(wins + losses + 2);
        }
    }

    public double computeSos(ArrayList teamsMaster) {
        double oRecord = 0;
        double oRecordIndex = 0;
        double ooRecord = 0;
        double ooRecordIndex = 0;

        ArrayList<Team> teams = teamsMaster;

        if(isFBS()) {
            // for each opponent on your schedule
            for(int i = 0; i < schedule.size(); i++) {
                int oppName = schedule.get(i);
                ArrayList<Integer> oppOppNameList;

                // for each FCS team check if its the opponent
                for(int j = 0; j < teams.size(); j++) {
                    // if it is the opponent run logic to add too or
                    if((teams.get(j).getName() == oppName) && teams.get(i).isFBS()) {
                        oRecord = oRecord + teams.get(j).getWinPercentage();
                        oRecordIndex++;

                        // get opponents opponents
                        oppOppNameList = teams.get(j).getSchedule();

                        // for each opponent on the opponents opponent list
                        for(int k = 0; k < oppOppNameList.size(); k++) {
                            // for each FCS team check if its the opponents opponent
                            for(int l = 0; l < teams.size(); l++) {
                                // if it is the opponents opponent then run logic to add to oor
                                if((teams.get(l).getName() == oppOppNameList.get(k)) && teams.get(l).isFBS()) {
                                    ooRecord = ooRecord + teams.get(l).getWinPercentage();
                                    ooRecordIndex++;
                                }
                            }
                        }
                    }
                }
            }

            // finally compute SOS
            double oRecordPercent = 0;
            double ooRecordPercent = 0;

            if(oRecordIndex != 0) {
                oRecordPercent = oRecord / oRecordIndex;
            }

            if(ooRecordIndex != 0) {
                ooRecordPercent = ooRecord / ooRecordIndex;
            }

            sos = (2 * (oRecordPercent) + (ooRecordPercent)) / 3;

            return sos;
        }

        return 0;
    }

    public double getScoreDiffFactor() {
        return scoreDifferentialFactor/50;
    }

    public double normalizeSos(double highest) {
        sos = (sos / highest);
        return sos;
    }

    public ArrayList getSchedule() {
        return schedule;
    }

    public double getWins() {
        return wins;
    }

    public double getLosses() {
        return losses;
    }

    public double getWinPercentage() {
        return win_percentage;
    }

    public String[] getGameData(int index) {
        return games.get(index).getGameData();
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public void initTeamData(ArrayList teamMaster) {
        for(int i = 0; i < games.size(); i++) {
            schedule.add(games.get(i).getOpponent());
        }

        computeSos(teamMaster);
    }

    public void setWinPercentageRank(int i) {
        winPercentageRank = i;
    }

    public void setColleyRank(int i) {
        colleyRank = i;
    }

    public void setSosRank(int i) {
        sosRank = i;
    }

    public int getRankingColley() {
        return colleyRank;
    }

    public int getRankingSOS() {
        return sosRank;
    }

    public int getRankingWinPercentage() {
        return winPercentageRank;
    }
}

