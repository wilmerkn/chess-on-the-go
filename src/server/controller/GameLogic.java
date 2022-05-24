package server.controller;

import client.board.BoardView;
import client.gameview.GameView;
import client.gameview.BoardPanel;
import client.gameview.PromotePawnWindow;
import server.model.*;

import javax.swing.*;
import java.util.*;

//todo switch moveset when array inversed,

//todo only initialize playable side with moves

//todo complete all chess peices to fully functioning.
// currently complete: knight,king,Pawn,rook,bishop,queen
// left to fix: turnbased, checkmate, castling.

//todo check for check mate every draw.

//todo inverse squarepanel in inverse array method

public class GameLogic {

    //private GameView view;
    private GameModel model;
    private GameAudio gameAudio;
    private ChessPieceAbstract originalBoard [][];

    public GameLogic(){

        //this.view = new GameView(this);

        this.model = new GameModel();
        this.originalBoard = model.getMap().getMap();
        this.gameAudio = new GameAudio();

        //all game code runs here
        model.setMap(new GameMap(8));



        //disableChesspieces();
    }



    //inverses map array
    public void inverseMapArray(){
        int mapDim = model.getMap().getMapDimension();
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        ChessPieceAbstract[][] tempChessArray = new ChessPieceAbstract[mapDim][mapDim];

        for (int row = mapDim - 1; row >= 0; row--) {
            for (int col = mapDim - 1; col >= 0; col--) {
                tempChessArray[(mapDim - 1) - row][(mapDim - 1) - col] = gamemap[row][col];
            }
        }
        model.getMap().setMap(tempChessArray);
    }

    //draws up Gui Map from map array


    //method used to update logical game map with chesspieces new position
    /*public void update(int y_or, int x_or, int y_tr, int x_tr){ //called when chesspiece is moved
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        BoardPanel.SquarePanel[][] squarePanel = view.getBoardPanel().getSquares();

        ChessPiece tempChesspiece = null;
        tempChesspiece = ((ChessPiece) gamemap[y_or][x_or]);

            squarePanel[y_tr][x_tr].revalidate(); // new
            squarePanel[y_tr][x_tr].repaint(); // new

            gamemap[y_tr][x_tr] = tempChesspiece;
            gamemap[y_or][x_or] = null;

            if( tempChesspiece.getChessPieceType() == ChessPieceType.PAWN && tempChesspiece.getMoved() == 0 ){
                tempChesspiece.setMoved(1);
                tempChesspiece.setMoveset(new int[][]{
                        {3,1,3},
                        {2,0,2},
                        {2,2,2}
                });
            }
        check(ChessPieceColor.BLACK, ChessPieceColor.WHITE, gamemap);

        check(ChessPieceColor.WHITE, ChessPieceColor.BLACK, gamemap);
//copy this to client
/*

        if (tempChesspiece != null && tempChesspiece.getChessPieceType() == ChessPieceType.PAWN ) {
            if (getLocationX(tempChesspiece, gamemap) == 0) {
                PromotePawnWindow pawnWindow = new PromotePawnWindow();
                pawnWindow.chooseChesspiece(this, tempChesspiece.getColor(), tempChesspiece);
            }
        }
            gameAudio.playSound("src\\AudioFiles\\mixkit-quick-jump-arcade-game-239.wav",0);

            check(ChessPieceColor.WHITE, ChessPieceColor.BLACK, gamemap);
            check(ChessPieceColor.BLACK, ChessPieceColor.WHITE, gamemap);
            inverseMapArray();
            drawMap();
    }
*/
    //check for check and checkmate
    public void check(ChessPieceColor friendlyColor, ChessPieceColor enemyColor, ChessPieceAbstract[][] gamemap){
        ChessPiece king = getTheKing(friendlyColor, gamemap);
        if (isCheck(king, enemyColor)) {
            //maybe highlight the square or play some sound effect?
            if ((isCheckmate(king, enemyColor, friendlyColor) == true)) {
                //show checkmateWindow with the name of the winner (maybe show the players username?)
                CheckmateWindow checkmateWindow = new CheckmateWindow(enemyColor.toString());
            }
        }
    }

    //method that checks for checkmate
    private boolean isCheckmate(ChessPiece theKing, ChessPieceColor enemyColor, ChessPieceColor friendlyColor) {
        ChessPieceAbstract[][] board = model.getMap().getMap();
        ArrayList<Integer> xKing = new ArrayList<>();
        ArrayList<Integer> yKing = new ArrayList<>();
        ArrayList<String> deadlyXandY = new ArrayList<String>();


        //create a method for this later
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                ChessPiece cp = (ChessPiece) board[i][j];
                //store coordinates of moves that are either empty or that have the opposite player's chess pieces on it
                if(board[i][j]==null || cp.getColor().equals(enemyColor)){
                    //check if the king can move there
                    if(moveValid(getLocationX(theKing, board), getLocationY(theKing, board), i, j, board)==true){
                        //save the coordinates
                        xKing.add(i);
                        yKing.add(j);
                    }
                }
            }
        }

        deadlyXandY.addAll(deadlyMoves(xKing, yKing, board, theKing, enemyColor));
        deadlyXandY.addAll(protectedPieces(xKing, yKing, board, friendlyColor, enemyColor));

        //remove this later
        for(int i = 0; i < xKing.size(); i++) {
            System.out.println("Possible moves: " + xKing.get(i) + ", " + yKing.get(i));
        }
        for(int i = 0; i < deadlyXandY.size(); i++){
            System.out.println("This is the whole list  " + deadlyXandY.get(i));
        }

        ArrayList uniqueListOfDeadlyMoves = deleteDuplicates(deadlyXandY);
        for(int i = 0; i < uniqueListOfDeadlyMoves.size(); i++){
            System.out.println("Deadly moves: " + deadlyXandY.get(i));
        }
        System.out.println("Number of possible moves: " + xKing.size() + ", number of deadly moves: " +uniqueListOfDeadlyMoves.size());

        //return true if the number of possible moves is equal to the number of moves that would result in the kings death
        if(uniqueListOfDeadlyMoves.size() == xKing.size()){
            System.out.println("Game Over");
            return true;
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
                        System.out.println(cp.toString() + " " + getLocationX(cp , board) + ", " + getLocationY(cp, board));
                        //see if a chess piece could attack the king
                        if (checkMove(getLocationX(cp, board), getLocationY(cp, board), cp, xKing.get(xy), yKing.get(xy), theKing) == true) {
                            list.add(xKing.get(xy)+", "+yKing.get(xy));
                        }
                    }
                }
            }
        }

        for(int i = 0; i < list.size(); i++){
            System.out.println("This is deadlyMoves(): " + list.get(i));
        }
        return list;
    }

    //this method returns an arraylist containing the coordinates of chess pieces that are protected
    //this method is used in the checkmate method, to see if the king can escape a check position by attacking one of the opponent's chess pieces
    //if by attacking the chess piece the king would find itself in a check position (again), add that position's coordinates in the array list
    private ArrayList<String> protectedPieces(ArrayList<Integer> xKing, ArrayList<Integer> yKing, ChessPieceAbstract[][] board, ChessPieceColor friendlyColor, ChessPieceColor enemyColor){
        ArrayList<String> list = new ArrayList<>();

        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int xy = 0; xy < xKing.size(); xy++) {
                    ChessPiece originalCp = (ChessPiece) board[xKing.get(xy)][yKing.get(xy)];
                    if (originalCp != null) {
                        ChessPiece chessPiece = (ChessPiece) board[i][j];
                        if (chessPiece != null && chessPiece.getColor() == enemyColor && chessPiece != originalCp) {
                            //create a fake chess piece - in this case the getTheKing method is used to do that
                            ChessPiece fakeChessPiece = getTheKing(friendlyColor, board);
                            if (checkMove(getLocationX(chessPiece, board), getLocationY(chessPiece, board), chessPiece, getLocationX(originalCp, board), getLocationY(originalCp, board), fakeChessPiece) == true) {
                               System.out.println(chessPiece + " " + " that is on " + getLocationX(chessPiece, board) + ", " + getLocationY(chessPiece, board) + " protects " + originalCp);
                                list.add(xKing.get(xy)+", " + yKing.get(xy));
                            }
                        }
                    }
                }
            }
        }

        for(int i = 0; i < list.size(); i++){
            System.out.println("This is from protectedPieces(): " + list.get(i));
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
        public boolean isCheck(ChessPiece theKing, ChessPieceColor enemyColor) {
            for(int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    try {
                        ChessPiece chessPiece = (ChessPiece) model.getMap().getMap()[i][j];
                        if (chessPiece != null && chessPiece.getColor().equals(enemyColor)) {
                            ChessPieceAbstract[][] mapp = model.getMap().getMap();
                            int kingX = getLocationX(theKing, model.getMap().getMap());
                            int kingY = getLocationY(theKing, model.getMap().getMap());
                            boolean valid = moveValid(i, j, kingX, kingY, mapp);
                            if (valid) {
                                System.out.println("CHECK!");
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
    public boolean checkMove(int chessPiece1Row, int chessPiece1Col, ChessPiece chessPiece1, int chessPiece2Row, int chessPiece2Col, ChessPiece chessPiece2) {
        ChessPieceAbstract[][]mapp = new ChessPieceAbstract[8][8];
        //create a new ChessPieceAbstract[][] map and copy the elements of the current chess board
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                mapp[i][j] = model.getMap().getMap()[i][j];
            }
        }
        //put the two chess pieces on the fake chess board
        mapp[chessPiece1Row][chessPiece1Col] = chessPiece1;
        mapp[chessPiece2Row][chessPiece2Col] = chessPiece2;
        //see if the first chess piece can attack the second chess piece
        boolean valid = moveValid(chessPiece1Row, chessPiece1Col, chessPiece2Row, chessPiece2Col, mapp);
        if(valid){
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


    //method returns the rook used in the castle method
    private ChessPiece getTheRookForCastle(ChessPieceColor kingColor, ChessPieceAbstract[][] gamemap, ChessPiece theKing){
        for(int i = 0;i < 8; i ++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece chessPiece = (ChessPiece) gamemap[i][j];
                if (chessPiece != null && chessPiece.getColor() == kingColor && chessPiece.getChessPieceType() == ChessPieceType.ROOK) {
                    if (getLocationX(chessPiece, gamemap) == getLocationX(theKing, gamemap) && Math.abs(getLocationY(theKing, gamemap) - getLocationY(chessPiece, gamemap)) == 2) {
                        return chessPiece;
                    }
                }
            }
        }
        return null;
    }

    private boolean kingCanCastle(ChessPieceColor kingColor, ChessPieceColor enemyColor) {
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        ChessPiece theKing = getTheKing(kingColor, gamemap);
        ChessPiece theRook = getTheRookForCastle(kingColor, gamemap, theKing);
        ChessPiece therook2 = null;

        for(int i = 0;i<8; i++){
            for(int j = 0;  j < 8; j++){
                ChessPiece cp = (ChessPiece) gamemap[i][j];
                if(cp!=null){
                    if(cp.getChessPieceType()==ChessPieceType.ROOK && cp.getColor()==kingColor){
                        therook2 = cp;
                    }
                }
            }
        }

        //if the king and / or the rook were not moved, and if the space between them is empty, return true
        //to see if the places between them are empty, we use the checkMove method that puts checks if the rook can move to the kings place
        if(//kingWasMoved = false && theRooksWasMoved = false &&
                checkMove(getLocationX(theRook, gamemap), getLocationY(theRook, gamemap), therook2, getLocationX(theKing, gamemap),
                getLocationY(theKing, gamemap), getTheKing(enemyColor, gamemap))){

            System.out.println(theKing + " can castle!");
            return true;
        }
        return false;
    }

    //finds the chess piece the player wants
    public ChessPiece getChesspiece(ChessPieceColor color, ChessPieceType type){
        ChessPiece theChessPiece = null;
        for(int r = 0; r <8; r++){
            for(int c = 0; c < 8; c++){
                //loop thorugh the original board
                ChessPiece cp = (ChessPiece) originalBoard[r][c];
                if(cp!=null && cp.getChessPieceType()==type && cp.getColor()==color){
                    theChessPiece = cp;
                }
            }
        }
        //set the location of that chess piece
        return theChessPiece;
    }













    //todo block highlighting if chesspieces in the way
    //method used to show highlighted movement pattern in GUI
    /*public void highlightMovementPattern(int srcY,int srcX){

        int YCord = srcY; //get y/x cordinate of piece
        int XCord = srcX;

        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        ChessPiece cp = (ChessPiece) gamemap[YCord][XCord]; //get piece at cordinate
        int[][] moveset = cp.getMoveset(); //get moveset of piece
        BoardPanel.SquarePanel[][] squarePanel = view.getBoardPanel().getSquares(); //get GUI panel

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
    }*/

    //check if move input by user is a valid move in chess
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

        samespot = samecpspot(sourceRow,sourceCol,targetRow,targetCol);
        withinMoveset = moveWithinCPMoveset(sourceRow,sourceCol,targetRow,targetCol,movesetOffsetY,movesetOffsetX,yTrOffset,xTrOffset,moveset,cp,gamemap);
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

    //cleans board of chesspiece sprites
/*    public void cleanBoard(){
        BoardPanel.SquarePanel[][] squarePanel = view.getBoardPanel().getSquares();
        for(int row = 0; row < squarePanel.length; row++){
            for(int col = 0; col < squarePanel[row].length; col++){
                squarePanel[row][col].removePiece();
            }
        }
    }*/

    //todo work on this

/*    public void disableChesspieces(){
        int playerTurn = model.getGameState().getPlayerTurn();
        ChessPieceAbstract[][] gamemap = model.getMap().getMap();
        BoardPanel.SquarePanel[][] squarePanel = view.getBoardPanel().getSquares();
        HashMap<String, JLabel> notationlbls = view.getBoardPanel().getNotationToJLMap();
        BoardPanel bp = view.getBoardPanel();
        //if player turn 1, disable black and vice versa

        ChessPiece cp;

        if (playerTurn == 1){
            for(int row = 0; row < gamemap.length; row++){
                for(int col = 0; col < gamemap[row].length; col ++){

                    if( gamemap[row][col] != null ){
                        cp = ((ChessPiece)gamemap[row][col]);
                        if(cp.getColor() == ChessPieceColor.BLACK){
                            bp.setSquareMouseListenerActive(row,col,false);
                        }

                    }

                }
            }
        }
        else if(playerTurn == 2){
            for(int row = 0; row < gamemap.length; row++){
                for(int col = 0; col < gamemap[row].length; col ++){

                }
            }
        }

    }*/

    public GameModel getModel() {
        return model;
    }

    //notes:
    //todo method used to switch movement pattern on pawns after first move, cleans board slate of chesspieces for redraw(done)
    // .
    // Kolla på hur logik ska skjötas på server medans clienter skickar moves.
    // Castling requirements:
    // Your king has not moved in the game yet.
    // Your king is not in check.
    // The king does not castle through a square which is controlled by an opponent’s piece.
    // The king is not in check after castling.
    // The rook has not been moved in the game yet.

}
