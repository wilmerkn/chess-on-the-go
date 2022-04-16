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

    private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    private int res_x = (int) screen.getWidth();
    private int res_y = (int) screen.getHeight();

    //constructor
    public BoardView() {
        this.frame = new JFrame();
        this.boardPanel = new BoardPanel();
        this.frame.setTitle("Chess");
        this.frame.setSize(CLIENT_DIMENSION);
        this.frame.setLocation(((int) res_x/2 - ((int)CLIENT_DIMENSION.getWidth()/2)),((int) res_y/2 - ((int)CLIENT_DIMENSION.getHeight()/2))); //devide screen res by 2 and subtract half dimension of client window (centers the window)
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setVisible(true);
        this.frame.add(boardPanel, BorderLayout.CENTER);
    }


    //generates board and stores squares in 2D array
    private class BoardPanel extends JPanel {

        JComponent[][] buttons = new JComponent[8][8];
        int colorCount = 0;
        public BoardPanel() {
            super(new GridLayout(8, 8));
            for (int row = 0; row < 8; row++){
                for(int col = 0; col < 8; col++){
                    SquarePanel squarePanel = new SquarePanel();
                    squarePanel.assignColor(colorCount);
                    buttons[row][col] = squarePanel;
                    this.add(squarePanel);
                    colorCount++;
                }
            }

            setPreferredSize(BOARD_DIMENSION);
            setVisible(true);
            validate();
        }
    }

    //panel for the squares
    private class SquarePanel extends JPanel {

        public SquarePanel () {

            setPreferredSize(SQUARE_DIMENSION);
            setVisible(true);
        }

        //draws board with chess color pattern
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
