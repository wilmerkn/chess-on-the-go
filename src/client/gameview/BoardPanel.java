package client.gameview;

import server.controller.GameLogic;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

public class BoardPanel extends JPanel {
    private final static Dimension BOARD_DIMENSION = new Dimension(800, 750);
    private final static Dimension SQUARE_DIMENSION = new Dimension(15, 15);

    private final static Color LIGHT_SQUARE_COLOR = new Color(211, 230, 211);
    private final static Color DARK_SQUARE_COLOR = new Color(82, 146, 82);
    private static final Border HIGHLIGHTER = BorderFactory.createLineBorder(Color.yellow, 3);

    private int sourceRow, sourceCol, targetRow, targetCol;
    private final SquarePanel[][] squares = new SquarePanel[8][8];

    private boolean mouseListenerEnabled = true;

    private GameLogic gameLogic;

    private HashMap<String, JLabel> notationToJLMap = BoardUtils.pieceNotationToJL();

    public BoardPanel(GameLogic gameLogic) {
        super(new GridLayout(8, 8));
        setSize(BOARD_DIMENSION);

        this.gameLogic = gameLogic;

        this.sourceRow = this.sourceCol = this.targetRow = this.targetCol = -1;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                SquarePanel squarePanel = new SquarePanel(row, col);
                squares[row][col] = squarePanel;
                this.add(squarePanel);
                squarePanel.addMouseListener(createMouselistener(squares[row][col]));
            }
        }
        setVisible(true);
        validate();
        //populateBoard();
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

    private void movePiece(SquarePanel source, SquarePanel target) {
        if (!source.isOccupied()) return;
        else if (target.isOccupied()) target.removePiece();
        target.placePiece(source.getPiece());
        source.removePiece();
    }

    private void enableMouseListener() {
        mouseListenerEnabled = true;
    }

    private void disableMouseListener() {
        mouseListenerEnabled = false;
    }

    public HashMap<String, JLabel> getNotationToJLMap() {
        return notationToJLMap;
    }

    //fix this later
    private MouseListener createMouselistener(SquarePanel squarePanel) {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                boolean valid;

                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (!mouseListenerEnabled) return;
                    if (sourceRow < 0 || sourceCol < 0) {
                        if (!squarePanel.isOccupied()) return;
                        sourceRow = squarePanel.getRow();
                        sourceCol = squarePanel.getCol();
                        gameLogic.highlightMovementPattern(sourceRow,sourceCol);
                        squares[sourceRow][sourceCol].toggleHighlight();
                        targetRow = targetCol = -1;

                    } else {
                        targetRow = squarePanel.getRow();
                        targetCol = squarePanel.getCol();

                        if(sourceRow == targetRow && sourceCol == targetCol){
                            gameLogic.highlightMovementPattern(sourceRow,sourceCol);
                            squares[sourceRow][sourceCol].toggleHighlight();
                            sourceRow = sourceCol = targetRow = targetCol = -1;
                            return;
                        }
                            valid = gameLogic.moveValid(sourceRow,sourceCol,targetRow,targetCol);

                            if(valid){
                                movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                squares[sourceRow][sourceCol].toggleHighlight();
                                gameLogic.highlightMovementPattern(sourceRow,sourceCol); //turns off highlights
                                gameLogic.update(sourceRow,sourceCol,targetRow,targetCol); //update view
                                sourceRow = sourceCol = targetRow = targetCol = -1;
                                //todo make next turn if this runs
                            }
                    }

                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        };
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
}




