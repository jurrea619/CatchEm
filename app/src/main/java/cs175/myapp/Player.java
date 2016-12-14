package cs175.myapp;

/**
 * Created by joshua on 12/9/16.
 */

public class Player {

    private String name;
    private int score;

    /*
    Initialize new player for current game and score recording
     */
    public Player(String userName){
        name = userName;
        score = 0;
    }

    //Get player name
    public String getName(){

        return name;
    }

    //increment score by given parameter
    public void incrementScore(int newScore){
        score += newScore;
    }

    //get player score
    public int getScore(){

        return score;
    }

}
