package server;

import server.controller.LoginController;
import server.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements Runnable {

    private static final int PORT = 1234;

    private Hashtable<Player, ClientHandler> playerClientMap; // Testa concurrentHashmap
    //private List<GameLogic> games = new ArrayList<>();
    private LoginController loginController = new LoginController();
    private ArrayList<String> playerList = new ArrayList<>();

    private HashMap<String, Player> usernamePlayerMap = new HashMap<>();

    private GameState state;

    private HashMap<String, GameState> idGameStateMap = new HashMap<>();


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
                                    playerClientMap.get(player).getOos().reset();
                                    playerClientMap.get(player).getOos().writeObject(challenge);
                                    playerClientMap.get(player).getOos().flush();
                                }
                            }
                        } else {
                            state = new GameState();
                            idGameStateMap.put(state.getGameID(), state);

                            state.setPlayer1(challenge.getSenderUsername());
                            state.setPlayer2(challenge.getReceiverUsername());
                            state.setTimeControl(challenge.getTimeControl());
                            state.prepareTimers(challenge.getTimeControl());
                            state.setCpa(initializeMap());

                            playerClientMap.get(usernamePlayerMap.get(challenge.getReceiverUsername())).getOos().reset();
                            playerClientMap.get(usernamePlayerMap.get(challenge.getReceiverUsername())).getOos().writeObject(state);
                            playerClientMap.get(usernamePlayerMap.get(challenge.getReceiverUsername())).getOos().flush();

                            playerClientMap.get(usernamePlayerMap.get(challenge.getSenderUsername())).getOos().reset();
                            playerClientMap.get(usernamePlayerMap.get(challenge.getSenderUsername())).getOos().writeObject(state);
                            playerClientMap.get(usernamePlayerMap.get(challenge.getSenderUsername())).getOos().flush();

                            state.setStarted();

                            System.out.println("Skriver state till clienter");

                        }

                    } else if(object instanceof Message) {
                        Message msg = (Message) object;
                        // Inte viktigt nu

                    } else if(object instanceof Move) {
                        Move move = (Move) object;

                        GameState state = idGameStateMap.get(move.getGameID());
                        boolean validMove = moveValid(move, state.getCpa());

                        playerClientMap.get(usernamePlayerMap.get(move.getUsername())).getOos().writeBoolean(validMove);
                        //might have put it to early, should be incremented first when the move is registered
                        //if playerTurn % 1 = 0 then it's one of the players turn, if it isn't then it's the other players turn.
                        //playerTurn is incremented by 0.5 every move.
                        //depending on which turn it is timer should be stopped and started for the other player.

                        if(validMove) {
                            state.setCpa(update(move, state.getCpa()));

                            state.turnIncrement();
                            state.setStarted();
                            if (state.getPlayerTurn() % 1 == 0){
                                //stop timer for player1 and start for the other one
                            } else{
                                //stop timer for player2 and start for the other one
                            }
                            playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().reset();
                            playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().writeObject(state);
                            playerClientMap.get(usernamePlayerMap.get(state.getPlayer1())).getOos().flush();

                            playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().reset();
                            playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().writeObject(state);
                            playerClientMap.get(usernamePlayerMap.get(state.getPlayer2())).getOos().flush();
                        }
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
                    //System.out.println("broadcastPlayers " + playerList.size());
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

    public boolean moveValid(Move move, ChessPieceAbstract[][] gamemap){

        int sourceRow = move.getSourceRow();
        int sourceCol = move.getSourceCol();
        int targetRow = move.getTargetRow();
        int targetCol = move.getTargetCol();


        ChessPiece cp = (ChessPiece) gamemap[sourceRow][sourceCol];
        int[][] moveset = cp.getMoveset();
        ChessPieceType chessPieceType = cp.getChessPieceType();

        int movesetOffsetY = -1;
        int movesetOffsetX = -1;

        try {
            //get offsets from moveset
            for(int row = 0; row < moveset.length; row ++){
                for (int col = 0; col < moveset[row].length; col++){
                    if(moveset[row][col] == 0){ //find number 0 in moveset array to get offsets
                        movesetOffsetY = row;
                        movesetOffsetX = col;
                        break;
                    }
                }
            }

        } catch (Exception e) {

        }

        int yTrOffset = (sourceRow-targetRow);
        int xTrOffset = (sourceCol-targetCol);

        boolean samespot = false;
        boolean withinMoveset = false;
        boolean friendlyObstruction = false;
        boolean pawnobstruct = false;
        boolean pawnattacks = false;
        boolean pawnTwoMoveObstruct = false;
        boolean rookobstruction = false;
        boolean bishopobstruction = false;
        boolean queenObstruction = false;

        samespot = sameCpspot(sourceRow,sourceCol,targetRow,targetCol);
        withinMoveset = moveWithinCpMoveset(movesetOffsetY,movesetOffsetX,yTrOffset,xTrOffset,moveset,cp,gamemap);
        friendlyObstruction = friendlyCpObstruction(targetRow,targetCol,gamemap,cp);
        pawnattacks = pawnAttacks(targetRow,targetCol,movesetOffsetY,movesetOffsetX,yTrOffset,xTrOffset,moveset,cp,gamemap);
        pawnobstruct = pawnObstruction(targetRow,targetCol,gamemap,cp);
        pawnTwoMoveObstruct = pawn2Moves(targetRow,sourceRow,sourceCol,gamemap,cp);
        rookobstruction = rookObstruct(sourceRow,sourceCol,targetRow,targetCol,gamemap,cp);
        bishopobstruction = bishopObstruct(sourceRow,sourceCol,targetRow,targetCol,gamemap,cp);
        queenObstruction = queenObstruct(sourceRow,sourceCol,targetRow,targetCol,gamemap,cp);

        System.out.println("Samespot error: " + samespot);
        System.out.println("Withinmoveset error: " + withinMoveset);
        System.out.println("FriendlyObstruction error: " + friendlyObstruction);
        System.out.println("pawnObstruction error: " + pawnobstruct);
        System.out.println("PawnAttack: " + pawnattacks);
        System.out.println("PawnTwoMovesObstruct error: " + pawnTwoMoveObstruct);
        System.out.println("RookObstruction error: " + rookobstruction);
        System.out.println("BishopObstruction error: " + bishopobstruction);
        System.out.println("queenObstruction error: " + queenObstruction);

        if( (!samespot && !withinMoveset && !friendlyObstruction && !pawnobstruct && !pawnTwoMoveObstruct && !rookobstruction && !bishopobstruction) || (pawnattacks) || ( (!samespot && !withinMoveset && !friendlyObstruction && !pawnobstruct && !pawnTwoMoveObstruct) && !queenObstruction ) ){//if errorchecks are negative make move valid
            return true; //move is valid
        }
        else{
            return false;
        }
    }

    //did user click the same spot
    public boolean sameCpspot(int sR, int sC, int tR, int tC){
        if(sR == tR && sC == tC){
            return true;
        }
        else{
            return false;
        }
    }

    //does peice move within moveset
    public boolean moveWithinCpMoveset(int movesetOffsetY, int movesetOffsetX, int yTrOffset, int xTrOffset, int[][] moveset, ChessPiece cp,ChessPieceAbstract[][] gamemap){
        try{
            if(moveset[movesetOffsetY-yTrOffset][movesetOffsetX-xTrOffset] != 1){
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {

        }
        return true;
    }

    //checks if obstruction is of same chesspiece color
    public boolean friendlyCpObstruction(int targetRow, int targetCol, ChessPieceAbstract[][] gamemap,ChessPiece cp){
        if(gamemap[targetRow][targetCol] != null){
            ChessPieceColor obstructionColor = ((ChessPiece)gamemap[targetRow][targetCol]).getColor();
            if(cp.getColor() == obstructionColor){
                return true;
            }
        }
        return false;
    }

    //is pawn obstruct by enemy
    public boolean pawnObstruction(int targetRow, int targetCol, ChessPieceAbstract[][] gamemap, ChessPiece cp){
        if(cp.getChessPieceType() == ChessPieceType.PAWN && gamemap[targetRow][targetCol] != null){
            if(((ChessPiece)gamemap[targetRow][targetCol]).getColor() != cp.getColor()){
                return true;
            }
        }
        return false;
    }

    //checks if pawn can move 2 steps ahead if no obstruction is in the way
    public boolean pawn2Moves(int targetRow, int sourceRow, int sourceCol, ChessPieceAbstract[][] gamemap, ChessPiece cp){
        if(cp.getChessPieceType() == ChessPieceType.PAWN && cp.getMoved() == 0 && gamemap[sourceRow-1][sourceCol] != null && targetRow == sourceRow-2){
            return true;
        }
        return false;
    }

    //checks if pawn can attack with attack pattern
    public boolean pawnAttacks(int targetRow, int targetCol, int movesetOffsetY, int movesetOffsetX, int yTrOffset, int xTrOffset, int[][] moveset, ChessPiece cp, ChessPieceAbstract[][] gamemap){
        try{
            if(cp.getChessPieceType() == ChessPieceType.PAWN && gamemap[targetRow][targetCol] != null && ((ChessPiece)gamemap[targetRow][targetCol]).getColor() != cp.getColor() && moveset[movesetOffsetY-yTrOffset][movesetOffsetX-xTrOffset] == 3){
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    //highlight Rook path until obstruction
    public boolean rookObstruct(int sourceRow, int sourceCol, int targetRow, int targetCol, ChessPieceAbstract[][] gamemap, ChessPiece cp){
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
    public boolean bishopObstruct(int sourceRow,int sourceCol,int targetRow,int targetCol,ChessPieceAbstract[][] gamemap, ChessPiece cp){

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

    public boolean queenObstruct(int sourceRow,int sourceCol,int targetRow,int targetCol,ChessPieceAbstract[][] gamemap,ChessPiece cp){

        boolean diagonal = false;
        boolean bishop = false;
        boolean rook = false;

        bishop = bishopObstruction(sourceRow,sourceCol,targetRow,targetCol,gamemap,cp); //use bishop and rook patterns for queen obstruction
        rook = rookObstruction(sourceRow,sourceCol,targetRow,targetCol,gamemap,cp);

        //checks if source cordinate and target cordinate is diagonal
        if( (targetRow - sourceRow == targetCol - sourceCol) || (targetRow - sourceRow == sourceCol - targetCol) ) {
            diagonal = true;
        }
        // System.out.println("is diagonal: " + diagonal);

        //check what method to use
        if(diagonal && bishop){
            return true;
        }
        else if(!diagonal && rook){
            return true;
        }
        return false;
    }

    public ChessPieceAbstract[][] update(Move move, ChessPieceAbstract[][] cpa){ //called when chesspiece is moved

        int sourceRow = move.getSourceRow();
        int sourceCol = move.getSourceCol();
        int targetRow = move.getTargetRow();
        int targetCol = move.getTargetCol();
        ChessPieceAbstract[][] gamemap = cpa;
        //BoardPanel.SquarePanel[][] squarePanel = view.getBoardPanel().getSquares();

        ChessPiece tempChesspiece = null;
        tempChesspiece = ((ChessPiece) gamemap[sourceRow][sourceCol]);

        //squarePanel[y_tr][x_tr].revalidate(); // new
        //squarePanel[y_tr][x_tr].repaint(); // new

        gamemap[targetRow][targetCol] = tempChesspiece;
        gamemap[sourceRow][sourceCol] = null;

        if( tempChesspiece.getChessPieceType() == ChessPieceType.PAWN && tempChesspiece.getMoved() == 0 ){
            tempChesspiece.setMoved(1);
            tempChesspiece.setMoveset(new int[][]{
                    {3,1,3},
                    {2,0,2},
                    {2,2,2}
            });
        }
        //check(ChessPieceColor.BLACK, ChessPieceColor.WHITE, gamemap);
        //check(ChessPieceColor.WHITE, ChessPieceColor.BLACK, gamemap);

        //gameAudio.playSound("src\\AudioFiles\\mixkit-quick-jump-arcade-game-239.wav",0);
        //inverseMapArray();
        displayMap(gamemap);
        return gamemap;
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

    public boolean moveValid(int sourceRow, int sourceCol, int targetRow, int targetCol, ChessPieceAbstract[][] gamemap){
        ChessPiece cp = (ChessPiece) gamemap[sourceRow][sourceCol];
        int[][] moveset = cp.getMoveset();
        ChessPieceType chessPieceType = cp.getChessPieceType();

        int movesetOffsetY = -1;
        int movesetOffsetX = -1;

        try {
            //get offsets from moveset
            for(int row = 0; row < moveset.length; row ++){
                for (int col = 0; col < moveset[row].length; col++){
                    if(moveset[row][col] == 0){ //find number 0 in moveset array to get offsets
                        movesetOffsetY = row;
                        movesetOffsetX = col;
                        break;
                    }
                }
            }

        } catch (Exception e) {

        }

        int yTrOffset = (sourceRow-targetRow);
        int xTrOffset = (sourceCol-targetCol);

        boolean samespot = false;
        boolean withinMoveset = false;
        boolean friendlyObstruction = false;
        boolean pawnobstruct = false;
        boolean pawnattacks = false;
        boolean pawnTwoMoveObstruct = false;
        boolean rookobstruction = false;
        boolean bishopobstruction = false;
        boolean queenObstruction = false;

        samespot = samecpspot(sourceRow, sourceCol, targetRow, targetCol);
        withinMoveset = moveWithinCPMoveset(sourceRow, sourceCol, targetRow, targetCol, movesetOffsetY, movesetOffsetX, yTrOffset, xTrOffset, moveset, cp, gamemap);
        friendlyObstruction = friendlyCPObstruction(targetRow,targetCol,gamemap,cp);
        pawnattacks = pawnAttack(targetRow,targetCol,movesetOffsetY,movesetOffsetX,yTrOffset,xTrOffset,moveset,cp,gamemap);
        pawnobstruct = pawnObstruct(targetRow,targetCol,gamemap,cp);
        pawnTwoMoveObstruct = pawnTwoMoves(targetRow,sourceRow,sourceCol,gamemap,cp);
        rookobstruction = rookObstruction(sourceRow,sourceCol,targetRow,targetCol,gamemap,cp);
        bishopobstruction = bishopObstruction(sourceRow,sourceCol,targetRow,targetCol,gamemap,cp);
        queenObstruction = queenObstruction(sourceRow,sourceCol,targetRow,targetCol,gamemap,cp);

        System.out.println("Samespot error: " + samespot);
        System.out.println("Withinmoveset error: " + withinMoveset);
        System.out.println("FriendlyObstruction error: " + friendlyObstruction);
        System.out.println("pawnObstruction error: " + pawnobstruct);
        System.out.println("PawnAttack: " + pawnattacks);
        System.out.println("PawnTwoMovesObstruct error: " + pawnTwoMoveObstruct);
        System.out.println("RookObstruction error: " + rookobstruction);
        System.out.println("BishopObstruction error: " + bishopobstruction);
        System.out.println("queenObstruction error: " + queenObstruction);

        if( (!samespot && !withinMoveset && !friendlyObstruction && !pawnobstruct && !pawnTwoMoveObstruct && !rookobstruction && !bishopobstruction) || (pawnattacks) || ( (!samespot && !withinMoveset && !friendlyObstruction && !pawnobstruct && !pawnTwoMoveObstruct) && !queenObstruction ) ){//if errorchecks are negative make move valid
            return true; //move is valid
        }
        else{
            return false;
        }

    }

    public boolean samecpspot(int sR, int sC, int tR, int tC){
        if(sR == tR && sC == tC){
            //System.out.println("same position!"); debugging
            return true;
        }
        else{
            return false;
        }
    }

    //does peice move within moveset
    public boolean moveWithinCPMoveset(int sR, int sC, int tR, int tC, int movesetOffsetY, int movesetOffsetX, int yTrOffset, int xTrOffset, int[][] moveset, ChessPiece cp,ChessPieceAbstract[][] gamemap){
        try{
            if(moveset[movesetOffsetY-yTrOffset][movesetOffsetX-xTrOffset] != 1){
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {

        }
        return true;
    }

    //checks if obstruction is of same chesspiece color
    public boolean friendlyCPObstruction(int targetRow, int targetCol, ChessPieceAbstract[][] gamemap,ChessPiece cp){
        if(gamemap[targetRow][targetCol] != null){
            ChessPieceColor obstructionColor = ((ChessPiece)gamemap[targetRow][targetCol]).getColor();
            if(cp.getColor() == obstructionColor){
                return true;
            }
        }
        return false;
    }

    //is pawn obstruct by enemy
    public boolean pawnObstruct(int targetRow, int targetCol, ChessPieceAbstract[][] gamemap, ChessPiece cp){
        if(cp.getChessPieceType() == ChessPieceType.PAWN && gamemap[targetRow][targetCol] != null){
            if(((ChessPiece)gamemap[targetRow][targetCol]).getColor() != cp.getColor()){
                return true;
            }
        }
        return false;
    }

    //checks if pawn can move 2 steps ahead if no obstruction is in the way
    public boolean pawnTwoMoves(int targetRow, int sourceRow, int sourceCol, ChessPieceAbstract[][] gamemap, ChessPiece cp){
        if(cp.getChessPieceType() == ChessPieceType.PAWN && cp.getMoved() == 0 && gamemap[sourceRow-1][sourceCol] != null && targetRow == sourceRow-2){
            return true;
        }
        return false;
    }

    //checks if pawn can attack with attack pattern
    public boolean pawnAttack(int targetRow, int targetCol, int movesetOffsetY, int movesetOffsetX, int yTrOffset, int xTrOffset, int[][] moveset, ChessPiece cp, ChessPieceAbstract[][] gamemap){
        try{
            if(cp.getChessPieceType() == ChessPieceType.PAWN && gamemap[targetRow][targetCol] != null && ((ChessPiece)gamemap[targetRow][targetCol]).getColor() != cp.getColor() && moveset[movesetOffsetY-yTrOffset][movesetOffsetX-xTrOffset] == 3){
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {
            return false;
        }
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

    public boolean queenObstruction(int sourceRow,int sourceCol,int targetRow,int targetCol,ChessPieceAbstract[][] gamemap,ChessPiece cp){

        boolean diagonal = false;
        boolean bishop = false;
        boolean rook = false;

        bishop = bishopObstruction(sourceRow,sourceCol,targetRow,targetCol,gamemap,cp); //use bishop and rook patterns for queen obstruction
        rook = rookObstruction(sourceRow,sourceCol,targetRow,targetCol,gamemap,cp);

        //checks if source cordinate and target cordinate is diagonal
        if( (targetRow - sourceRow == targetCol - sourceCol) || (targetRow - sourceRow == sourceCol - targetCol) ) {
            diagonal = true;
        }
        // System.out.println("is diagonal: " + diagonal);

        //check what method to use
        if(diagonal && bishop){
            return true;
        }
        else if(!diagonal && rook){
            return true;
        }
        return false;
    }



    public static void main(String[] args) {
        System.out.println("Server starting");
        new Server();
    }
}
