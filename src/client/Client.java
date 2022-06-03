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

/**
 * Client: Responsible for providing the user with a GUI, as well as sending information from the user, via server to other clients
 * @version 1.0
 * @author wilmerknutas, lucaskylberg, dannygazic, mirkosmiljanic
 */

public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 1234;

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private final LoginView loginView;
    private LobbyView lobbyView;
    private GameView gameView;

    private Player player;
    private String username;
    private String gameID;

    private Timer timer1;
    private int myTime;
    private Timer timer2;
    private int opponentTime;

    private GameState state;
    private String opponent;

    //initialize Client
    public Client() {
        LoginController loginController = new LoginController();
        loginView = new LoginView(loginController, this);
        connect();
        new ServerListener().start();
    }

    //let user login with password and username
    public void login(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        try {
            oos.writeObject(loginRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //send Challenge request
    public void challenge(String receiverUsername, int timeControl) {
        ChallengeRequest challenge = new ChallengeRequest(username, receiverUsername, timeControl);
        try {
            oos.writeObject(challenge);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //sends out a move from client
    public void sendMove(Move move) {
        move.setGameID(gameID);
        startOpponentTime();
        try {
            oos.reset();
            oos.writeObject(move);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //send current game state
    public void sendState(GameState state){
        try {
            oos.reset();
            oos.writeObject(state);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //connect to the server
    private void connect() {
        try {
            socket = new Socket(HOST, PORT);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //disconnect from the server
    public void disconnect() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Client disconnected");
            e.printStackTrace();
        }
    }

    //retrieves username
    public String getUsername() {
        return username;
    }

    //sends a chat message in game
    public void sendMessage(String messageText) {
        Message message = new Message(gameID, player, messageText);
        try {
            oos.reset();
            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //player requests to resign
    public void resign() {
        int answer = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to resign?",
                "Resignation",
                JOptionPane.YES_NO_OPTION
        );

        if(answer == 0) {
            ResignRequest request = new ResignRequest(gameID, username);
            try {
                oos.reset();
                oos.writeObject(request);
                oos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            gameView.disposeFrame();
        }
    }

    //client sends out request to offer a draw
    public void offerDraw() {
        int answer = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to offer draw?",
                "Offer draw",
                JOptionPane.YES_NO_OPTION
        );
        if(answer == 0) {
            DrawRequest request = new DrawRequest(gameID, username);
            try {
                oos.reset();
                oos.writeObject(request);
                oos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            gameView.disposeFrame();
        }
    }

    private class ServerListener extends Thread {
        //handles Server communication
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
                            player = request.getPlayer();
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
                            lobbyView.diposeFrame();
                        }
                    } else if (obj instanceof GameState) {

                        state = (GameState) obj;
                        gameID = state.getGameID();
                        if(state.getWinner().equals(username)){
                            JOptionPane.showMessageDialog(null, "You won on time!");
                            gameView.disposeFrame();
                        } else if (state.getWinner().equals(opponent)){
                            JOptionPane.showMessageDialog(null, "You lost on time! :) noob");
                            gameView.disposeFrame();
                        }
                        if(!state.getStarted()) {
                            gameView = new GameView(Client.this);
                            myTime = state.getTimeControl()*60;
                            opponentTime = state.getTimeControl()*60;
                            gameView.getBoardPanel().disableMouseListeners();


                            timer1 = new Timer(1000, new ActionListener() {
                                @Override
                                //checks if time has reached 0 to make a player win by time constraint
                                public void actionPerformed(ActionEvent e) {
                                    myTime--;
                                    gameView.setMyTime((myTime /60) + ":" + (myTime %60));
                                    if(myTime ==0){
                                        timer1.stop();
                                        state.setWinner(opponent);
                                        sendState(state);
                                        //should sent signal to server that one player wins, register result in DB in "stats" and then terminate game
                                        System.out.println("Player 2 wins on time");
                                    }
                                }
                            });
                            gameView.setMyTime(state.getTimeControl()+ ":00");
                            timer1.setInitialDelay(0);
                            timer2 = new Timer(1000, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    opponentTime--;
                                    gameView.setOpponentTime((opponentTime /60) + ":" + (opponentTime %60));
                                    if(opponentTime == 0){
                                        timer2.stop();
                                        System.out.println("Player 1 wins on time");
                                    }
                                }
                            });
                            gameView.setOpponentTime(state.getTimeControl()+ ":00");
                            timer2.setInitialDelay(0);
                            gameView.setMyName(username);
                            if(!username.equals(state.getPlayer1())) {
                                gameView.setOpponentName(state.getPlayer1());
                                opponent = state.getPlayer1();
                            } else if (!username.equals(state.getPlayer2())) {
                                gameView.setOpponentName(state.getPlayer2());
                                opponent = state.getPlayer2();
                            }
                        }
                        //turns off mouselisteners when it's not your turn, might change it so you cant register a moe when it isnt your turn, but you can check highlights
                        if(username.equals(state.getPlayer1()) && state.getPlayerTurn() % 1 == 0 && state.getPlayer1White() == 1) {
                            gameView.getBoardPanel().enableMouseListeners();
                        }
                        if(username.equals(state.getPlayer2()) && state.getPlayerTurn() % 1 != 0 && state.getPlayer1White() == 1){
                            gameView.getBoardPanel().enableMouseListeners();
                        }else if(username.equals(state.getPlayer1()) && state.getPlayerTurn() % 1 != 0 && state.getPlayer1White() == 0) {
                            gameView.getBoardPanel().enableMouseListeners();
                        }
                        else if(username.equals(state.getPlayer2()) && state.getPlayerTurn() % 1 == 0 && state.getPlayer1White() == 0){
                            gameView.getBoardPanel().enableMouseListeners();
                        }
                        //Timers for both boards
                        if (state.getStarted()){
                            if(state.getPlayer1White() == 1) {
                                if (state.getPlayerTurn() % 1 == 0) {
                                    if (username.equals(state.getPlayer1())) {
                                        startMyTime();
                                        gameView.getBoardPanel().enableMouseListeners();
                                    }
                                    if (username.equals(state.getPlayer2())) {
                                        startOpponentTime();
                                        gameView.getBoardPanel().disableMouseListeners();
                                    }
                                }
                                if (state.getPlayerTurn() % 1 != 0) {
                                    if (username.equals(state.getPlayer1())) {
                                        startOpponentTime();
                                        gameView.getBoardPanel().disableMouseListeners();
                                    }
                                    if (username.equals(state.getPlayer2())){
                                        startMyTime();
                                        gameView.getBoardPanel().enableMouseListeners();
                                    }
                                }
                            }
                            if(state.getPlayer1White() == 0) {
                                if (state.getPlayerTurn() % 1 == 0) {
                                    if (username.equals(state.getPlayer1())) {
                                        startOpponentTime();
                                        gameView.getBoardPanel().disableMouseListeners();
                                    }
                                    if (username.equals(state.getPlayer2())) {
                                        startMyTime();
                                        gameView.getBoardPanel().enableMouseListeners();
                                    }
                                }
                                if (state.getPlayerTurn() % 1 != 0) {
                                    if (username.equals(state.getPlayer1())) {
                                        startMyTime();
                                        gameView.getBoardPanel().enableMouseListeners();
                                    }
                                    if (username.equals(state.getPlayer2())){
                                        startOpponentTime();
                                        gameView.getBoardPanel().disableMouseListeners();
                                    }
                                }
                            }
                        }
                        drawMap(state.getCpa());
                    } else if (obj instanceof ChatLog) {
                        ChatLog chatLog = (ChatLog) obj;
                        gameView.getChatPanel().setChatPanelText(chatLog.getStringList());
                    } else if(obj instanceof PlayerList) {
                        PlayerList playerList = (PlayerList) obj;
                        ArrayList<String> tempList = playerList.getStringList();
                        tempList.remove(username);
                        lobbyView.getUserPanel().setOnlinePlayers(tempList);
                    } else if (obj instanceof Move) {
                        Move move = (Move) obj;
                        if(move.isLegalMove()) {
                            gameView.getBoardPanel().updateMoveValid(move.getSourceRow(), move.getSourceCol());
                        }
                    } else if (obj instanceof ResignRequest) {
                        JOptionPane.showMessageDialog(null, "You won by resignation!");
                        gameView.disposeFrame();
                    } else if (obj instanceof DrawRequest) {
                        DrawRequest request = (DrawRequest) obj;
                        if(request.isAccepted()) {
                            JOptionPane.showMessageDialog(null, "The game is a draw!");
                            gameView.disposeFrame();
                        } else {
                            int answer = JOptionPane.showConfirmDialog(
                                    null,
                                    "Accept draw offer?",
                                    "Draw offered",
                                    JOptionPane.YES_NO_OPTION
                            );
                            if(answer == 0) {
                                request.setAccepted();
                                try {
                                    oos.reset();
                                    oos.writeObject(request);
                                    oos.flush();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                JOptionPane.showMessageDialog(null, "The game is a draw!");
                                gameView.disposeFrame();
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                disconnect();
            }
        }
    }

    //Method used to draw up/update the Main game board
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

    //removes all sprites from board
    public void cleanBoard(){
        BoardPanel.SquarePanel[][] squarePanel = gameView.getBoardPanel().getSquares();
        for(int row = 0; row < squarePanel.length; row++){
            for(int col = 0; col < squarePanel[row].length; col++){
                squarePanel[row][col].removePiece();
            }
        }
    }

    //method to start a client
    public static void main(String[] args) {
        new Client();
    }

    //start players timer
    public void startMyTime(){
        timer2.stop();
        timer1.start();
    }

    //Start opponents timer
    public void startOpponentTime(){
        timer1.stop();
        timer2.start();
    }

    //hiighlights movement pattern for a clicked chesspiece
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
