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
    private static final String HOST = "4.tcp.ngrok.io";
    private static final int PORT = 16396;

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

    private GameState state;


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
        gameView.getBoardPanel().disableMouseListeners();
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

    public String getUsername() {
        return username;
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
                        state = (GameState) obj;
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
                        }
                        if(state.getPlayerTurn() % 1 == 0 && state.getPlayer1White() == 1 && username.equals(state.getPlayer1())) {
                            gameView.getBoardPanel().enableMouseListeners();
                        } else {
                            gameView.getBoardPanel().disableMouseListeners();
                        }

                        if (state.getPlayerTurn() % 1 != 0 && state.getStarted()){ // Svarts tur!
                            gameView.getBoardPanel().enableMouseListeners();
                            startPlayer1Time();
                        }
                        if (state.getPlayerTurn() %1 == 0 && state.getStarted()){ // Vits tur
                            startPlayer2Time();
                        }*/

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
                    } else if (obj instanceof Move) {
                        Move move = (Move) obj;
                        if(move.isLegalMove()) {
                            gameView.getBoardPanel().updateMoveValid(move.getSourceRow(), move.getSourceCol());
                        }

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

    public ObjectInputStream getOis() {
        return ois;
    }

    public void highlightMovementPattern(int srcY, int srcX){

        int YCord = srcY; //get y/x cordinate of piece
        int XCord = srcX;

        ChessPieceAbstract[][] gamemap = state.getCpa();
        ChessPiece cp = (ChessPiece) gamemap[YCord][XCord]; //get piece at cordinate
        int[][] moveset = cp.getMoveset(); //get moveset of piece
        BoardPanel.SquarePanel[][] squarePanel = gameView.getBoardPanel().getSquares(); //get GUI panel

        int YOffset = 0; //initialize offsets
        int XOffset = 0;

        try {
            //get offsets from moveset
            for(int row = 0; row < moveset.length; row ++){
                for (int col = 0; col < moveset[row].length; col++){
                    if(moveset[row][col] == 0){ //find number 0 in moveset array to get offsets
                        YOffset = row;
                        XOffset = col;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //y/x = cordinate - offset.
        boolean runOnce = false;
        for(int y = (YCord - YOffset); y < (YCord-YOffset+moveset.length); y++){
            for(int x = (XCord - XOffset); x < (XCord-XOffset+moveset.length);x++){
                try{
                    if(moveset[y-(YCord-YOffset)][x-(XCord-XOffset)] == 1 && gamemap[y][x] == null && cp.getChessPieceType() != ChessPieceType.ROOK && cp.getChessPieceType() != ChessPieceType.BISHOP && cp.getChessPieceType() != ChessPieceType.QUEEN){ //if there is a 1 in moveset then highlight tile
                        squarePanel[y][x].toggleHighlight();
                    }
                    if(moveset[y-(YCord-YOffset)][x-(XCord-XOffset)] == 1 && gamemap[YCord-1][XCord] !=null && cp.getChessPieceType() == ChessPieceType.PAWN && cp.getMoved() ==0){ // if pawn has 2 moves with enemy obstruction, dehighlight tile behind enemy peice
                        if(!runOnce){
                            runOnce = true;
                            squarePanel[y][x].toggleHighlight();
                        }
                    }
                    if(moveset[y-(YCord-YOffset)][x-(XCord-XOffset)] == 3 && cp.getChessPieceType() == ChessPieceType.PAWN && ((ChessPiece)gamemap[y][x]).getColor() != cp.getColor()){ //highlight if within pawn attack pattern
                        squarePanel[y][x].toggleHighlight();
                    }
                    if(moveset[y-(YCord-YOffset)][x-(XCord-XOffset)] == 1 && cp.getColor() != ((ChessPiece)gamemap[y][x]).getColor() && cp.getChessPieceType() != ChessPieceType.PAWN && cp.getChessPieceType() != ChessPieceType.ROOK && cp.getChessPieceType() != ChessPieceType.BISHOP && cp.getChessPieceType() != ChessPieceType.QUEEN){ //highlights position if chesspiece is not of same color
                        squarePanel[y][x].toggleHighlight();
                    }
                } catch (Exception e) {
                    continue;
                }

            }
        }
        //todo optimize rotation of patterns
        //rook highlight logic
        if(cp.getChessPieceType() == ChessPieceType.ROOK || cp.getChessPieceType() == ChessPieceType.QUEEN){
            //iterate all sides
            //first chess peice block rest of tiles from highlighting.

            //checks obstruction upside
            int yc = (YCord-1);
            int xc = (XCord+1);

            for(;yc >= 0; yc--){
                if(gamemap[yc][XCord] == null){
                    squarePanel[yc][XCord].toggleHighlight();
                }
                if(gamemap[yc][XCord] != null && ((ChessPiece)gamemap[yc][XCord]).getColor() != cp.getColor() ){
                    squarePanel[yc][XCord].toggleHighlight();
                    break;
                }
                else if(gamemap[yc][XCord] != null && ((ChessPiece)gamemap[yc][XCord]).getColor() == cp.getColor() ){
                    break;
                }
            }

            //checks obstruction right side
            for(;xc <= gamemap.length-1; xc++){
                if(gamemap[YCord][xc] == null){
                    squarePanel[YCord][xc].toggleHighlight();
                }
                if(gamemap[YCord][xc] != null && ((ChessPiece)gamemap[YCord][xc]).getColor() != cp.getColor() ){
                    squarePanel[YCord][xc].toggleHighlight();
                    break;
                }
                else if(gamemap[YCord][xc] != null && ((ChessPiece)gamemap[YCord][xc]).getColor() == cp.getColor() ){
                    break;
                }
            }

            //checks obstruction below
            yc = (YCord+1);
            for(;yc <= gamemap.length-1; yc++){
                if(gamemap[yc][XCord] == null){
                    squarePanel[yc][XCord].toggleHighlight();
                }
                if(gamemap[yc][XCord] != null && ((ChessPiece)gamemap[yc][XCord]).getColor() != cp.getColor() ){
                    squarePanel[yc][XCord].toggleHighlight();
                    break;
                }
                else if(gamemap[yc][XCord] != null && ((ChessPiece)gamemap[yc][XCord]).getColor() == cp.getColor() ){
                    break;
                }
            }

            //checks obstruction left
            xc = (XCord-1);
            for(;xc >= 0; xc--){
                if(gamemap[YCord][xc] == null){
                    squarePanel[YCord][xc].toggleHighlight();
                }
                if(gamemap[YCord][xc] != null && ((ChessPiece)gamemap[YCord][xc]).getColor() != cp.getColor() ){
                    squarePanel[YCord][xc].toggleHighlight();
                    break;
                }
                else if(gamemap[YCord][xc] != null && ((ChessPiece)gamemap[YCord][xc]).getColor() == cp.getColor() ){
                    break;
                }
            }
        }
        //iterate and highlight bishop path unless obstructed by foe or friend
        if(cp.getChessPieceType() == ChessPieceType.BISHOP || cp.getChessPieceType() == ChessPieceType.QUEEN){

            //top right
            int xc = (XCord+1);
            int yc = (YCord-1);

            for(; (yc >= 0) && (xc < gamemap[yc].length); yc--,xc++){
                if(gamemap[yc][xc] == null){
                    squarePanel[yc][xc].toggleHighlight();
                }
                if(gamemap[yc][xc] != null && ((ChessPiece)gamemap[yc][xc]).getColor() != cp.getColor() ){
                    squarePanel[yc][xc].toggleHighlight();
                    break;
                }
                else if(gamemap[yc][xc] != null && ((ChessPiece)gamemap[yc][xc]).getColor() == cp.getColor() ){
                    break;
                }
            }

            xc = (XCord+1);
            yc = (YCord+1);

            for(; (yc < gamemap.length) && xc < gamemap[yc].length; yc++,xc++){
                if(gamemap[yc][xc] == null){
                    squarePanel[yc][xc].toggleHighlight();
                }
                if(gamemap[yc][xc] != null && ((ChessPiece)gamemap[yc][xc]).getColor() != cp.getColor() ){
                    squarePanel[yc][xc].toggleHighlight();
                    break;
                }
                else if(gamemap[yc][xc] != null && ((ChessPiece)gamemap[yc][xc]).getColor() == cp.getColor() ){
                    break;
                }
            }

            xc = (XCord-1);
            yc = (YCord+1);

            for(; (yc < gamemap.length) && xc >= 0; yc++,xc--){
                if(gamemap[yc][xc] == null){
                    squarePanel[yc][xc].toggleHighlight();
                }
                if(gamemap[yc][xc] != null && ((ChessPiece)gamemap[yc][xc]).getColor() != cp.getColor() ){
                    squarePanel[yc][xc].toggleHighlight();
                    break;
                }
                else if(gamemap[yc][xc] != null && ((ChessPiece)gamemap[yc][xc]).getColor() == cp.getColor() ){
                    break;
                }
            }

            xc= (XCord-1);
            yc= (YCord-1);

            for(; (yc >= 0) && xc >= 0; yc--,xc--){
                if(gamemap[yc][xc] == null){
                    squarePanel[yc][xc].toggleHighlight();
                }
                if(gamemap[yc][xc] != null && ((ChessPiece)gamemap[yc][xc]).getColor() != cp.getColor() ){
                    squarePanel[yc][xc].toggleHighlight();
                    break;
                }
                else if(gamemap[yc][xc] != null && ((ChessPiece)gamemap[yc][xc]).getColor() == cp.getColor() ){
                    break;
                }
            }

        }
    }
}
