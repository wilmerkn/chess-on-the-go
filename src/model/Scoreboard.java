package model;
import java.sql.Time;

public class Scoreboard {
    private Player[] player;
    private String name;
    private Time time;
    private int score;

    //constructor
    public Scoreboard(Player[] player, String name, Time time, int score){
        this.player = player;
        this.score = score;
        this.time = time;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
