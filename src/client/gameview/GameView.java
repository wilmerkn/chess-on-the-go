package client.gameview;

import client.board.BoardUtils;
import client.board.BoardView;
import server.controller.GameLogic;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;

public class GameView {
    private final static Dimension CLIENT_DIMENSION = new Dimension(1000, 800);

    JLabel p1NameJL;
    JLabel p2NameJL;
    JLabel p1TimeJL;
    JLabel p2TimeJL;
    BoardPanel boardPanel;
    ChatPanel chatPanel;
    JButton resignBTN;
    JButton offerDrawBTN;

    public GameView() {
        JFrame frame = new JFrame();
        frame.setTitle("Chess On The Go");
        frame.setSize(CLIENT_DIMENSION);
        frame.setBounds(new Rectangle(CLIENT_DIMENSION));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);
        //frame.getContentPane().setBackground(new Color(35,35,35));

        p1NameJL = new JLabel("Spelare 1");
        p2NameJL = new JLabel("Spelare 2");

        p1TimeJL = new JLabel("Time: " + "0:46");
        p2TimeJL = new JLabel("Time: " + "0:13");

        boardPanel = new BoardPanel();
        chatPanel = new ChatPanel();

        resignBTN = new JButton("Resign");
        offerDrawBTN = new JButton("Offer draw");


        p1NameJL.setBounds(85, 50, 100, 25);
        p1NameJL.setFont(new Font("Serif", Font.PLAIN, 20));

        p2NameJL.setBounds(85, 675, 100, 25);
        p2NameJL.setFont(new Font("Serif", Font.PLAIN, 20));

        p1TimeJL.setBounds(580, 50, 100, 25);
        p1TimeJL.setFont(new Font("Serif", Font.PLAIN, 20));

        p2TimeJL.setBounds(580, 675, 100, 25);
        p2TimeJL.setFont(new Font("Serif", Font.PLAIN, 20));

        boardPanel.setBounds(75, 75, 600, 600);
        chatPanel.setBounds(725, 75, 225, 520);

        resignBTN.setBounds(725, 600, 100,30);
        offerDrawBTN.setBounds(830, 600, 120,30);

        frame.add(p1NameJL);
        frame.add(p2NameJL);
        frame.add(p1TimeJL);
        frame.add(p2TimeJL);
        frame.add(boardPanel);
        frame.add(chatPanel);
        frame.add(resignBTN);
        frame.add(offerDrawBTN);
        frame.setVisible(true);
    }

    public void setP1NameJL(JLabel p1NameJL) {
        this.p1NameJL = p1NameJL;
    }

    public void setP2NameJL(JLabel p2NameJL) {
        this.p2NameJL = p2NameJL;
    }

    public void setP1TimeJL(JLabel p1TimeJL) {
        this.p1TimeJL = p1TimeJL;
    }

    public void setP2TimeJL(JLabel p2TimeJL) {
        this.p2TimeJL = p2TimeJL;
    }


}
