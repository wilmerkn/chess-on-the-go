package client.board;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

public class BoardView {
    private final static Dimension CLIENT_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_DIMENSION = new Dimension(400, 300);
    private final static Dimension SQUARE_DIMENSION = new Dimension(15, 15);
    private final static Color LIGHT_SQUARE_COLOR = new Color(211, 230, 211);
    private final static Color DARK_SQUARE_COLOR = new Color(82, 146, 82);
    private static final Border HIGHLIGHTER = BorderFactory.createLineBorder(Color.yellow, 3);

    private final BoardPanel boardPanel;
    private boolean mouseListenerEnabled;
    private final HashMap<String, JLabel> notationToJLMap = BoardUtils.pieceNotationToJL();
    private GameLogic gameLogic;


    public BoardView() {
        this.boardPanel = new BoardPanel();
        JFrame frame = new JFrame();
        frame.setTitle("Chess On The Go");
        frame.setSize(CLIENT_DIMENSION);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.add(boardPanel, BorderLayout.CENTER);

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
                    boolean valid;

                    if(SwingUtilities.isLeftMouseButton(e))  {
                        if(!mouseListenerEnabled) return;
                        if(sourceRow < 0 || sourceCol < 0) {
                            if(!squarePanel.isOccupied()) return;
                            sourceRow = squarePanel.getRow();
                            sourceCol = squarePanel.getCol();
                            //gameLogic.highlightMovementPattern(sourceRow,sourceCol);
                            squares[sourceRow][sourceCol].toggleHighlight();
                            targetRow = targetCol = -1;
                            //System.out.println("row "+sourceRow);
                            //System.out.println("col "+sourceCol);

                        } else {
                            targetRow = squarePanel.getRow();
                            targetCol = squarePanel.getCol();

                            valid = false; //gameLogic.moveValid(sourceRow,sourceCol,targetRow,targetCol, gameLogic.getModel().getMap().getMap());
                            if(valid){
                                movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                squares[sourceRow][sourceCol].toggleHighlight();
                                //gameLogic.highlightMovementPattern(sourceRow,sourceCol); //turns off highlights
                                //gameLogic.update(sourceRow,sourceCol,targetRow,targetCol); //update view
                                sourceRow = sourceCol = targetRow = targetCol = -1;
                                //todo make next turn if this runs
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

    public class SquarePanel extends JPanel {
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

        public void toggleHighlight() {
            if(this.getBorder() != null) this.setBorder(null);
            else this.setBorder(HIGHLIGHTER);
        }
    }
}
