package server.model;

import javax.swing.*;
import java.io.Serializable;
import java.util.Timer;
import java.util.UUID;

public class GameState implements Serializable {

    private String gameID = UUID.randomUUID().toString();

    private int playerTurn;
    private boolean checkMate;

    private String player1;
    private String player2;

    private int timeControl;

    private GameTimer timer1;
    private GameTimer timer2;
    private int timeTimer1;
    private int timeTimer2;

    private Message[] messages;

    private ChessPieceAbstract[][] cpa;

    private Timer timer = new Timer("Lucas", true);

    private boolean started = false;

    public GameState(){
        playerTurn = 1;
        checkMate = false;
    }

    public void prepareTimers(int timeControl) {
        timer1 = new GameTimer(timeControl);
        timer2 = new GameTimer(timeControl);
    }

    public void updateCurrentTime() {
        timeTimer1 = Integer.parseInt(timer1.getTime().substring(3,5));
        timeTimer2 = Integer.parseInt(timer2.getTime().substring(3,5));
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

    public GameTimer getTimer1() {
        return timer1;
    }

    public void setTimer1(GameTimer timer1) {
        this.timer1 = timer1;
    }

    public GameTimer getTimer2() {
        return timer2;
    }

    public void setTimer2(GameTimer timer2) {
        this.timer2 = timer2;
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

    public int getTimeTimer1() {
        return timeTimer1;
    }

    public int getTimeTimer2() {
        return timeTimer2;
    }
}
