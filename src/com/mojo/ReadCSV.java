package com.mojo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mojsiejenko on 10/6/15.
 */
public class ReadCSV {

    private ArrayList teams = new ArrayList();
    private String file;

    public void readTeams(String file) {
        String csvFile = file;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                line = line.trim();

                // use comma as separator
                String[] game = line.split(cvsSplitBy);

                for(int i = 0; i < game.length; i++) {
                    game[i] = game[i].trim();
                }

                teams.add(game);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList getTeams(int week) {
        switch(week) {
            case 0:
                file = "/Users/Mojsiejenko/IdeaProjects/CFBRanker/src/com/mojo/cfb_complete.csv";
                break;
            case 1:
                file = "/Users/Mojsiejenko/IdeaProjects/CFBRanker/src/com/mojo/cfb_week1.csv";
                break;
            case 2:
                file = "/Users/Mojsiejenko/IdeaProjects/CFBRanker/src/com/mojo/cfb_week2.csv";
                break;
            case 3:
                file = "/Users/Mojsiejenko/IdeaProjects/CFBRanker/src/com/mojo/cfb_week3.csv";
                break;
            case 4:
                file = "/Users/Mojsiejenko/IdeaProjects/CFBRanker/src/com/mojo/cfb_week4.csv";
                break;
            case 5:
                file = "/Users/Mojsiejenko/IdeaProjects/CFBRanker/src/com/mojo/cfb_week5.csv";
                break;
            case 6:
                file = "/Users/Mojsiejenko/IdeaProjects/CFBRanker/src/com/mojo/team.csv";
                break;
            case 7:
                file = "/Users/Mojsiejenko/IdeaProjects/CFBRanker/src/com/mojo/conference.csv";
                break;
            case 8:
                file = "/Users/Mojsiejenko/IdeaProjects/CFBRanker/src/com/mojo/games.csv";
                break;
        }

        readTeams(file);
        return teams;
    }
}
