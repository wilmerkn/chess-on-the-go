package model;

import view.GameView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//todo currently only shows current time. change to time passed in game
public class GameTimerTask implements Runnable{

    private boolean stopFlag; //stop flag for while loop
    private GameView view; //access to view to change text in components
    private DateTimeFormatter formatter; //formatter to format correct time

    public GameTimerTask(){
        this.view = null;
        stopFlag = false;
        formatter = DateTimeFormatter.ofPattern("HH:mm:ss"); //time in hours, minutes, seconds
    }
    public GameTimerTask(GameView view){
        this.view = view;
        stopFlag = false;
        formatter = DateTimeFormatter.ofPattern("HH:mm:ss"); //time in hours, minutes, seconds
    }

    public void setStopFlag(boolean stopFlag) {
        this.stopFlag = stopFlag;
    }

    @Override
    public void run() {
        while(!stopFlag){
            try{

                LocalDateTime time = LocalDateTime.now(); //get local time
                String clockdisplay = time.format(formatter); //format time
                //view.getClock().setText(clockdisplay); //display time in GUI

                System.out.println(clockdisplay);

                Thread.sleep(1000); //wait 1 sec

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
