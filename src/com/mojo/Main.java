package com.mojo;

import org.apache.commons.math3.linear.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {

    public static String[] teamArray = {"Air Force", "Akron", "Alabama", "Appalachian State", "Arizona", "Arizona State", "Arkansas", "Arkansas State",
            "Army", "Auburn", "Ball State", "Baylor", "Boise State", "Boston College", "Bowling Green", "Buffalo", "BYU", "California",
            "Central Michigan", "Charlotte", "Cincinnati", "Clemson", "Colorado", "Colorado State", "Duke", "Eastern Michigan", "East Carolina",
            "Florida Atlantic", "FIU", "Florida", "Fresno State", "Florida State", "Georgia Southern", "Georgia", "Georgia State", "Georgia Tech", "Hawaii",
            "Houston", "Idaho", "Illinois", "Indiana", "Iowa", "Iowa State", "Kansas", "Kansas State", "Kent State", "Kentucky",
            "La. Tech", "La.-Lafayette", "Louisville", "LSU", "Marshall", "Maryland", "Memphis", "Miami FL", "Miami OH",
            "Michigan", "Michigan State", "MTSU", "Minnesota", "Mississippi State", "Missouri", "Northern Illinois", "Navy", "Nebraska",
            "Nevada", "New Mexico", "New Mexico State", "N.C. State", "North Texas", "Northwestern", "Notre Dame", "Ohio",
            "Oklahoma", "Oklahoma State", "Old Dominion", "Mississippi", "Oregon", "Oregon State", "Ohio State", "Penn State", "Pittsburgh",
            "Purdue", "Rice", "Rutgers", "San Diego State", "San Jose State", "SMU", "South Alabama", "South Carolina",
            "Southern Mississippi", "Stanford", "Syracuse", "TCU", "Temple", "Tennessee", "Texas", "Texas A&M", "Texas State", "Texas Tech",
            "Toledo", "Troy", "Tulane", "Tulsa", "UCF", "UCLA", "Connecticut", "La.-Monroe", "Massachusetts", "North Carolina", "UNLV", "Southern Cal", "South Florida", "Utah",
            "Utah State", "UTEP", "UTSA", "Virginia", "Vanderbilt", "Virginia Tech", "Western Kentucky", "Western Michigan", "Wake Forest", "Washington",
            "Washington State", "West Virginia", "Wisconsin", "Wyoming"};

    public static ArrayList<String[]> games = new ArrayList<String[]>();
    public static ArrayList<String> teams = new ArrayList<String>();
    public static ArrayList<Team> teamMaster = new ArrayList<Team>();
    public static ArrayList<Integer> teamCodesMaster = new ArrayList<Integer>();
    public static ReadCSV mReadCSV = new ReadCSV();
    public static Team tempTeam;
    public static boolean addTeam = false;
    public static ArrayList<String[]> rawData;
    public static double[][] matrix = new double[128][128];
    public static double[] matrixRow = new double[128];
    public static RealMatrix realMatrixA;
    public static RealVector realMatrixB;
    public static DecompositionSolver solver;
    public static ArrayList<Team> sortedTeamsColley = new ArrayList();
    public static ArrayList<Team> sortedTeamsSos = new ArrayList();
    public static ArrayList<Team> sortedTeamsWinPercentage = new ArrayList();
    private static ArrayList<String[]> rawGameData;
    private static ArrayList<String[]> rawConferenceData;
    private static ArrayList<String[]> rawTeamData;

    public static void main(String[] args)  {

        rawData = mReadCSV.getTeams(0);
        mReadCSV = new ReadCSV();
        rawGameData = mReadCSV.getTeams(8);
        mReadCSV = new ReadCSV();
        rawTeamData = mReadCSV.getTeams(6);
        mReadCSV = new ReadCSV();
        rawConferenceData = mReadCSV.getTeams(7);

        for(int i = 0; i < teamCodesMaster.size(); i++) {
            teamMaster.add(new Team(teamCodesMaster.get(i)));
            teamMaster.get(i).setFBS();
        }

        for(int i = 1; i < 128; i++) {
            teamCodesMaster.add(Integer.parseInt(rawTeamData.get(i)[0]));
        }

        // populate the team objects
        parseScraper();

        // cycle through the teams and compute results
        for (int i = 0; i < teamMaster.size(); i++) {
            teamMaster.get(i).initTeamData(teamMaster);
        }

        // build the matrix
        buildColleyMatrix();

        // sort the teams in the ArrayList for display
        sortTeams();


        Scanner in = new Scanner(System.in);  // System.in is an InputStream

        System.out.println("Enter colley for Colley rankings, sos for SOS rank ");
        System.out.println("or winpercentage for win percentage rank or enter a team name");

        String input;

        while(true) {
            try {
                input = in.nextLine();

                for (Team team : teamMaster) {
                    if (input.equals(team.getName())) {
                        printTeam(input);
                    }
                }

                if (input.equals("sos")) {
                    printRanks(1);
                } else if (input.equals("wins")) {
                    printRanks(2);
                } else if (input.equals("colley")) {
                    printRanks(0);
                }

            } catch (Exception e) {
                System.out.println(e);
            }
        }


        // checks for misspellings and errors in the CSV file
        //debugTeamCSVFile();
    }

    public static void printTeam(String input) {
        for(Team team : teamMaster) {
            if(team.getName() == (Integer.parseInt(input))) {
                System.out.println("Team: " + team.getName());
                System.out.println("Record: " + team.getWins() + " - " + team.getLosses());
                System.out.println("Win Percentage Rank: " + team.getRankingWinPercentage() + " at " + team.getWinPercentage());
                System.out.println("Colley Rank: " + team.getRankingColley() + " at " + team.getColleyRanking());
                System.out.println("SOS Rank: " + team.getRankingSOS() + " at " + team.getRawSos());
            }
        }
    }

    public static void sortTeams() {

        sortedTeamsColley.clear();
        sortedTeamsWinPercentage.clear();
        sortedTeamsSos.clear();

        int highestIndex = 0;
        ArrayList<Team> sortedTeams = new ArrayList();
        double tempData;

        // for all the teams in FBS
        for(int i = 0; i < 128; i++) {

            // pull an element to compare to
            tempTeam = teamMaster.get(0);

            tempData = tempTeam.getWinPercentage();

            highestIndex = 0;

            for(int j = 0; j < teamMaster.size(); j++) {
                if(teamMaster.get(j).getWinPercentage() >= tempData) {
                    tempTeam = teamMaster.get(j);
                    tempData = tempTeam.getWinPercentage();
                    highestIndex = j;
                }
            }

            sortedTeams.add(tempTeam);
            teamMaster.remove(highestIndex);
        }

        teamMaster.addAll(sortedTeams);

        for(int i = 0; i < teamMaster.size(); i++) {
            teamMaster.get(i).setWinPercentageRank(i+1);
        }

        sortedTeamsWinPercentage.addAll(teamMaster);
        sortedTeams.clear();

        // for all the teams in FBS
        for(int i = 0; i < 128; i++) {

            // pull an element to compare to
            tempTeam = teamMaster.get(0);

            tempData = tempTeam.getRawSos();

            highestIndex = 0;

            for(int j = 0; j < teamMaster.size(); j++) {

                if(teamMaster.get(j).getRawSos() >= tempData) {
                    tempTeam = teamMaster.get(j);
                    tempData = tempTeam.getRawSos();
                    highestIndex = j;
                }
            }

            sortedTeams.add(tempTeam);
            teamMaster.remove(highestIndex);
        }

        teamMaster.addAll(sortedTeams);

        for(int i = 0; i < teamMaster.size(); i++) {
            teamMaster.get(i).setSosRank(i + 1);
        }

        sortedTeamsSos.addAll(teamMaster);
        sortedTeams.clear();

        // for all the teams in FBS
        for(int i = 0; i < 128; i++) {

            // pull an element to compare to
            tempTeam = teamMaster.get(0);

            tempData = tempTeam.getColleyRanking();

            highestIndex = 0;

            for(int j = 0; j < teamMaster.size(); j++) {

                if(teamMaster.get(j).getColleyRanking() >= tempData) {
                    tempTeam = teamMaster.get(j);
                    tempData = tempTeam.getColleyRanking();
                    highestIndex = j;
                }
            }

            sortedTeams.add(tempTeam);
            teamMaster.remove(highestIndex);
        }

        teamMaster.addAll(sortedTeams);

        sortedTeamsColley.addAll(teamMaster);
        sortedTeams.clear();

        for(int i = 0; i < teamMaster.size(); i++) {
            teamMaster.get(i).setColleyRank(i + 1);
        }
    }

    public static void printRanks(int i) {
        int j;

        switch(i) {
            case 0:
                j = 1;

                System.out.println("Teams sorted by Colley Ranking:");

                for(Team team : sortedTeamsColley) {
                    System.out.println(j + " - " + team.getName() + " at " + team.getColleyRanking());

                    j++;
                }
                break;
            case 1:
                j = 1;

                System.out.println("Teams sorted by SOS:");

                for(Team team : sortedTeamsSos) {
                    System.out.println(j + " - " + team.getName() + " at " + team.getRawSos());

                    j++;
                }
                break;
            case 2:
                j = 1;

                System.out.println("Teams sorted by win %:");

                for(Team team : sortedTeamsWinPercentage) {
                    System.out.println(j + " - " + team.getName() + " at " + team.getWinPercentage());

                    j++;
                }
                break;
        }
    }

    public static void buildColleyMatrix() {

        Team rowTeam;
        Team columnTeam;

        for(int row = 0; row < teamMaster.size(); row++) {

            rowTeam = teamMaster.get(row);

            matrixRow[row] = 1 + (rowTeam.getWins() - rowTeam.getLosses())/2;

            for(int column = 0; column < teamMaster.size(); column++) {

                columnTeam = teamMaster.get(column);

                if(row == column) {
                    matrix[row][column] = (rowTeam.getWins() + rowTeam.getLosses() + 2);
                } else {

                    matrix[row][column] = 0;

                    // get row teams games
                    for(int k = 0; k < rowTeam.getGames().size(); k++) {
                        if(rowTeam.getGames().get(k).getOpponent() == (columnTeam.getName())) {
                            matrix[row][column] = -1;
                        }
                    }
                }
            }
        }

        realMatrixA = new Array2DRowRealMatrix(matrix, false);
        realMatrixB = new ArrayRealVector(matrixRow, false);
        solver = new CholeskyDecomposition(realMatrixA).getSolver();
        RealVector solution = solver.solve(realMatrixB);

        // set each teams Colley result
        for(int i = 0; i < 128; i++) {
            teamMaster.get(i).setColleyRanking(solution.getEntry(i));
        }
    }

    /*
    public static void populateData() {

        boolean winnerOK;
        boolean loserOK;
        boolean isFBS;

        for(int i = 0; i < teamArray.length; i++) {
            teamMaster.add(new Team(teamArray[i]));
            teamMaster.get(i).setFBS();
        }

        for(int i = 0; i < rawData.size(); i++) {

            String[] stringData = rawData.get(i);

            if(stringData[0].equals("2015")) {
                for(int j = 0; j < teamMaster.size(); j++) {

                    if(teamCodesMaster.get(j) == (Integer.parseInt(stringData[4]))) {

                        winnerOK = false;
                        loserOK = false;
                        isFBS = false;

                        for(int k = 0; k < teamMaster.size(); k++) {
                            if(stringData[4].equals(teamMaster.get(k).getName())) {
                                winnerOK = true;
                            }

                            if(stringData[25].equals(teamMaster.get(k).getName())) {
                                loserOK = true;
                            }
                        }

                        if(winnerOK && loserOK) {
                            isFBS = true;
                        }

                        //teamMaster.get(j).addGame(Integer.parseInt(stringData[3]), stringData[3], stringData[4], stringData[25], Integer.parseInt(stringData[7]), Integer.parseInt(stringData[28]), stringData[6], stringData, true, isFBS);
                    }

                    if(teamMaster.get(j).getName().equals(stringData[25])) {

                        winnerOK = false;
                        loserOK = false;
                        isFBS = false;

                        for(int k = 0; k < teamMaster.size(); k++) {
                            if(stringData[4].equals(teamMaster.get(k).getName())) {
                                winnerOK = true;
                            }

                            if(stringData[25].equals(teamMaster.get(k).getName())) {
                                loserOK = true;
                            }
                        }

                        if(winnerOK && loserOK) {
                            isFBS = true;
                        }

                        teamMaster.get(j).addGame(Integer.parseInt(stringData[3]), stringData[3], stringData[4], stringData[25], Integer.parseInt(stringData[7]), Integer.parseInt(stringData[28]), stringData[27], stringData, false, isFBS);
                    }
                }
            }
        }
    }
    */

    public static void debugTeamCSVFile() {
        // tests the teams and prints the teams not recognized regardless of if one was fbs
        boolean teamAok;
        boolean teamBok;

        for(int i = 0; i < rawData.size(); i++) {
            teamAok = false;
            teamBok = false;
            for(int j = 0; j < teamMaster.size(); j++) {
                if(rawData.get(i)[4].equals(teamMaster.get(j).getName())) {
                    teamAok = true;
                }
            }

            for(int j = 0; j < teamMaster.size(); j++) {
                if(rawData.get(i)[25].equals(teamMaster.get(j).getName())) {
                    teamBok = true;
                }
            }

            if(!teamAok) {
                System.out.println(rawData.get(i)[4]);
            }

            if(!teamBok) {
                System.out.println(rawData.get(i)[25]);
            }
        }
    }

    // returns the game from a specific team for a specific week
    public static Game getTeamGameByWeek(int week, int teamName) {

        ArrayList<Game> games = new ArrayList();
        Game weekGame = null;

        for(Team team : teamMaster) {
            if(team.getName() == (teamName)) {
                games.addAll(team.getGames());

                for(Game game : games) {
                    if(game.getWeek() == week) {
                        weekGame = game;
                    }
                }
            }
        }

        return weekGame;
    }

    // returns an ArrayList of all the games in the range inputted
    public static ArrayList getWeek(int startWeek, int endWeek) {

        ArrayList<Game> games = new ArrayList();
        ArrayList<Game> weeksGames = new ArrayList();

        for(Team team : teamMaster) {
            games.addAll(team.getGames());

            for(Game game : games) {
                if(game.getWeek() >= startWeek && game.getWeek() <= endWeek) {
                    weeksGames.add(game);
                }
            }

            games.clear();
        }

        return weeksGames;
    }

    public static void parseScraper() {

        String tempGameCode = null;
        int tempVisitorScore;
        boolean homeGame;
        boolean teamWon;


        for(int i = 1; i < rawGameData.size(); i++) {

            tempGameCode = rawGameData.get(i)[1];
            tempVisitorScore = Integer.parseInt(rawGameData.get(i)[35]);

            homeGame = false;
            teamWon = false;

            for(int j = 0; j < rawGameData.size(); j++) {
                if(tempGameCode.equals(rawGameData.get(j)[1])) {
                    if(j > i) {
                        homeGame = true;
                    }

                    if(tempVisitorScore > Integer.parseInt(rawGameData.get(j)[35])) {
                        teamWon = true;
                    }

                }
            }

            for(Team team : teamMaster) {

                String[] rawStringData = rawGameData.get(i);
                int winningTeam = 0;
                int losingTeam = 0;
                int winningPoints = 0;
                int losingPoints = 0;

                if(teamWon) {
                    winningTeam = Integer.parseInt(rawStringData[0]);
                    losingTeam = Integer.parseInt(rawGameData.get(i+1)[0]);

                    winningPoints = Integer.parseInt(rawStringData[35]);
                    losingPoints = Integer.parseInt(rawGameData.get(i+1)[35]);
                } else {
                    losingTeam = Integer.parseInt(rawStringData[0]);
                    winningTeam = Integer.parseInt(rawGameData.get(i+1)[0]);

                    losingPoints = Integer.parseInt(rawStringData[35]);
                    winningPoints = Integer.parseInt(rawGameData.get(i+1)[35]);
                }

                team.addGame(parseWeek(rawStringData[68]), rawStringData[68], winningTeam, losingTeam, winningPoints, losingPoints, homeGame, rawGameData.get(i), teamWon, true);
            }

        }
    }

    public static int parseWeek(String week) {
        int date = Integer.parseInt(week);
        int year = date / 10000;
        int month = (date % 10000) / 100;
        int day = date % 100;
        int total = year + month + day;

        if(year == 2015) {
            if(month == 9) {
                if(day < 7) {
                    return 1;
                } else if(day < 14) {
                    return 2;
                } else if(day < 21) {
                    return 3;
                } else if(day < 28) {
                    return 4;
                } else {
                    return 5;
                }
            } else if(month == 10) {
                if(day < 5) {
                    return 5;
                } else if(day < 12) {
                    return 6;
                } else if(day < 19) {
                    return 7;
                } else if(day < 26) {
                    return 8;
                } else {
                    return 9;
                }
            } else if(month == 11) {
                // TODO: add november week numbers
            }
        }

        return 0;
    }
}


/*
        // find the highest SOS to normalize the data
        double tempSos = 0;

        for (Team team : teamMaster) {
            if (team.getRawSos() > tempSos) {
                tempSos = team.getRawSos();
            }
        }
 */