package view.board;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BoardView {
    private final static Dimension CLIENT_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_DIMENSION = new Dimension(400, 300);
    private final static Dimension SQUARE_DIMENSION = new Dimension(15, 15);
    private final static Color LIGHT_SQUARE_COLOR = new Color(211, 230, 211);
    private final static Color DARK_SQUARE_COLOR = new Color(82, 146, 82);
    private static final Border HIGHLIGHTER = BorderFactory.createLineBorder(Color.yellow, 3);

    public BoardView() {
        BoardPanel boardPanel = new BoardPanel();
        JFrame frame = new JFrame();
        frame.setTitle("Chess");
        frame.setSize(CLIENT_DIMENSION);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(boardPanel, BorderLayout.CENTER);
        boardPanel.populateBoard();
    }

    private class BoardPanel extends JPanel {
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
                    squarePanel.addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) { }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            if(SwingUtilities.isLeftMouseButton(e))  {
                                if(sourceRow < 0 || sourceCol < 0) {
                                    sourceRow = squarePanel.getRow();
                                    sourceCol = squarePanel.getCol();
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    targetRow = targetCol = -1;
                                } else {
                                    targetRow = squarePanel.getRow();
                                    targetCol = squarePanel.getCol();
                                    movePiece(squares[sourceRow][sourceCol], squares[targetRow][targetCol]);
                                    squares[sourceRow][sourceCol].toggleHighlight();
                                    sourceRow = sourceCol = targetRow = targetCol = -1;
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
                    });
                }
            }
        }

        private void movePiece(SquarePanel source, SquarePanel target) {
            if(target.isOccupied()) target.removePiece();
            target.placePiece(source.getPiece());
            source.removePiece();
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
            piece = null;
            revalidate();
            repaint();
        }

        private JLabel getPiece() {
            return piece;
        }

        private boolean isOccupied() {
            return piece != null;
        }

        private void toggleHighlight() {
            if(this.getBorder() != null) {
                this.setBorder(null);
            } else this.setBorder(HIGHLIGHTER);
        }
    }
}
