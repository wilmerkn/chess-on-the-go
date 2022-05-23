package server;

import server.controller.GameLogic;
import server.controller.LoginController;
import server.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class Server implements Runnable {

    private static final int PORT = 1234;

    private Hashtable<Player, ClientHandler> playerClientMap; // Testa concurrentHashmap
    private List<GameLogic> games = new ArrayList<>();
    private LoginController loginController = new LoginController();
    private ArrayList<String> playerList = new ArrayList<>();

    private HashMap<String, Player> usernamePlayerMap = new HashMap<>();

    private GameState state;


    public Server() {
        playerClientMap = new Hashtable<>();
        new Thread(this).start();
    }

    @Override
    public void run() {
        System.out.println("Server started");

        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                new ClientHandler(socket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private Player player;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                this.ois = new ObjectInputStream(socket.getInputStream());
                this.oos = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while(true) {
                    Object object = ois.readObject();

                    if(object instanceof LoginRequest) {
                        LoginRequest loginReq = (LoginRequest) ois.readObject();
                        boolean loginOk = loginController.checkLogin(loginReq.getUsername(), loginReq.getPassword());

                        if(loginOk) {
                            loginReq.setAccepted(true);
                            oos.writeObject(loginReq);

                            player = new Player(loginReq.getUsername()); // Ska hämtas från databas

                            usernamePlayerMap.put(player.getUsrName(), player);
                            playerClientMap.put(player, this);
                            playerList.add(player.getUsrName());

                            broadcastPlayers();
                        } else {
                            oos.reset();
                            oos.writeObject(loginReq);
                            oos.flush();
                        }
                    }

                    if (object instanceof ChallengeRequest challenge) {

                        if(!challenge.isAccepted()) {
                            for (Player player: playerClientMap.keySet()) {
                                if(player.getUsrName().equals(challenge.getReceiverUsername())) {
                                    oos.reset();
                                    playerClientMap.get(player).getOos().writeObject(challenge);
                                    oos.flush();
                                }
                            }
                        } else {
                            System.out.println("Challenge accepted");
                            GameLogic gl = new GameLogic();

                            state = new GameState();
                            state.setPlayer1(challenge.getSenderUsername());
                            state.setPlayer2(challenge.getReceiverUsername());
                            state.setCpa(initializeMap());

                            oos.reset();
                            playerClientMap.get(usernamePlayerMap.get(challenge.getReceiverUsername())).getOos().writeObject(state);
                            oos.flush();
                            oos.reset();
                            playerClientMap.get(usernamePlayerMap.get(challenge.getSenderUsername())).getOos().writeObject(state);
                            oos.flush();


                            System.out.println("Skriver state till clienter");

                            games.add(gl);
                        }

                    } else if(object instanceof Message) {
                        Message msg = (Message) object;
                        // Inte viktigt nu

                    } else if(object instanceof Move) {
                        //Move mv = (Move) object;
                        // If move legal make move
                        // If not return null
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                playerList.remove(player.getUsrName());
                playerClientMap.remove(player);
                usernamePlayerMap.remove(player.getUsrName());
                broadcastPlayers();
                disconnect();
            }
        }

        private void disconnect() {
            try {
                if (ois != null) ois.close();
                if (oos != null) oos.close();
                if (socket != null) socket.close();
                System.out.println("Client disconnected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public ObjectOutputStream getOos() {
            return oos;
        }

        private void broadcastPlayers() {

            for(ClientHandler client: playerClientMap.values()) {
                try {
                    System.out.println("broadcastPlayers " + playerList.size());
                    client.getOos().reset();
                    client.getOos().writeObject(playerList);
                    client.getOos().flush();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public ChessPieceAbstract[][] initializeMap() {
        //gameAudio.start();

        int mapDim = 8;
        HashMap<Integer, ChessPieceAbstract> chessPieces = new HashMap<>();
        ChessPieceAbstract[][] gamemap = new ChessPieceAbstract[mapDim][mapDim];

        gamemap[0][0] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.ROOK, "BR");
        gamemap[0][1] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.KNIGHT, "BN");
        gamemap[0][2] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.BISHOP, "BB");
        gamemap[0][3] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.QUEEN, "BQ");
        gamemap[0][4] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.KING, "BK");
        gamemap[0][5] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.BISHOP, "BB");
        gamemap[0][6] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.KNIGHT, "BN");
        gamemap[0][7] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.ROOK, "BR");

        //loads black Pawns
        int row = 1;
        for(int col = 0; col < mapDim; col++){
            gamemap[row][col] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.PAWN, "BP");
        }

        //load white Pawns
        row = 6;
        for(int col = 0; col < mapDim; col++){
            gamemap[row][col] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.PAWN, "WP");
        }

        //load white chesspieces
        gamemap[7][0] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.ROOK, "WR");
        gamemap[7][1] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.KNIGHT, "WN");
        gamemap[7][2] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.BISHOP, "WB");
        gamemap[7][3] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.QUEEN, "WQ");
        gamemap[7][4] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.KING, "WK");
        gamemap[7][5] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.BISHOP, "WB");
        gamemap[7][6] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.KNIGHT, "WN");
        gamemap[7][7] = new ChessPiece(ChessPieceColor.WHITE, ChessPieceType.ROOK, "WR");

        for(int r = 0; r < gamemap.length; r++){
            for(int c = 0; c < gamemap[r].length;c++){
                if(gamemap[r][c] != null){
                    ChessPiece chessPiece = (ChessPiece) gamemap[r][c];
                    ChessPieceType type = chessPiece.getChessPieceType();

                    if(type.equals(ChessPieceType.KING)){
                        chessPiece.setMoveset(new int[][]{
                                {1,1,1},
                                {1,0,1},
                                {1,1,1}});
                    }
                    else if(type.equals(ChessPieceType.QUEEN)){
                        chessPiece.setMoveset(new int[][]{
                                {1,2,2,2,2,2,2,1,2,2,2,2,2,2,1},
                                {2,1,2,2,2,2,2,1,2,2,2,2,2,1,2},
                                {2,2,1,2,2,2,2,1,2,2,2,2,1,2,2},
                                {2,2,2,1,2,2,2,1,2,2,2,1,2,2,2},
                                {2,2,2,2,1,2,2,1,2,2,1,2,2,2,2},
                                {2,2,2,2,2,1,2,1,2,1,2,2,2,2,2},
                                {2,2,2,2,2,2,1,1,1,2,2,2,2,2,2},
                                {1,1,1,1,1,1,1,0,1,1,1,1,1,1,1},
                                {2,2,2,2,2,2,1,1,1,2,2,2,2,2,2},
                                {2,2,2,2,2,1,2,1,2,1,2,2,2,2,2},
                                {2,2,2,2,1,2,2,1,2,2,1,2,2,2,2},
                                {2,2,2,1,2,2,2,1,2,2,2,1,2,2,2},
                                {2,2,1,2,2,2,2,1,2,2,2,2,1,2,2},
                                {2,1,2,2,2,2,2,1,2,2,2,2,2,1,2},
                                {1,2,2,2,2,2,2,1,2,2,2,2,2,2,1}});
                    }
                    else if(type.equals(ChessPieceType.KNIGHT)){
                        chessPiece.setMoveset(new int[][]{
                                {2,1,2,1,2},
                                {1,2,2,2,1},
                                {2,2,0,2,2},
                                {1,2,2,2,1},
                                {2,1,2,1,2}});
                    }
                    else if(type.equals(ChessPieceType.BISHOP)){
                        chessPiece.setMoveset(new int[][]{
                                {1,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
                                {2,1,2,2,2,2,2,2,2,2,2,2,2,1,2},
                                {2,2,1,2,2,2,2,2,2,2,2,2,1,2,2},
                                {2,2,2,1,2,2,2,2,2,2,2,1,2,2,2},
                                {2,2,2,2,1,2,2,2,2,2,1,2,2,2,2},
                                {2,2,2,2,2,1,2,2,2,1,2,2,2,2,2},
                                {2,2,2,2,2,2,1,2,1,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,0,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,1,2,1,2,2,2,2,2,2},
                                {2,2,2,2,2,1,2,2,2,1,2,2,2,2,2},
                                {2,2,2,2,1,2,2,2,2,2,1,2,2,2,2},
                                {2,2,2,1,2,2,2,2,2,2,2,1,2,2,2},
                                {2,2,1,2,2,2,2,2,2,2,2,2,1,2,2},
                                {2,1,2,2,2,2,2,2,2,2,2,2,2,1,2},
                                {1,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
                        });
                    }
                    else if(type.equals(ChessPieceType.ROOK)){
                        chessPiece.setMoveset(new int[][]{
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {1,1,1,1,1,1,1,0,1,1,1,1,1,1,1},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                                {2,2,2,2,2,2,2,1,2,2,2,2,2,2,2},
                        });
                    }
                    else if(type.equals(ChessPieceType.PAWN)){
                        chessPiece.setMoveset(new int[][]{
                                {2,1,2},
                                {3,1,3},
                                {2,0,2},
                        });
                    }
                }
            }
        }
        return gamemap;
    }

    public static void main(String[] args) {
        System.out.println("Server starting");
        new Server();
    }
}
