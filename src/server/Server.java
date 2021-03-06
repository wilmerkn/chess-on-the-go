package server;

import client.gameview.MoveKing;
import client.gameview.PromotePawnWindow;
import server.controller.LoginController;
import server.model.*;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements Runnable {
    private static final int PORT = 1234;
    private final Hashtable<Player, ClientHandler> playerClientMap;
    private final LoginController loginController = new LoginController();
    private final PlayerList playerList = new PlayerList();
    private final HashMap<String, Player> usernamePlayerMap = new HashMap<>();
    private final HashMap<String, GameState> idGameStateMap = new HashMap<>();
    private ChessPieceAbstract[][] originalChessboard;
    private boolean blackKingWasMoved = false;
    private boolean whiteKingWasMoved = false;
    private ChessPiece rookForCastle;

    public Server() {
        playerClientMap = new Hashtable<>();
        new Thread(this).start();
        originalChessboard = initializeMap();
    }

    @Override
    public void run() {
        System.out.println("Server started");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private final Socket socket;
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

        public Player getPlayer() {
            return player;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Object object = ois.readObject();
                    if (object instanceof LoginRequest) {
                        LoginRequest loginReq = (LoginRequest) object;
                        boolean loginOk = loginController.checkLogin(loginReq.getUsername(), loginReq.getPassword());
                        if (loginOk) {
                            player = new Player(loginReq.getUsername()); // Ska h??mtas fr??n databas
                            loginReq.setAccepted(true, player);
                            oos.writeObject(loginReq);
                            usernamePlayerMap.put(player.getUsrName(), player);
                            playerClientMap.put(player, this);
                            playerList.add(player);
                            broadcastPlayers();
                        } else {
                            oos.reset();
                            oos.writeObject(loginReq);
                            oos.flush();
                        }
                    }else if (object instanceof ChallengeRequest challenge) {
                        if (!challenge.isAccepted()) {
                            for (Player player : playerClientMap.keySet()) {
                                if (player.getUsrName().equals(challenge.getReceiverUsername())) {
                                    playerClientMap.get(player).getOos().reset();
                                    playerClientMap.get(player).getOos().writeObject(challenge);
                                    playerClientMap.get(player).getOos().flush();
                                }
                            }
                        } else {
                            GameState state = new GameState();
                            idGameStateMap.put(state.getGameID(), state);
                            state.setPlayer1(challenge.getSenderUsername());
                            state.setPlayer2(challenge.getReceiverUsername());
                            state.setTimeControl(challenge.getTimeControl());
                            state.prepareTimers(challenge.getTimeControl());
                            state.setCpa(initializeMap());
                            if (state.getPlayer1White() == 1) {
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().reset();
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().writeObject(state);
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().flush();

                                inverseMapArray(state);
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().reset();
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().writeObject(state);
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().flush();
                            } else {
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().reset();
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().writeObject(state);
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().flush();

                                inverseMapArray(state);
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().reset();
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().writeObject(state);
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().flush();
                            }
                            inverseMapArray(state);
                            state.setStarted();
                            System.out.println("Skriver state till clienter");
                        }
                    } else if (object instanceof Message) {
                        Message msg = (Message) object;
                        String gameID = msg.getGameID();
                        GameState state = idGameStateMap.get(gameID);
                        state.getMessages().add(msg);

                        playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().reset();
                        playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().writeObject(state.getMessages());
                        playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().flush();

                        inverseMapArray(state);
                        playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().reset();
                        playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().writeObject(state.getMessages());
                        playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().flush();
                    } else if (object instanceof Move) {
                        Move move = (Move) object;
                        GameState state = idGameStateMap.get(move.getGameID());
                        boolean validMove;
                        if (state.getPlayer1White() == 1 && move.getUsername().equals(state.getPlayer1()) && state.getPlayerTurn() % 1 == 0) {
                            validMove = moveValid(move, state.getCpa());
                        } else if (state.getPlayer1White() != 1 && move.getUsername().equals(state.getPlayer2()) && state.getPlayerTurn() % 1 == 0) {
                            validMove = moveValid(move, state.getCpa());
                        } else {
                            inverseMapArray(state);
                            validMove = moveValid(move, state.getCpa());
                            inverseMapArray(state);
                        }
                        if (validMove) {
                            if (state.getPlayer1White() == 1 && (move.getUsername().equals(state.getPlayer1())) && state.getPlayerTurn() % 1 == 0) {
                                state.setCpa(update(move, state));
                            } else if (state.getPlayer1White() != 1 && (move.getUsername().equals(state.getPlayer2())) && state.getPlayerTurn() % 1 == 0) {
                                state.setCpa(update(move, state));
                            } else {
                                inverseMapArray(state);
                                state.setCpa(update(move, state));
                                inverseMapArray(state);
                            }
                            state.turnIncrement();
                            state.setStarted();
                            if (state.getPlayer1White() == 1) {
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().reset();
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().writeObject(state);
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().flush();

                                inverseMapArray(state);
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().reset();
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().writeObject(state);
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().flush();
                                inverseMapArray(state);
                            } else {
                                inverseMapArray(state);
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().reset();
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().writeObject(state);
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().flush();
                                inverseMapArray(state);

                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().reset();
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().writeObject(state);
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().flush();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Illegal move");
                            playerClientMap.get(usernamePlayerMap.get(move.getUsername())).getOos().reset();
                            playerClientMap.get(usernamePlayerMap.get(move.getUsername())).getOos().writeObject(move);
                            playerClientMap.get(usernamePlayerMap.get(move.getUsername())).getOos().flush();
                        }
                    } else if (object instanceof ResignRequest) {
                        ResignRequest request = (ResignRequest) object;
                        GameState state = idGameStateMap.get(request.getGameID());

                        if (request.getResigner().equals(state.getPlayer1())) {
                            playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().reset();
                            playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().writeObject(request);
                            playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().flush();
                        } else if (request.getResigner().equals(state.getPlayer2())) {
                            playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().reset();
                            playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().writeObject(request);
                            playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().flush();
                        }
                        //ToDo spara state i databas och ta bort game fr??n mappar.
                    } else if (object instanceof DrawRequest) {
                        DrawRequest request = (DrawRequest) object;
                        GameState state = idGameStateMap.get(request.getGameID());

                        if (request.isAccepted()) {
                            playerClientMap.get(usernamePlayerMap.get(request.getSender())).getOos().reset();
                            playerClientMap.get(usernamePlayerMap.get(request.getSender())).getOos().writeObject(request);
                            playerClientMap.get(usernamePlayerMap.get(request.getSender())).getOos().flush();
                        } else {
                            if (request.getSender().equals(state.getPlayer1())) {
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().reset();
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().writeObject(request);
                                playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().flush();
                            } else {
                                playerClientMap.get(usernamePlayerMap.get(request.getSender())).getOos().reset();
                                playerClientMap.get(usernamePlayerMap.get(request.getSender())).getOos().writeObject(request);
                                playerClientMap.get(usernamePlayerMap.get(request.getSender())).getOos().flush();
                            }
                        }
                        //ToDo spara state i databas och ta bort game fr??n mappar.
                    } else if (object instanceof GameState) {
                        GameState state = (GameState) object;
                        playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().reset();
                        playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().writeObject(state);
                        playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().flush();

                        playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().reset();
                        playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().writeObject(state);
                        playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().flush();
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                playerList.remove(player);
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
            for (ClientHandler client : playerClientMap.values()) {
                try {
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
        for (int col = 0; col < mapDim; col++) {
            gamemap[row][col] = new ChessPiece(ChessPieceColor.BLACK, ChessPieceType.PAWN, "BP");
        }

        //load white Pawns
        row = 6;
        for (int col = 0; col < mapDim; col++) {
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

        for (int r = 0; r < gamemap.length; r++) {
            for (int c = 0; c < gamemap[r].length; c++) {
                if (gamemap[r][c] != null) {
                    ChessPiece chessPiece = (ChessPiece) gamemap[r][c];
                    ChessPieceType type = chessPiece.getChessPieceType();

                    if (type.equals(ChessPieceType.KING)) {
                        chessPiece.setMoveset(new int[][]{
                                {1, 1, 1},
                                {1, 0, 1},
                                {1, 1, 1}});
                    } else if (type.equals(ChessPieceType.QUEEN)) {
                        chessPiece.setMoveset(new int[][]{
                                {1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1},
                                {2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 1, 2},
                                {2, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 2},
                                {2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2},
                                {2, 2, 2, 2, 1, 2, 2, 1, 2, 2, 1, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 2, 2, 2, 2},
                                {1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1},
                                {2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 1, 2, 2, 1, 2, 2, 1, 2, 2, 2, 2},
                                {2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2},
                                {2, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 2},
                                {2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 1, 2},
                                {1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1}});
                    } else if (type.equals(ChessPieceType.KNIGHT)) {
                        chessPiece.setMoveset(new int[][]{
                                {2, 1, 2, 1, 2},
                                {1, 2, 2, 2, 1},
                                {2, 2, 0, 2, 2},
                                {1, 2, 2, 2, 1},
                                {2, 1, 2, 1, 2}});
                    } else if (type.equals(ChessPieceType.BISHOP)) {
                        chessPiece.setMoveset(new int[][]{
                                {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                                {2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2},
                                {2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2},
                                {2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2},
                                {2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2},
                                {2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2},
                                {2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2},
                                {2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2},
                                {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                        });
                    } else if (type.equals(ChessPieceType.ROOK)) {
                        chessPiece.setMoveset(new int[][]{
                                {2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2},
                                {1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1},
                                {2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2},
                                {2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2},
                        });
                    } else if (type.equals(ChessPieceType.PAWN)) {
                        chessPiece.setMoveset(new int[][]{
                                {2, 1, 2},
                                {3, 1, 3},
                                {2, 0, 2},
                        });
                    }
                }
            }
        }
        return gamemap;
    }

    //method returns the rook used in the castle method
    private ChessPiece getTheRookForCastle(ChessPieceColor kingColor, ChessPieceAbstract[][] gamemap, ChessPiece theKing){
        for(int i = 0;i < 8; i ++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece chessPiece = (ChessPiece) gamemap[i][j];
                if (chessPiece != null && chessPiece.getColor() == kingColor && chessPiece.getChessPieceType() == ChessPieceType.ROOK) {
                    if (getLocationX(chessPiece, gamemap) == getLocationX(theKing, gamemap) & Math.abs(getLocationY(theKing, gamemap) - getLocationY(chessPiece, gamemap)) == 3) {
                        return chessPiece;
                    }
                }
            }
        }
        return null;
    }

    public boolean moveValid(Move move, ChessPieceAbstract[][] gamemap) {
        int sourceRow = move.getSourceRow();
        int sourceCol = move.getSourceCol();
        int targetRow = move.getTargetRow();
        int targetCol = move.getTargetCol();

        ChessPiece cp = (ChessPiece) gamemap[sourceRow][sourceCol];
        int[][] moveset = cp.getMoveset();

        int movesetOffsetY = -1;
        int movesetOffsetX = -1;

        try {
            //get offsets from moveset
            for (int row = 0; row < moveset.length; row++) {
                for (int col = 0; col < moveset[row].length; col++) {
                    if (moveset[row][col] == 0) { //find number 0 in moveset array to get offsets
                        movesetOffsetY = row;
                        movesetOffsetX = col;
                        break;
                    }
                }
            }

        } catch (Exception e) {

        }

        int yTrOffset = (sourceRow - targetRow);
        int xTrOffset = (sourceCol - targetCol);

        boolean samespot = false;
        boolean withinMoveset = false;
        boolean friendlyObstruction = false;
        boolean pawnobstruct = false;
        boolean pawnattacks = false;
        boolean pawnTwoMoveObstruct = false;
        boolean rookobstruction = false;
        boolean bishopobstruction = false;
        boolean queenObstruction = false;
        boolean canKingCastle = true;


        canKingCastle = kingCanCastle(cp, targetRow, targetCol, gamemap);
        samespot = sameCpspot(sourceRow, sourceCol, targetRow, targetCol);
        withinMoveset = moveWithinCpMoveset(movesetOffsetY, movesetOffsetX, yTrOffset, xTrOffset, moveset, cp, gamemap);
        friendlyObstruction = friendlyCpObstruction(targetRow, targetCol, gamemap, cp);
        pawnattacks = pawnAttacks(targetRow, targetCol, movesetOffsetY, movesetOffsetX, yTrOffset, xTrOffset, moveset, cp, gamemap);
        pawnobstruct = pawnObstruction(targetRow, targetCol, gamemap, cp);
        pawnTwoMoveObstruct = pawn2Moves(targetRow, sourceRow, sourceCol, gamemap, cp);
        rookobstruction = rookObstruct(sourceRow, sourceCol, targetRow, targetCol, gamemap, cp);
        bishopobstruction = bishopObstruct(sourceRow, sourceCol, targetRow, targetCol, gamemap, cp);
        queenObstruction = queenObstruct(sourceRow, sourceCol, targetRow, targetCol, gamemap, cp);


        System.out.println("Samespot error: " + samespot);
        System.out.println("Withinmoveset error: " + withinMoveset);
        System.out.println("FriendlyObstruction error: " + friendlyObstruction);
        System.out.println("pawnObstruction error: " + pawnobstruct);
        System.out.println("PawnAttack: " + pawnattacks);
        System.out.println("PawnTwoMovesObstruct error: " + pawnTwoMoveObstruct);
        System.out.println("RookObstruction error: " + rookobstruction);
        System.out.println("BishopObstruction error: " + bishopobstruction);
        System.out.println("queenObstruction error: " + queenObstruction);


        //if errorchecks are negative make move valid
            return (!samespot && !withinMoveset && !friendlyObstruction && !pawnobstruct && !pawnTwoMoveObstruct && !rookobstruction && !bishopobstruction) || (pawnattacks) || ((!samespot && !withinMoveset && !friendlyObstruction && !pawnobstruct && !pawnTwoMoveObstruct) && !queenObstruction
|| canKingCastle);

    }

    //did user click the same spot
    public boolean sameCpspot(int sR, int sC, int tR, int tC) {
        return sR == tR && sC == tC;
    }

    //does peice move within moveset
    public boolean moveWithinCpMoveset(int movesetOffsetY, int movesetOffsetX, int yTrOffset, int xTrOffset, int[][] moveset, ChessPiece cp, ChessPieceAbstract[][] gamemap) {
        try {
            return moveset[movesetOffsetY - yTrOffset][movesetOffsetX - xTrOffset] != 1;
        } catch (Exception e) {

        }
        return true;
    }

    //checks if obstruction is of same chesspiece color
    public boolean friendlyCpObstruction(int targetRow, int targetCol, ChessPieceAbstract[][] gamemap, ChessPiece cp) {
        if (gamemap[targetRow][targetCol] != null) {
            ChessPieceColor obstructionColor = ((ChessPiece) gamemap[targetRow][targetCol]).getColor();
            return cp.getColor() == obstructionColor;
        }
        return false;
    }

    //is pawn obstruct by enemy
    public boolean pawnObstruction(int targetRow, int targetCol, ChessPieceAbstract[][] gamemap, ChessPiece cp) {
        if (cp.getChessPieceType() == ChessPieceType.PAWN && gamemap[targetRow][targetCol] != null) {
            return ((ChessPiece) gamemap[targetRow][targetCol]).getColor() != cp.getColor();
        }
        return false;
    }

    //checks if pawn can move 2 steps ahead if no obstruction is in the way
    public boolean pawn2Moves(int targetRow, int sourceRow, int sourceCol, ChessPieceAbstract[][] gamemap, ChessPiece cp) {
        return cp.getChessPieceType() == ChessPieceType.PAWN && cp.getMoved() == 0 && gamemap[sourceRow - 1][sourceCol] != null && targetRow == sourceRow - 2;
    }

    //checks if pawn can attack with attack pattern
    public boolean pawnAttacks(int targetRow, int targetCol, int movesetOffsetY, int movesetOffsetX, int yTrOffset, int xTrOffset, int[][] moveset, ChessPiece cp, ChessPieceAbstract[][] gamemap) {
        try {
            return cp.getChessPieceType() == ChessPieceType.PAWN && gamemap[targetRow][targetCol] != null && ((ChessPiece) gamemap[targetRow][targetCol]).getColor() != cp.getColor() && moveset[movesetOffsetY - yTrOffset][movesetOffsetX - xTrOffset] == 3;
        } catch (Exception e) {
            return false;
        }
    }

    //highlight Rook path until obstruction
    public boolean rookObstruct(int sourceRow, int sourceCol, int targetRow, int targetCol, ChessPieceAbstract[][] gamemap, ChessPiece cp) {
        if (cp.getChessPieceType() == ChessPieceType.ROOK || cp.getChessPieceType() == ChessPieceType.QUEEN) {
            if (sourceCol < targetCol) {
                for (int x = (sourceCol + 1); x < gamemap[sourceRow].length - 1; x++) {
                    if (gamemap[sourceRow][x] != null) {
                        if (targetCol > x) {
                            return true;
                        }
                    }
                }
            }
            if (sourceRow < targetRow) {
                for (int y = (sourceRow + 1); y < gamemap.length - 1; y++) {
                    if (gamemap[y][sourceCol] != null) {
                        if (targetRow > y) {
                            return true;
                        }
                    }
                }
            }
            if (sourceCol > targetCol) {
                for (int x = (sourceCol - 1); x >= 0; x--) {
                    if (gamemap[sourceRow][x] != null) {
                        if (targetCol < x) {
                            return true;
                        }
                    }
                }
            }
            if (sourceRow > targetRow) {
                for (int y = (sourceRow - 1); y >= 0; y--) {
                    if (gamemap[y][sourceCol] != null) {
                        if (targetRow < y) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //method for bishop. checks if target position is obstruct by another peice.
    public boolean bishopObstruct(int sourceRow, int sourceCol, int targetRow, int targetCol, ChessPieceAbstract[][] gamemap, ChessPiece cp) {

        int yc;
        int xc;

        if (cp.getChessPieceType() == ChessPieceType.BISHOP || cp.getChessPieceType() == ChessPieceType.QUEEN) {
            if ((sourceRow > targetRow) && (sourceCol < targetCol)) {
                //check top right
                yc = (sourceRow - 1);
                xc = (sourceCol + 1);
                for (; (yc >= 0) && (xc < gamemap[yc].length); yc--, xc++) {
                    if (gamemap[yc][xc] != null) {
                        if ((targetRow < yc) && (targetCol > xc)) {
                            return true;
                        }
                    }
                }
            }
            //check bottom right
            yc = (sourceRow + 1);
            xc = (sourceCol + 1);
            if ((sourceRow < targetRow) && (sourceCol < targetCol)) {
                for (; (yc < gamemap.length) && (xc < gamemap[yc].length); yc++, xc++) {
                    if (gamemap[yc][xc] != null) {
                        if ((targetRow > yc) && (targetCol > xc)) {
                            return true;
                        }
                    }
                }
            }
            //check bottom left
            yc = (sourceRow + 1);
            xc = (sourceCol - 1);
            for (; (yc < gamemap.length) && (xc >= 0); yc++, xc--) {
                if (gamemap[yc][xc] != null) {
                    if ((targetRow > yc) && (targetCol < xc)) {
                        return true;
                    }
                }
            }
            //check top left
            yc = (sourceRow - 1);
            xc = (sourceCol - 1);
            for (; (yc >= 0) && (xc >= 0); yc--, xc--) {
                if (gamemap[yc][xc] != null) {
                    if ((targetRow < yc) && (targetCol < xc)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean queenObstruct(int sourceRow, int sourceCol, int targetRow, int targetCol, ChessPieceAbstract[][] gamemap, ChessPiece cp) {

        boolean diagonal = false;
        boolean bishop = false;
        boolean rook = false;

        bishop = bishopObstruction(sourceRow, sourceCol, targetRow, targetCol, gamemap, cp); //use bishop and rook patterns for queen obstruction
        rook = rookObstruction(sourceRow, sourceCol, targetRow, targetCol, gamemap, cp);

        //checks if source cordinate and target cordinate is diagonal
        if ((targetRow - sourceRow == targetCol - sourceCol) || (targetRow - sourceRow == sourceCol - targetCol)) {
            diagonal = true;
        }
        // System.out.println("is diagonal: " + diagonal);

        //check what method to use
        if (diagonal && bishop) {
            return true;
        } else return !diagonal && rook;
    }

    //method thar returns whiteKingWasMoved or blackKingWasMoved
    private boolean kingCastledBoolean(ChessPieceColor color){
        if(color == ChessPieceColor.WHITE){
            return whiteKingWasMoved;
        }else if(color == ChessPieceColor.BLACK){
            return blackKingWasMoved;
        }
        return false;
    }

    //castle the king
    private void doCastleMove(int rookMove, int kingMove, int sourceRow, int sourceCol, ChessPieceAbstract[][] gamemap, int targetRow, int targetCol, ChessPiece tempChesspiece){
        boolean kingMoved = kingCastledBoolean(tempChesspiece.getColor());
        ChessPiece rook = null;
        rook = (ChessPiece) gamemap[targetRow][targetCol];
        gamemap[sourceRow][sourceCol + kingMove] = tempChesspiece;
        gamemap[getLocationX(rook, gamemap)][getLocationY(rook, gamemap) + rookMove] = rook;
        gamemap[targetRow][targetCol] = null;
        gamemap[sourceRow][sourceCol] = null;
        rookForCastle = null;
        kingMoved = true;
    }

    public ChessPieceAbstract[][] update(Move move, GameState state) { //called when chesspiece is moved

        ChessPieceAbstract[][] gamemap = state.getCpa();

        int sourceRow = move.getSourceRow();
        int sourceCol = move.getSourceCol();
        int targetRow = move.getTargetRow();
        int targetCol = move.getTargetCol();

        ChessPiece tempChesspiece = null;
        tempChesspiece = ((ChessPiece) gamemap[sourceRow][sourceCol]);

        if(tempChesspiece.getChessPieceType() == ChessPieceType.KING & tempChesspiece.getColor() == ChessPieceColor.BLACK && rookForCastle != null & gamemap[targetRow][targetCol] == rookForCastle){
            doCastleMove(2, -2, sourceRow, sourceCol, gamemap, targetRow, targetCol, tempChesspiece);

        }else if(tempChesspiece.getChessPieceType() == ChessPieceType.KING & tempChesspiece.getColor() == ChessPieceColor.WHITE && rookForCastle != null & gamemap[targetRow][targetCol] == rookForCastle){
            doCastleMove(-2, 2, sourceRow, sourceCol, gamemap, targetRow, targetCol, tempChesspiece);
        }

        else {
            if(tempChesspiece.getColor() == ChessPieceColor.BLACK & tempChesspiece.getChessPieceType() == ChessPieceType.KING || tempChesspiece.getColor() == ChessPieceColor.BLACK & tempChesspiece.getChessPieceType() == ChessPieceType.ROOK){
                blackKingWasMoved = true;

            }else if(tempChesspiece.getColor() == ChessPieceColor.WHITE & tempChesspiece.getChessPieceType() == ChessPieceType.KING || tempChesspiece.getColor() == ChessPieceColor.WHITE & tempChesspiece.getChessPieceType() == ChessPieceType.ROOK){
                whiteKingWasMoved = true;
            }

            gamemap[targetRow][targetCol] = tempChesspiece;
            gamemap[sourceRow][sourceCol] = null;
        }

        if (tempChesspiece.getChessPieceType() == ChessPieceType.PAWN && tempChesspiece.getMoved() == 0) {
            tempChesspiece.setMoved(1);
            tempChesspiece.setMoveset(new int[][]{
                    {3, 1, 3},
                    {2, 0, 2},
                    {2, 2, 2}
            });
        }
        if (tempChesspiece != null && tempChesspiece.getChessPieceType() == ChessPieceType.PAWN) {
            if (getLocationX(tempChesspiece, gamemap) == 0) {
                PromotePawnWindow pawnWindow = new PromotePawnWindow();
                pawnWindow.chooseChesspiece(this, tempChesspiece.getColor());
                gamemap[targetRow][targetCol] = pawnWindow.getChessPiece();
            }
        }

        check(ChessPieceColor.BLACK, ChessPieceColor.WHITE, state, gamemap);
        check(ChessPieceColor.WHITE, ChessPieceColor.BLACK, state, gamemap);

        displayMap(gamemap);
        return gamemap;
    }

    //this method checks if the king can be castled
    public boolean kingCanCastle(ChessPiece cp, int tr, int tc, ChessPieceAbstract[][] gamemap){
        if(blackKingWasMoved!=true) {
            if (cp.getChessPieceType() == ChessPieceType.KING & cp.getColor() == ChessPieceColor.BLACK) {
                ChessPiece rook = getTheRookForCastle(cp.getColor(), gamemap, cp);
                if ((tr == getLocationX(rook, gamemap) & tc == getLocationY(rook, gamemap))) {
                    rookForCastle = rook;
                    return true;
                }
            }
        }
        if(whiteKingWasMoved!=true) {
         if (cp.getChessPieceType() == ChessPieceType.KING & cp.getColor() == ChessPieceColor.WHITE) {
                ChessPiece rook = getTheRookForCastle(cp.getColor(), gamemap, cp);
                if ((tr == getLocationX(rook, gamemap) & tc == getLocationY(rook, gamemap))) {
                    rookForCastle = rook;
                    return true;
                }
            }
        }
        return false;
    }

    public ChessPiece getChesspiece(ChessPieceColor color, ChessPieceType type){
        ChessPiece theChessPiece = null;
        for(int r = 0; r <8; r++){
            for(int c = 0; c < 8; c++){
                ChessPiece cp = (ChessPiece) originalChessboard[r][c];
                if(cp!=null && cp.getChessPieceType() == type && cp.getColor() == color){
                    theChessPiece = cp;
                }
            }
        }
        return theChessPiece;
    }

    //check for check and checkmate
    public void check(ChessPieceColor friendlyColor, ChessPieceColor enemyColor, GameState state,ChessPieceAbstract[][] gamemap){
        ChessPiece king = getTheKing(friendlyColor, gamemap);

        if (isCheck(king, enemyColor, gamemap)) {

            MoveKing moveKing = new MoveKing();

             if(state.getPlayer1White()!=1 && state.getPlayerTurn() %1 == 0){
                if ((isCheckmate(king, ChessPieceColor.WHITE, ChessPieceColor.BLACK, gamemap) == true )) {
                    CheckmateWindow checkmateWindow = new CheckmateWindow(enemyColor.toString());
                }
            }
             else{
                 if ((isCheckmate(king, ChessPieceColor.BLACK, ChessPieceColor.WHITE, gamemap) == true )) {
                     CheckmateWindow checkmateWindow = new CheckmateWindow(enemyColor.toString());
                 }
             }
        }
    }

    //method used to check if a chess piece can block a check
    private boolean noPieceCanBlockCheck(ChessPiece king, ChessPiece enemy, ChessPieceColor friendlyColor, ChessPieceColor enemyColor, ChessPieceAbstract[][] board){
        ArrayList<Integer> xCoordinates = new ArrayList<>();
        ArrayList<Integer> yCoordinates = new ArrayList<>();
        int piecesBlockingCheck = 0;

        //get all the possible positions the enemy chess pieces can go to
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                Move move = new Move(getLocationX( enemy,board), getLocationY(enemy, board), i, j);
                if(moveValid(move, board)){
                    //check if the chess piece will be attacking the king if moved to the new position
                    if (checkMove(i, j, enemy, getLocationX(king, board), getLocationY(king, board), king, board)) {
                        xCoordinates.add(i);
                        yCoordinates.add(j);
                    }
                }
            }
        }

        //see if any of the friendly chess pieces can block the check
        for(int xy = 0; xy < xCoordinates.size(); xy++) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    ChessPiece cp = (ChessPiece) board[i][j];
                    if (cp != null && cp.getColor() == friendlyColor && cp.getChessPieceType() != ChessPieceType.KING) {
                        Move move = new Move(getLocationX(cp, board), getLocationY(cp, board), xCoordinates.get(xy), yCoordinates.get(xy));
                        if(moveValid(move, board)){
                            piecesBlockingCheck++;
                        }
                    }
                }
            }
        }

        //if there is at least one chess piece that can block check, return false
        if(piecesBlockingCheck > 0){
            return false;
        }
        else {
            return true;
        }
    }

    //method that checks for checkmate
    private boolean isCheckmate(ChessPiece theKing, ChessPieceColor enemyColor, ChessPieceColor friendlyColor, ChessPieceAbstract[][] board ) {
        ArrayList<Integer> xKing = new ArrayList<>();
        ArrayList<Integer> yKing = new ArrayList<>();
        ArrayList<String> checkmateCoordinates = new ArrayList<String>();

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                ChessPiece cp = (ChessPiece) board[i][j];
                if(board[i][j] == null || cp.getColor().equals(enemyColor)){
                    //check all the places the king can move to
                    Move kingMove = new Move(getLocationX(theKing, board), getLocationY(theKing, board), i, j);
                    if(moveValid(kingMove, board)){
                        //save the coordinates of moves that are either empty or that have the opposite player's chess pieces on it
                        xKing.add(i);
                        yKing.add(j);
                    }
                }
            }
        }

        //add coordinates of places where the king would be under attack to the list of checkmateCoordinates
        checkmateCoordinates.addAll(deadlyMoves(xKing, yKing, board, theKing, enemyColor));
        //add coordinates of places where the king can attack the enemy chess piece, but would still be under check to the list of checkmateCoordinates
        checkmateCoordinates.addAll(protectedPieces(xKing, yKing, board, friendlyColor, enemyColor));

        ArrayList checkmateCoordinatesSorted = deleteDuplicates(checkmateCoordinates);//sort the checkmateCoordinates list by removing duplicates form the list

        //return true if the number of possible moves is equal to the number of moves that would result in the kings death and if no chess piece is blocking the check
        if(checkmateCoordinatesSorted.size() == xKing.size()) {
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){
                    ChessPiece cp = (ChessPiece) board[i][j];
                    if(cp!=null && cp.getColor() == enemyColor) {
                        Move move = new Move(getLocationX(cp, board), getLocationY(cp, board), getLocationX(theKing, board), getLocationY(theKing, board));
                        if(moveValid(move, board) && noPieceCanBlockCheck(theKing, cp, friendlyColor, enemyColor, board) == true) {
                            return true;
                        }
                        else{
                            continue;
                        }
                    }
                }
            }
        }

        return false;
    }

    //this method returns an arraylist of places on the chess board where the king would still be under attack if moved there
    private ArrayList<String> deadlyMoves(ArrayList<Integer> xKing, ArrayList<Integer> yKing, ChessPieceAbstract[][] board, ChessPiece theKing, ChessPieceColor enemyColor){
        ArrayList<String> list = new ArrayList<>();
        for (int xy = 0; xy < xKing.size(); xy++) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    ChessPiece cp = (ChessPiece) board[i][j];
                    if (cp != null && cp.getColor().equals(enemyColor)) {
                        //see if a chess piece could attack the king
                        if (checkMove(getLocationX(cp, board), getLocationY(cp, board), cp, xKing.get(xy), yKing.get(xy), theKing, board) == true) {
                            list.add(xKing.get(xy)+", "+yKing.get(xy));
                        }
                    }
                }
            }
        }
        return list;
    }

    //see if the chess piece that is attacking the king protected by another chess piece and if they are, add them to the list
    private ArrayList<String> protectedPieces(ArrayList<Integer> xKing, ArrayList<Integer> yKing, ChessPieceAbstract[][] board, ChessPieceColor friendlyColor, ChessPieceColor enemyColor){
        ArrayList<String> list = new ArrayList<>();

        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int xy = 0; xy < xKing.size(); xy++) {
                    ChessPiece originalCp = (ChessPiece) board[xKing.get(xy)][yKing.get(xy)];
                    if (originalCp != null) {
                        ChessPiece chessPiece = (ChessPiece) board[i][j];
                        if (chessPiece != null && chessPiece.getColor() == enemyColor && chessPiece != originalCp) {
                            ChessPiece fakeChessPiece = getTheKing(friendlyColor, board);
                            if (checkMove(getLocationX(chessPiece, board), getLocationY(chessPiece, board), chessPiece, getLocationX(originalCp, board), getLocationY(originalCp, board), fakeChessPiece, board) == true) {
                                list.add(xKing.get(xy)+", " + yKing.get(xy));
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    //this method removes duplicates from an arraylist
    private <T> ArrayList<T> deleteDuplicates(ArrayList<T> arrList) {
        Set<T> newSet = new LinkedHashSet<>();
        newSet.addAll(arrList);
        arrList.clear();
        arrList.addAll(newSet);
        return arrList;
    }

    //method that checks if the king is attacked
    public boolean isCheck(ChessPiece theKing, ChessPieceColor enemyColor, ChessPieceAbstract[][]mapp) {
        int kingX = getLocationX(theKing, mapp);
        int kingY = getLocationY(theKing, mapp);

        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                try {
                    ChessPiece chessPiece = (ChessPiece)mapp[i][j];
                    if (chessPiece != null && chessPiece.getColor()==enemyColor) {
                        Move chesspieceMove = new Move(i, j, kingX, kingY);
                        boolean valid = moveValid(chesspieceMove, mapp);
                        if (valid) {
                            return true;
                        }
                    }
                }catch (ArrayIndexOutOfBoundsException e){

                }
            }
        }
        return false;
    }

    //method puts two chess pieces on a chess board and then check if the first chess piece can move on the second chess pieces location
    public boolean checkMove(int chessPiece1Row, int chessPiece1Col, ChessPiece chessPiece1, int chessPiece2Row, int chessPiece2Col, ChessPiece chessPiece2, ChessPieceAbstract[][] board) {
        ChessPieceAbstract[][] mapp = new ChessPieceAbstract[8][8];
        //create a new ChessPieceAbstract[][] map and copy the elements of the current chess board
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                mapp[i][j] = board[i][j];
            }
        }
        //put the two chess pieces on the fake chess board
        mapp[chessPiece1Row][chessPiece1Col] = chessPiece1;
        mapp[chessPiece2Row][chessPiece2Col] = chessPiece2;

        //see if the first chess piece can attack the second chess piece
        Move move = new Move(chessPiece1Row, chessPiece1Col, chessPiece2Row, chessPiece2Col);

        if(moveValid(move, mapp)){
            return true;
        }
        return false;
    }

    //method that returns the row of a chess piece
    public int getLocationX(ChessPiece chessPiece, ChessPieceAbstract[][] map) {
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(map[i][j] == chessPiece){
                    return i;
                }
            }
        }
        return -1;
    }

    //method that return a column of a chess piece
    public int getLocationY(ChessPiece chessPiece, ChessPieceAbstract[][] map) {
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(map[i][j] == chessPiece){
                    return j;
                }
            }
        }
        return -1;
    }

    //method that returns a king
    public ChessPiece getTheKing(ChessPieceColor color, ChessPieceAbstract[][] mapp) {
        ChessPiece piece = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                piece = (ChessPiece) mapp[i][j];
                if (piece == null) {
                    continue;
                }
                if (piece.getChessPieceType().equals(ChessPieceType.KING) && piece.getColor().equals(color)) {
                    return piece;
                }
            }
        }
        return null;
    }

    public void displayMap(ChessPieceAbstract[][] cpa){
        System.out.println("-----------------------------------------------------------");
        for(int row = 0; row < 8; row++){
            for(int col = 0; col < 8; col++){
                System.out.print("[" + cpa[row][col]+ "]" + " ");
            }
            System.out.println();
        }
        System.out.println("-----------------------------------------------------------");
    }

    //highlight Rook path until obstruction
    public boolean rookObstruction(int sourceRow, int sourceCol, int targetRow, int targetCol, ChessPieceAbstract[][] gamemap, ChessPiece cp){
        if(cp.getChessPieceType() == ChessPieceType.ROOK || cp.getChessPieceType() == ChessPieceType.QUEEN){
            if(sourceCol < targetCol){
                for (int x = (sourceCol + 1); x < gamemap[sourceRow].length-1; x++ ){
                    if(gamemap[sourceRow][x] != null){
                        if(targetCol > x){
                            return true;
                        }
                    }
                }
            }
            if (sourceRow < targetRow){
                for (int y = (sourceRow + 1); y < gamemap.length-1; y++ ){
                    if(gamemap[y][sourceCol] != null){
                        if(targetRow > y){
                            return true;
                        }
                    }
                }
            }
            if(sourceCol > targetCol){
                for (int x = (sourceCol - 1); x >= 0; x--){
                    if(gamemap[sourceRow][x] != null){
                        if(targetCol < x){
                            return true;
                        }
                    }
                }
            }
            if(sourceRow > targetRow){
                for (int y = (sourceRow - 1); y >= 0; y--){
                    if(gamemap[y][sourceCol] != null){
                        if(targetRow < y){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //method for bishop. checks if target position is obstruct by another peice.
    public boolean bishopObstruction(int sourceRow,int sourceCol,int targetRow,int targetCol,ChessPieceAbstract[][] gamemap, ChessPiece cp){
        int yc;
        int xc;

        if(cp.getChessPieceType() == ChessPieceType.BISHOP || cp.getChessPieceType() == ChessPieceType.QUEEN){
            if( (sourceRow > targetRow) && (sourceCol < targetCol) ){
                //check top right
                yc = (sourceRow-1);
                xc = (sourceCol+1);
                for(; (yc >= 0) && (xc < gamemap[yc].length); yc--,xc++){
                    if(gamemap[yc][xc] != null){
                        if( (targetRow < yc) && (targetCol > xc) ){
                            return true;
                        }
                    }
                }
            }
            //check bottom right
            yc = (sourceRow+1);
            xc = (sourceCol+1);
            if( (sourceRow < targetRow) && (sourceCol < targetCol) ){
                for(; (yc < gamemap.length) && (xc < gamemap[yc].length); yc++,xc++){
                    if(gamemap[yc][xc]!= null){
                        if( (targetRow > yc) && (targetCol > xc) ){
                            return true;
                        }
                    }
                }
            }
            //check bottom left
            yc =(sourceRow+1);
            xc =(sourceCol-1);
            for(; (yc < gamemap.length) && (xc >=0); yc++,xc--){
                if(gamemap[yc][xc]!= null){
                    if( (targetRow > yc) && (targetCol < xc) ){
                        return true;
                    }
                }
            }
            //check top left
            yc = (sourceRow-1);
            xc = (sourceCol-1);
            for(; (yc >= 0) && (xc >=0); yc--,xc--){
                if(gamemap[yc][xc]!= null){
                    if( (targetRow < yc) && (targetCol < xc) ){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void inverseMapArray(GameState state){
        int mapDim = 8;
        ChessPieceAbstract[][] gamemap = state.getCpa();
        ChessPieceAbstract[][] tempChessArray = new ChessPieceAbstract[mapDim][mapDim];

        for (int row = mapDim - 1; row >= 0; row--) {
            for (int col = mapDim - 1; col >= 0; col--) {
                tempChessArray[(mapDim - 1) - row][(mapDim - 1) - col] = gamemap[row][col];
            }
        }
        state.setCpa(tempChessArray);
    }

    public static void main(String[] args) {
        System.out.println("Server starting");
        new Server();
    }

}
