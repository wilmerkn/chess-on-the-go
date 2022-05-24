package client;

import client.gameview.BoardPanel;
import client.gameview.GameView;
import client.lobby.LobbyView;
import client.login.LoginView;
import server.controller.LoginController;
import server.model.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
    private static final String HOST = "2.tcp.ngrok.io";
    private static final int PORT = 15217;

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private LoginView loginView;
    private LobbyView lobbyView;
    private GameView gameView;

    private String username;
    private String gameID;

    private Timer timer1;
    private int timer1Time;
    private Timer timer2;
    private int timer2Time;


    public Client() {
        LoginController loginController = new LoginController();
        loginView = new LoginView(loginController, this);
        connect();
        new ServerListener().start();
    }

    public void login(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        try {
            oos.writeObject(loginRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void challenge(String receiverUsername, int timeControl) {
        ChallengeRequest challenge = new ChallengeRequest(username, receiverUsername, timeControl);
        try {
            oos.writeObject(challenge);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMove(Move move) {
        move.setGameID(gameID);
        try {
            oos.reset();
            oos.writeObject(move);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void connect() {
        try {
            socket = new Socket(HOST, PORT);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Client disconnected");
            e.printStackTrace();
        }
    }

    private class ServerListener extends Thread {

        @Override
        public void run() {
            try {
                while(true) {

                    Object obj = ois.readObject();
                    if(obj instanceof LoginRequest) {
                        LoginRequest request = (LoginRequest) obj;
                        if(!request.isAccepted()) {
                            JOptionPane.showMessageDialog(null, "Login failed");
                        } else {
                            username = request.getUsername();
                            lobbyView = new LobbyView(Client.this);
                            loginView.closeLoginWindow();
                        }
                    } else if(obj instanceof ChallengeRequest) {
                        ChallengeRequest challenge = (ChallengeRequest) obj;
                        String sender = challenge.getSenderUsername();
                        int answer = JOptionPane.showConfirmDialog(
                                null,
                                String.format("Do you want to accept incoming challenge from %s?", sender),
                                "Incoming challenge",
                                JOptionPane.YES_NO_OPTION
                        );

                        if(answer == 0) {
                            challenge.accept();
                            oos.reset();
                            oos.writeObject(challenge);
                            oos.flush();
                            lobbyView.dispose();
                            //set timeControl

                        }

                    } else if (obj instanceof GameState) {
                        GameState state = (GameState) obj;
                        //if a move is made by one player, others timer should start
                        //System.out.println(username + " tar emot gamestate. ID: " + gameID);
                        gameID = state.getGameID();
                        //get playerTurn, depending on whos turn stop and start timers

                        if(!state.getStarted()) {
                            timer1Time = state.getTimer1Time();
                            timer2Time = state.getTimer2Time();

                            gameView = new GameView(Client.this);
                            timer1 = new Timer(1000, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    timer1Time--;
                                    gameView.setPlayer1Time((timer1Time /60) + ":" + (timer1Time %60));
                                    if(timer1Time==0){
                                        timer1.stop();
                                        //should sent signal to server that one player wins, register result in DB in "stats" and then terminate game
                                        System.out.println("Player 2 wins on time");
                                    }

                                }
                            });
                            gameView.setPlayer1Time(state.getTimeControl()+ ":00");

                            timer1.setInitialDelay(0);

                            timer2 = new Timer(1000, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    timer2Time--;
                                    gameView.setPlayer2Time((timer2Time /60) + ":" + (timer2Time %60));
                                    if(timer2Time==0){
                                        timer2.stop();
                                        System.out.println("Player 1 wins on time");
                                    }
                                }
                            });
                            gameView.setPlayer2Time(state.getTimeControl()+ ":00");


                            timer2.setInitialDelay(0);

                            //starts timers right away?
                        }
                        if (state.getPlayerTurn() % 1 != 0 && state.getStarted()){
                            startPlayer1Time();
                        }
                        if (state.getPlayerTurn() %1 == 0 && state.getStarted()){
                            startPlayer2Time();
                        }



                        gameView.setPlayer1Name(state.getPlayer1());
                        gameView.setPlayer2Name(state.getPlayer2());

                        drawMap(state.getCpa());

                        //ToDo timer etc


                    } else if (obj instanceof Message) {
                        // Lägg message i textArea
                    } else if(obj instanceof ArrayList) {
                        ArrayList<String> players = (ArrayList<String>) obj; // No problem
                        //
                        // System.out.println("CLIENT: players length" + players.size());

                        lobbyView.getUserPanel().setOnlinePlayers(players);

                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                disconnect();
            }
        }
    }

    //ToDo Rita upp skicka över nätverk
    public void drawMap(ChessPieceAbstract[][] cpa){
        HashMap<String, JLabel> notationLbl = gameView.getBoardPanel().getNotationToJLMap();
        ChessPieceAbstract[][] gamemap = cpa;
        BoardPanel.SquarePanel[][] sqp = gameView.getBoardPanel().getSquares();

        cleanBoard();
        for(int row = 0; row < 8; row++){
            for(int col = 0; col < 8; col++){
                if(gamemap[row][col] != null){

                    ChessPiece chessPiece = (ChessPiece) gamemap[row][col];
                    sqp[row][col].placePiece(notationLbl.get(chessPiece.getSpriteName()));

                }
            }
        }
    }

    public void cleanBoard(){
        BoardPanel.SquarePanel[][] squarePanel = gameView.getBoardPanel().getSquares();

        for(int row = 0; row < squarePanel.length; row++){
            for(int col = 0; col < squarePanel[row].length; col++){
                squarePanel[row][col].removePiece();
            }
        }
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public static void main(String[] args) {
        new Client();
    }

    public void startPlayer1Time(){
        timer2.stop();
        timer1.start();
    }
    public void startPlayer2Time(){
        timer1.stop();
        timer2.start();
    }
}
