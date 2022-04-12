package model;

import view.GameView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;

//task inherited by a thread to start a timer
public class GameTimerTask implements Runnable{

    private boolean stopFlag; //stop flag for while loop
    private GameView view; //access to view to change text in components

    private int hours;
    private int minutes;
    private int seconds;
    private String hour;
    private String minute;
    private String second;
    private static final int N = 60;

    public GameTimerTask(){
        this.view = null;
        stopFlag = false;
    }
    public GameTimerTask(GameView view){
        this.view = view;
        stopFlag = false;
    }

    public void setStopFlag(boolean stopFlag) {
        this.stopFlag = stopFlag;
    }

    @Override
    public void run() {

        while(!stopFlag){
            try{

                NumberFormat formatter = new DecimalFormat("00");
                if (seconds == N) {
                    seconds = 00; //increment time
                    minutes++;
                }

                if (minutes == N) {
                    minutes = 00;
                    hours++;
                }
                hour = formatter.format(hours);
                minute = formatter.format(minutes); //formats time to 00 unit
                second = formatter.format(seconds);
                String clockTime = String.valueOf(hour + ":" + minute + ":" + second);
                System.out.println(clockTime); //debugging, remove when set in GUI
                seconds++;

                Thread.sleep(1000); //wait 1 sec

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
