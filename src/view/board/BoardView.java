package view.board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.event.MouseEvent.BUTTON1;


public class BoardView {

    private final JFrame frame;
    private final BoardPanel boardPanel;
    private final static Dimension CLIENT_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_DIMENSION = new Dimension(400, 300);
    private final static Dimension SQUARE_DIMENSION = new Dimension(15, 15);

    public BoardView() {

        this.frame = new JFrame();
        this.boardPanel = new BoardPanel();

        this.frame.setTitle("Chess");
        this.frame.setSize(CLIENT_DIMENSION);
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setVisible(true);
        this.frame.add(boardPanel, BorderLayout.CENTER);
        boardPanel.populateBoard();
    }


    private class BoardPanel extends JPanel {

        private int sourceRow, sourceCol, targetRow, targetCol;
        SquarePanel[][] squares = new SquarePanel[8][8];

        public BoardPanel() {
            super(new GridLayout(8, 8));
            sourceRow = sourceCol = targetRow = targetCol = -1;

            for (int row = 0; row < 8; row++){
                for(int col = 0; col < 8; col++){
                    SquarePanel squarePanel = new SquarePanel(row, col);
                    squares[row][col] = squarePanel;
                    this.add(squarePanel);
                    squarePanel.addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if(e.getButton() == BUTTON1)  {
                                System.out.println("BUTTON1 clicked: " + "Row: " + squarePanel.getRow() + " Col: " + squarePanel.getCol());

                                if(sourceRow < 0 || sourceCol < 0) {
                                    sourceRow = squarePanel.getRow();
                                    sourceCol = squarePanel.getCol();
                                    System.out.println("Source: " + sourceRow + " " + sourceCol);
                                } else {
                                    targetRow = squarePanel.getRow();
                                    targetCol = squarePanel.getCol();
                                    System.out.println("Target: " + sourceRow + " " + sourceCol);
                                    movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                    sourceRow = -1;
                                    sourceCol = -1;
                                }
                            }
                        }

                        @Override
                        public void mousePressed(MouseEvent e) {

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
                    });
                }
            }

            setPreferredSize(BOARD_DIMENSION);
            setVisible(true);
            validate();
        }

        private void movePiece(SquarePanel source, SquarePanel target) {
            JLabel piece = source.getPiece();
            if(piece != null) {
                target.placePiece(piece);
                source.removePiece();
            }
        }


        public void populateBoard() {
            for (int i = 0; i < 8; i++) {
                squares[0][i].placePiece(null);
                squares[1][i].placePiece(null);
                squares[6][i].placePiece(null);
                squares[7][i].placePiece(null);
            }
        }
    }

    private class SquarePanel extends JPanel {

        private final int row;
        private final int col;

        private JLabel piece;

        public SquarePanel (int row, int col) {
            this.row = row;
            this.col = col;
            assignColor();
            setPreferredSize(SQUARE_DIMENSION);
            setVisible(true);
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public void assignColor() {
            if(col % 2 == 0 ) {
                if(row % 2 == 0) {
                    setBackground(Color.CYAN);
                } else {
                    setBackground(Color.BLUE);
                }
            } else {
                if(row % 2 != 0) {
                    setBackground(Color.CYAN);
                } else {
                    setBackground(Color.BLUE);
                }
            }
        }

        private void placePiece(JLabel pieceToMove) {
            try {
                BufferedImage knightImage = ImageIO.read(new File("sprites/Chess-Knight.png"));
                knightImage = BoardUtils.fitImageToJPanel(this, knightImage);
                piece = new JLabel(new ImageIcon(knightImage));
                this.add(piece);

            } catch (IOException e) {
                e.printStackTrace();
            }
            validate();
        }

        private void removePiece() {
            this.remove(piece);
            revalidate();
            repaint();
        }

        public JLabel getPiece() {
            return piece;
        }
    }
}
