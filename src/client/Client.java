package client;

import client.gameview.BoardPanel;
import client.gameview.GameView;
import client.lobby.LobbyView;
import client.login.LoginView;
import server.controller.GameLogic;
import server.controller.LoginController;
import server.model.*;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 1234;

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private LoginView loginView;
    private LobbyView lobbyView;
    private GameView gameView;

    private String username;
    private String gameID;

    private GameTimer timer1;
    private GameTimer timer2;

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

                        }

                    } else if (obj instanceof GameState) {
                        GameState state = (GameState) obj;

                        System.out.println(username + " tar emot gamestate. ID: " + gameID);
                        gameID = state.getGameID();

                        if(!state.getStarted()) {
                            gameView = new GameView(Client.this);
                            timer1 = new GameTimer(state.getTimeControl());
                            timer2 = new GameTimer(state.getTimeControl());
                        }

                        gameView.setPlayer1Time("" + state.getTimeTimer1());
                        gameView.setPlayer2Time("" + state.getTimeTimer2());


                        gameView.setPlayer1Name(state.getPlayer1());
                        gameView.setPlayer2Name(state.getPlayer2());

                        timer1.turnOn();
                        timer2.turnOn();


                        drawMap(state.getCpa());


                        //ToDo fixa player names labels timer etc


                    } else if (obj instanceof Message) {
                        // Lägg message i textArea
                    } else if(obj instanceof ArrayList) {
                        ArrayList<String> players = (ArrayList<String>) obj; // No problem
                        System.out.println("CLIENT: players length" + players.size());

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
}
