package view.board;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


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

        JButton[][] buttons = new JButton[8][8];
        int colorCount = 0;
        public BoardPanel() {
            super(new GridLayout(8, 8));
            for (int row = 0; row < 8; row++){
                for(int col = 0; col < 8; col++){
                    SquarePanel squarePanel = new SquarePanel();
                    squarePanel.assignColor(colorCount);
                    buttons[row][col].add(squarePanel);
                    this.add(squarePanel);
                    colorCount++;
                }
            }







            setPreferredSize(BOARD_DIMENSION);
            setVisible(true);
            validate();
        }

        /*
        public BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardSquares =  new ArrayList<>();
            for (int i = 0; i < 64; i++) {
                SquarePanel squarePanel = new SquarePanel(i);
                squarePanel.assignColor(i);
                this.boardSquares.add(squarePanel);
                this.add(squarePanel);
            }


            setPreferredSize(BOARD_DIMENSION);
            setVisible(true);
            validate();
        }
        */

    }

    private class SquarePanel extends JPanel {

        public SquarePanel () {

            setPreferredSize(SQUARE_DIMENSION);
            setVisible(true);
        }

        public void assignColor(int squareID) {
            int row = squareID/8;
            if(squareID % 2 == 0 ) {
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
