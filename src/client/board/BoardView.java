package client.board;

import server.controller.GameLogic;

import org.w3c.dom.ranges.Range;
import server.model.ChessPiece;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class BoardView {
    private final static Dimension CLIENT_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_DIMENSION = new Dimension(400, 300);
    private final static Dimension SQUARE_DIMENSION = new Dimension(15, 15);
    private final static Color LIGHT_SQUARE_COLOR = new Color(211, 230, 211);
    private final static Color DARK_SQUARE_COLOR = new Color(82, 146, 82);
    private static final Border HIGHLIGHTER = BorderFactory.createLineBorder(Color.yellow, 3);

    private BoardPanel boardPanel;
    private boolean mouseListenerEnabled;
    private final HashMap<String, JLabel> notationToJLMap = BoardUtils.pieceNotationToJL();
    private GameLogic gameLogic;



    private JLabel checkLabel;

    public BoardView() {
        this.boardPanel = new BoardPanel();
        JFrame frame = new JFrame();
        frame.setTitle("Chess On The Go");
        frame.setSize(CLIENT_DIMENSION);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.add(boardPanel, BorderLayout.CENTER);
        //boardPanel.populateBoard();

        mouseListenerEnabled = true;

    }
    public BoardView(GameLogic gameLogic) {
        this.boardPanel = new BoardPanel();
        JFrame frame = new JFrame();
        frame.setTitle("Chess On The Go");
        this.gameLogic = gameLogic;
        frame.setSize(CLIENT_DIMENSION);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.add(boardPanel, BorderLayout.CENTER);
        //boardPanel.populateBoard();

        mouseListenerEnabled = true;

    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public HashMap<String, JLabel> getNotationToJLMap() {
        return notationToJLMap;
    }

    public class BoardPanel extends JPanel {
        private int sourceRow, sourceCol, targetRow, targetCol;
        private final SquarePanel[][] squares = new SquarePanel[8][8];

        public BoardPanel() {
            super(new GridLayout(8, 8));
            this.sourceRow = this.sourceCol = this.targetRow = this.targetCol = -1;

            for (int row = 0; row < 8; row++){
                for(int col = 0; col < 8; col++){
                    SquarePanel squarePanel = new SquarePanel(row, col);
                    squares[row][col] = squarePanel;
                    this.add(squarePanel);
                    setPreferredSize(BOARD_DIMENSION);
                    setVisible(true);
                    validate();
                    squarePanel.addMouseListener(createMouselistener(squares[row][col]));
                }
            }
        }

        private void movePiece(SquarePanel source, SquarePanel target) {
            if(!source.isOccupied()) return;
            else if (target.isOccupied()) target.removePiece();
            target.placePiece(source.getPiece());
            source.removePiece();
        }

        /*
        private void populateBoard() {
            squares[0][0].placePiece(notationToJLMap.get("BR"));
            squares[0][1].placePiece(notationToJLMap.get("BN"));
            squares[0][2].placePiece(notationToJLMap.get("BB"));
            squares[0][3].placePiece(notationToJLMap.get("BQ"));
            squares[0][4].placePiece(notationToJLMap.get("BK"));
            squares[0][5].placePiece(notationToJLMap.get("BB"));
            squares[0][6].placePiece(notationToJLMap.get("BN"));
            squares[0][7].placePiece(notationToJLMap.get("BR"));
            for (int i = 0; i < 8; i++) {
                squares[1][i].placePiece(notationToJLMap.get("BP"));
                squares[6][i].placePiece(notationToJLMap.get("WP"));
            }
            squares[7][0].placePiece(notationToJLMap.get("WR"));
            squares[7][1].placePiece(notationToJLMap.get("WN"));
            squares[7][2].placePiece(notationToJLMap.get("WB"));
            squares[7][3].placePiece(notationToJLMap.get("WQ"));
            squares[7][4].placePiece(notationToJLMap.get("WK"));
            squares[7][5].placePiece(notationToJLMap.get("WB"));
            squares[7][6].placePiece(notationToJLMap.get("WN"));
            squares[7][7].placePiece(notationToJLMap.get("WR"));
        }

         */

        private void enableMouseListener() {
            mouseListenerEnabled = true;
        }

        private void disableMouseListener() {
            mouseListenerEnabled = false;
        }



        //fix this later
        private MouseListener createMouselistener(SquarePanel squarePanel){
            return new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) { }

                @Override
                public void mousePressed(MouseEvent e) {

                    if(SwingUtilities.isLeftMouseButton(e))  {
                        if(!mouseListenerEnabled) return;
                        if(sourceRow < 0 || sourceCol < 0) {
                            if(!squarePanel.isOccupied()) return;
                            sourceRow = squarePanel.getRow();
                            sourceCol = squarePanel.getCol();
                            squares[sourceRow][sourceCol].toggleHighlight();
                            targetRow = targetCol = -1;
                            System.out.println("row "+sourceRow);
                            System.out.println("col "+sourceCol);

                        } else {
                            targetRow = squarePanel.getRow();
                            targetCol = squarePanel.getCol();
                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                            squares[sourceRow][sourceCol].toggleHighlight();
                            gameLogic.update(sourceRow,sourceCol,targetRow,targetCol);
                            sourceRow = sourceCol = targetRow = targetCol = -1;

                            //if the chess piece is a white pawn
                            if(targetRow-sourceRow==-1 & targetCol-sourceCol==0 & squares[sourceRow][sourceCol].getPiece().getIcon()==notationToJLMap.get("WP").getIcon()){
                                movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                squares[sourceRow][sourceCol].toggleHighlight();
                                sourceRow = sourceCol = targetRow = targetCol = -1;
                            }

                            //if the chess piece is a black pawn
                            else if(targetRow-sourceRow==1 & targetCol - sourceCol == 0 & squares[sourceRow][sourceCol].getPiece().getIcon()==notationToJLMap.get("BP").getIcon()){
                                movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                squares[sourceRow][sourceCol].toggleHighlight();
                                sourceRow = sourceCol = targetRow = targetCol = -1;
                            }

                            //if the chess piece is a black rook
                            else if(squares[sourceRow][sourceCol].getPiece().getIcon()==notationToJLMap.get("BR").getIcon()){
                                if(sourceCol==targetCol){
                                    if (sourceRow < targetRow) {
                                        for (int i = sourceRow + 1; i <= targetRow; i++)
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                    } else {
                                        for (int i = sourceRow - 1; i >= targetRow; i--) {
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                        }
                                    }
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;
                                }
                                else if(sourceRow==targetRow){
                                    if(sourceCol < targetCol) {
                                        for (int i = sourceCol + 1; i <= targetCol; i++) {
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                        }
                                    }
                                    else{
                                        for(int i = sourceCol -1; i >= targetCol; i--){
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                        }
                                    }
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;
                                }
                            }

                            //if the chess piece is a white rook
                            else if(squares[sourceRow][sourceCol].getPiece().getIcon()==notationToJLMap.get("WR").getIcon()){
                                if(sourceCol==targetCol){
                                    if (sourceRow < targetRow) {
                                        for (int i = sourceRow + 1; i <= targetRow; i++)
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                    } else {
                                        for (int i = sourceRow - 1; i >= targetRow; i--) {
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                        }
                                    }
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;
                                }
                                else if(sourceRow==targetRow){
                                    if(sourceCol < targetCol) {
                                        for (int i = sourceCol + 1; i <= targetCol; i++) {
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                        }
                                    }
                                    else{
                                        for(int i = sourceCol -1; i >= targetCol; i--){
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                        }
                                    }
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;
                                }
                            }

                            //if the chess piece is a black bishop
                            else if(squares[sourceRow][sourceCol].getPiece().getIcon()==notationToJLMap.get("BB").getIcon()){
                                if( Math.abs(sourceRow-targetRow) == Math.abs(sourceCol-targetCol)) {
                                    movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;
                                }
                            }

                            //if the chess piece is a white bishop
                            else if(squares[sourceRow][sourceCol].getPiece().getIcon()==notationToJLMap.get("WB").getIcon()){
                                if( Math.abs(sourceRow-targetRow) == Math.abs(sourceCol-targetCol)) {
                                    movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;
                                }
                            }

                            //if the chess piece is a white knight
                            else if(squares[sourceRow][sourceCol].getPiece().getIcon()==notationToJLMap.get("WN").getIcon()){
                                if((targetRow== sourceRow+2 && targetCol==sourceCol+1) || (targetRow== sourceRow+2 && targetCol==sourceCol-1)||
                                        (targetRow==sourceRow-2 && targetCol==sourceCol+1) || (targetRow==sourceRow-2 && targetCol==sourceCol-1) ||
                                        (targetRow==sourceRow+1 && targetCol==sourceCol+2) || (targetRow==sourceRow-1 && targetCol==sourceCol+2) ||
                                        (targetRow==sourceRow+1 && targetCol==sourceCol-2) || (targetRow==sourceRow-1 && targetCol==sourceCol-2)
                                ){
                                    movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;
                                }
                            }

                            //if the chess piece is a black knight
                            else if(squares[sourceRow][sourceCol].getPiece().getIcon()==notationToJLMap.get("BN").getIcon()){
                                if((targetRow== sourceRow+2 && targetCol==sourceCol+1) || (targetRow== sourceRow+2 && targetCol==sourceCol-1)||
                                        (targetRow==sourceRow-2 && targetCol==sourceCol+1) || (targetRow==sourceRow-2 && targetCol==sourceCol-1) ||
                                        (targetRow==sourceRow+1 && targetCol==sourceCol+2) || (targetRow==sourceRow-1 && targetCol==sourceCol+2) ||
                                        (targetRow==sourceRow+1 && targetCol==sourceCol-2) || (targetRow==sourceRow-1 && targetCol==sourceCol-2)
                                ){
                                    movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;
                                }
                            }

                            //if the chess piece is a black king
                            else if(squares[sourceRow][sourceCol].getPiece().getIcon()==notationToJLMap.get("BK").getIcon()){
                                    if((targetRow== sourceRow+1 && targetCol==sourceCol+1) || (targetRow== sourceRow+1 && targetCol==sourceCol-1) ||
                                            (targetRow==sourceRow-1 && targetCol==sourceCol+1) || (targetRow==sourceRow-1 && targetCol==sourceCol-1) ||
                                            (targetRow==sourceRow+1 && targetCol==sourceCol+1) || (targetRow==sourceRow-1 && targetCol==sourceCol+1) ||
                                            (targetRow==sourceRow+1 && targetCol==sourceCol-1) || (targetRow==sourceRow-1 && targetCol==sourceCol-1) ||
                                            (targetRow==sourceRow+1 && targetCol==sourceCol) || (targetRow==sourceRow-1 && targetCol==sourceCol)||
                                            (targetRow==sourceRow && targetCol==sourceCol+1) || (targetRow==sourceRow && targetCol==sourceCol-1)){

                                        movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                        squares[sourceRow][sourceCol].toggleHighlight();
                                        sourceRow = sourceCol = targetRow = targetCol = -1;
                                    }
                                }

                            //if the chess piece is a white king
                            else if(squares[sourceRow][sourceCol].getPiece().getIcon()==notationToJLMap.get("WK").getIcon()){
                                if((targetRow== sourceRow+1 && targetCol==sourceCol+1) || (targetRow== sourceRow+1 && targetCol==sourceCol-1) ||
                                        (targetRow==sourceRow-1 && targetCol==sourceCol+1) || (targetRow==sourceRow-1 && targetCol==sourceCol-1) ||
                                        (targetRow==sourceRow+1 && targetCol==sourceCol+1) || (targetRow==sourceRow-1 && targetCol==sourceCol+1) ||
                                        (targetRow==sourceRow+1 && targetCol==sourceCol-1) || (targetRow==sourceRow-1 && targetCol==sourceCol-1) ||
                                        (targetRow==sourceRow+1 && targetCol==sourceCol) || (targetRow==sourceRow-1 && targetCol==sourceCol)||
                                        (targetRow==sourceRow && targetCol==sourceCol+1) || (targetRow==sourceRow && targetCol==sourceCol-1)){

                                    movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;
                                }
                            }

                            //if the chess piece is a white queen
                            else if(squares[sourceRow][sourceCol].getPiece().getIcon()==notationToJLMap.get("WQ").getIcon()) {
                                if (sourceCol == targetCol) {
                                    if (sourceRow < targetRow) {
                                        for (int i = sourceRow + 1; i <= targetRow; i++)
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                    } else {
                                        for (int i = sourceRow - 1; i >= targetRow; i--) {
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                        }
                                    }
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;
                                } else if (sourceRow == targetRow) {
                                    if (sourceCol < targetCol) {
                                        for (int i = sourceCol + 1; i <= targetCol; i++) {
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                        }
                                    } else {
                                        for (int i = sourceCol - 1; i >= targetCol; i--) {
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                        }
                                    }
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;
                                }

                                    else if((targetRow== sourceRow+1 && targetCol==sourceCol+1) || (targetRow== sourceRow+1 && targetCol==sourceCol-1) ||
                                            (targetRow==sourceRow-1 && targetCol==sourceCol+1) || (targetRow==sourceRow-1 && targetCol==sourceCol-1) ||
                                            (targetRow==sourceRow+1 && targetCol==sourceCol+1) || (targetRow==sourceRow-1 && targetCol==sourceCol+1) ||
                                            (targetRow==sourceRow+1 && targetCol==sourceCol-1) || (targetRow==sourceRow-1 && targetCol==sourceCol-1) ||
                                            (targetRow==sourceRow+1 && targetCol==sourceCol) || (targetRow==sourceRow-1 && targetCol==sourceCol)||
                                            (targetRow==sourceRow && targetCol==sourceCol+1) || (targetRow==sourceRow && targetCol==sourceCol-1)||
                                            (targetRow== sourceRow+2 && targetCol==sourceCol+1) || (targetRow== sourceRow+2 && targetCol==sourceCol-1)||
                                            (targetRow==sourceRow-2 && targetCol==sourceCol+1) || (targetRow==sourceRow-2 && targetCol==sourceCol-1) ||
                                            (targetRow==sourceRow+1 && targetCol==sourceCol+2) || (targetRow==sourceRow-1 && targetCol==sourceCol+2) ||
                                            (targetRow==sourceRow+1 && targetCol==sourceCol-2) || (targetRow==sourceRow-1 && targetCol==sourceCol-2) ||
                                            Math.abs(sourceRow-targetRow) == Math.abs(sourceCol-targetCol) ||
                                            (targetRow-sourceRow==-1 & targetCol-sourceCol==0)){

                                        movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                        squares[sourceRow][sourceCol].toggleHighlight();
                                        sourceRow = sourceCol = targetRow = targetCol = -1;

                                    }
                            }

                            //if the chess piece is a black queen
                            else if(squares[sourceRow][sourceCol].getPiece().getIcon()==notationToJLMap.get("BQ").getIcon()) {
                                if (sourceCol == targetCol) {
                                    if (sourceRow < targetRow) {
                                        for (int i = sourceRow + 1; i <= targetRow; i++)
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                    } else {
                                        for (int i = sourceRow - 1; i >= targetRow; i--) {
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                        }
                                    }
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;
                                } else if (sourceRow == targetRow) {
                                    if (sourceCol < targetCol) {
                                        for (int i = sourceCol + 1; i <= targetCol; i++) {
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                        }
                                    } else {
                                        for (int i = sourceCol - 1; i >= targetCol; i--) {
                                            movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                        }
                                    }
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;
                                }

                                else if((targetRow== sourceRow+1 && targetCol==sourceCol+1) || (targetRow== sourceRow+1 && targetCol==sourceCol-1) ||
                                        (targetRow==sourceRow-1 && targetCol==sourceCol+1) || (targetRow==sourceRow-1 && targetCol==sourceCol-1) ||
                                        (targetRow==sourceRow+1 && targetCol==sourceCol+1) || (targetRow==sourceRow-1 && targetCol==sourceCol+1) ||
                                        (targetRow==sourceRow+1 && targetCol==sourceCol-1) || (targetRow==sourceRow-1 && targetCol==sourceCol-1) ||
                                        (targetRow==sourceRow+1 && targetCol==sourceCol) || (targetRow==sourceRow-1 && targetCol==sourceCol)||
                                        (targetRow==sourceRow && targetCol==sourceCol+1) || (targetRow==sourceRow && targetCol==sourceCol-1)||
                                        (targetRow== sourceRow+2 && targetCol==sourceCol+1) || (targetRow== sourceRow+2 && targetCol==sourceCol-1)||
                                        (targetRow==sourceRow-2 && targetCol==sourceCol+1) || (targetRow==sourceRow-2 && targetCol==sourceCol-1) ||
                                        (targetRow==sourceRow+1 && targetCol==sourceCol+2) || (targetRow==sourceRow-1 && targetCol==sourceCol+2) ||
                                        (targetRow==sourceRow+1 && targetCol==sourceCol-2) || (targetRow==sourceRow-1 && targetCol==sourceCol-2) ||
                                        Math.abs(sourceRow-targetRow) == Math.abs(sourceCol-targetCol) ||
                                        (targetRow-sourceRow==-1 & targetCol-sourceCol==0)){

                                    movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;

                                }
                            }

                        }
                    } else if (SwingUtilities.isRightMouseButton(e) && !(sourceRow == -1 && sourceCol ==-1)) {
                        squares[sourceRow][sourceCol].toggleHighlight();
                        sourceRow = sourceCol = targetRow = targetCol = -1;
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) { }

                @Override
                public void mouseEntered(MouseEvent e) { }

                @Override
                public void mouseExited(MouseEvent e) { }
            };
        }
        public SquarePanel[][] getSquares() {
            return squares;
        }
    }

    private class SquarePanel extends JPanel {
        private final int row;
        private final int col;
        private JLabel piece;

        public SquarePanel (int row, int col) {
            this.row = row;
            this.col = col;
            this.assignColor();
            this.setLayout(new BorderLayout());
            this.setPreferredSize(SQUARE_DIMENSION);
            this.setVisible(true);
        }

        private int getRow() {
            return row;
        }

        private int getCol() {
            return col;
        }

        private void assignColor() {
            if(col % 2 == 0 ) {
                if(row % 2 == 0) {
                    setBackground(LIGHT_SQUARE_COLOR);
                } else {
                    setBackground(DARK_SQUARE_COLOR);
                }
            } else {
                if(row % 2 != 0) {
                    setBackground(LIGHT_SQUARE_COLOR);
                } else {
                    setBackground(DARK_SQUARE_COLOR);
                }
            }
        }

        private void placePiece(JLabel pieceJL) {
            JLabel newPieceJL = BoardUtils.cloneIconJL(pieceJL);
            this.removePiece();
            piece = newPieceJL;
            this.add(newPieceJL);
            validate();
        }

        private void removePiece() {
            if(piece != null) {
                this.remove(piece);
                piece = null;
                revalidate();
                repaint();
            }
        }

        private JLabel getPiece() {
            return piece;
        }

        private boolean isOccupied() {
            return piece != null;
        }

        private void toggleHighlight() {
            if(this.getBorder() != null) this.setBorder(null);
            else this.setBorder(HIGHLIGHTER);
        }
    }
}
