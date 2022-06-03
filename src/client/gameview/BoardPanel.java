package client.gameview;

import client.Client;
import jdk.swing.interop.SwingInterOpUtils;
import server.controller.GameLogic;
import server.model.Move;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BoardPanel extends JPanel {
    private final static Dimension BOARD_DIMENSION = new Dimension(800, 750);
    private final static Dimension SQUARE_DIMENSION = new Dimension(15, 15);

    private final static Color LIGHT_SQUARE_COLOR = new Color(211, 230, 211);
    private final static Color DARK_SQUARE_COLOR = new Color(82, 146, 82);
    private static final Border HIGHLIGHTER = BorderFactory.createLineBorder(Color.yellow, 3);

    private int sourceRow, sourceCol, targetRow, targetCol;
    private final SquarePanel[][] squares = new SquarePanel[8][8];

    private boolean mouseListenersEnabled = false;

    private HashMap<String, JLabel> notationToJLMap = BoardUtils.pieceNotationToJL();

    private final List<SquareMouseListener> mouseListenerList = new ArrayList<SquareMouseListener>();

    private Client client;

    public BoardPanel(Client client) {
        super(new GridLayout(8, 8));
        this.client = client;


        setSize(BOARD_DIMENSION);

        this.sourceRow = this.sourceCol = this.targetRow = this.targetCol = -1;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                SquarePanel squarePanel = new SquarePanel(row, col);
                squares[row][col] = squarePanel;
                this.add(squarePanel);
                SquareMouseListener squareMouseListener = new SquareMouseListener(squarePanel);
                mouseListenerList.add(squareMouseListener);
                squarePanel.addMouseListener(squareMouseListener);
            }
        }
        setVisible(true);
        validate();
    }
    private void movePiece(SquarePanel source, SquarePanel target) {
        if (!source.isOccupied()) return;
        else if (target.isOccupied()) target.removePiece();
        target.placePiece(source.getPiece());
        source.removePiece();
    }

    public void enableMouseListeners() {
        mouseListenersEnabled = true;
    }

    public void disableMouseListeners() {
        mouseListenersEnabled = false;
    }

    public void setSquareMouseListenerActive(int row, int col, boolean active) {
        for (SquareMouseListener ml : mouseListenerList) {
            if((row == ml.getSquarePanel().getRow()) && (col == ml.getSquarePanel().getCol())) {
                ml.setActive(active);
            }
        }
    }

    public HashMap<String, JLabel> getNotationToJLMap() {
        return notationToJLMap;
    }

    public SquarePanel[][] getSquares() {
        return squares;
    }


    public static class SquarePanel extends JPanel {
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

        public void placePiece(JLabel pieceJL) {
            JLabel newPieceJL = BoardUtils.cloneIconJL(pieceJL);
            this.removePiece();
            piece = newPieceJL;
            this.add(newPieceJL);
            validate();
        }

        public void removePiece() {
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

        public void toggleHighlight() {
            if(this.getBorder() != null) this.setBorder(null);
            else this.setBorder(HIGHLIGHTER);
        }
    }

    public class SquareMouseListener implements MouseListener {

        private final SquarePanel squarePanel;
        private boolean active;

        public SquareMouseListener(SquarePanel squarePanel) {
            this.squarePanel = squarePanel;
            this.active = true;
        }

        public SquarePanel getSquarePanel() {
            return squarePanel;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            boolean valid;

            if (SwingUtilities.isLeftMouseButton(e)) {
                if (!mouseListenersEnabled) return;
                //if(!active) return;

                if (sourceRow < 0 || sourceCol < 0) {
                    if (!squarePanel.isOccupied()) return;
                    sourceRow = squarePanel.getRow();
                    sourceCol = squarePanel.getCol();
                    client.highlightMovementPattern(sourceRow, sourceCol);
                    squares[sourceRow][sourceCol].toggleHighlight();
                    targetRow = targetCol = -1;

                } else {
                    targetRow = squarePanel.getRow();
                    targetCol = squarePanel.getCol();

                    if(sourceRow == targetRow && sourceCol == targetCol){
                        client.highlightMovementPattern(sourceRow, sourceCol);
                        squares[sourceRow][sourceCol].toggleHighlight();
                        sourceRow = sourceCol = targetRow = targetCol = -1;
                        return;
                    }
                    //valid = gameLogic.moveValid(sourceRow, sourceCol, targetRow, targetCol, gameLogic.getModel().getMap().getMap());
                    //valid = någotfrånServern.isValid() ???????¤¤¤¤¤¤¤
                    Move move = new Move(sourceRow, sourceCol, targetRow, targetCol);
                    move.setUsername(client.getUsername());
                    client.sendMove(move);



                        //movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);


                        //gameLogic.highlightMovementPattern(sourceRow,sourceCol); //turns off highlights
                        //gameLogic.update(sourceRow,sourceCol,targetRow,targetCol); //update view
                    sourceRow = sourceCol = targetRow = targetCol = -1;
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            if(squares[i][j].getBorder() != null) squares[i][j].setBorder(null);
                        }
                    }
                        //todo make next turn if this runs

                }
            } else if (SwingUtilities.isRightMouseButton(e) && !(sourceRow == -1 && sourceCol == -1)) {
                squares[sourceRow][sourceCol].toggleHighlight();
                sourceRow = sourceCol = targetRow = targetCol = -1;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    }

    public void updateMoveValid(int sRow, int sCol) {
        squares[sRow][sCol].toggleHighlight();
        client.highlightMovementPattern(sRow, sCol);
    }

}




