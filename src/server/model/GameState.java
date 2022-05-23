package server.model;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.UUID;

public class GameState implements Serializable {

    private String gameID = UUID.randomUUID().toString();

    private int playerTurn;
    private boolean checkMate;

    private String player1;
    private String player2;

    private int timeControl;

    private Timer timer1;
    private Timer timer2;
    private int timer1Time;
    private int timer2Time;

    private Message[] messages;

    private ChessPieceAbstract[][] cpa;

    private boolean started = false;

    public GameState(){
        playerTurn = 1;
        checkMate = false;
    }

    public void prepareTimers(int timeControl) {
        timer1Time = timeControl*60;
        timer2Time = timeControl*60;

        timer1 = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer1Time--;
            }
        });
        timer1.setInitialDelay(0);

        timer2 = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer2Time--;
            }
        });
        timer2.setInitialDelay(0);
    }

    public void startTimer1() {
        timer1.start();
    }

    public void stopTimer1() {
        timer1.stop();
    }

    public void startTimer2() {
        timer2.start();
    }

    public void stopTimer2() {
        timer2.stop();
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    public boolean isCheckMate() {
        return checkMate;
    }

    public void setCheckMate(boolean checkMate) {
        this.checkMate = checkMate;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public Message[] getMessages() {
        return messages;
    }

    public void setMessages(Message[] messages) {
        this.messages = messages;
    }

    public ChessPieceAbstract[][] getCpa() {
        return cpa;
    }

    public void setCpa(ChessPieceAbstract[][] cpa) {
        this.cpa = cpa;
    }

    public String getGameID() {
        return gameID;
    }

    public void setStarted() {
        this.started = true;
    }

    public boolean getStarted() {
        return started;
    }

    public void setTimeControl(int timeControl) {
        this.timeControl = timeControl;
    }

    public int getTimeControl() {
        return timeControl;
    }

    public int getTimer1Time() {
        return timer1Time;
    }

    public int getTimer2Time() {
        return timer2Time;
    }

    public Timer getTimer1() {
        return timer1;
    }

    public Timer getTimer2() {
        return timer2;
    }
}
