package view.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

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
    }


    private class BoardPanel extends JPanel {

        JComponent[][] squares = new JComponent[8][8];
        public BoardPanel() {
            super(new GridLayout(8, 8));
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
    }

    private class SquarePanel extends JPanel {

        private final int row;
        private final int col;

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
    }
}
