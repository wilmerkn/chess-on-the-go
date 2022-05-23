package client.gameview;

import client.Client;
import client.board.BoardUtils;
import client.board.BoardView;
import server.controller.GameLogic;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;

public class GameView {
    private static final Dimension CLIENT_DIMENSION = new Dimension(1000, 800);
    private BoardPanel boardPanel;
    private ChatPanel chatPanel;

    private JLabel player1Name;
    private JLabel player1Time;
    private JLabel player2Name;
    private JLabel player2Time;

    private JButton resignBTN;
    private JButton offerDrawBTN;

    private Client client;

    public GameView(Client client) {
                this.client = client;

                JFrame frame = new JFrame();
                frame.setTitle("Chess On The Go");
                frame.getContentPane().setSize(CLIENT_DIMENSION);
                frame.setBounds(new Rectangle(CLIENT_DIMENSION));
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setLayout(null);
                //frame.getContentPane().setBackground(new Color(35,35,35));

                boardPanel = new BoardPanel(client);
                boardPanel.setBounds(7, 50, 650, 650);

                player1Name = new JLabel("Player 1");
                player1Name.setFont(new Font("Sans Serif", Font.BOLD, 15));
                player1Name.setBounds(15, 700, 565, 25);

                player2Name = new JLabel("Player 2");
                player2Name.setFont(new Font("Sans Serif", Font.BOLD, 15));
                player2Name.setBounds(15, 25, 565, 25);

                player1Time = new JLabel("Time: 11:11");
                player1Time.setFont(new Font("Sans Serif", Font.BOLD, 15));
                player1Time.setBounds(565, 700, 565, 25);

                player2Time = new JLabel("Time: 22:22");
                player2Time.setFont(new Font("Sans Serif", Font.BOLD, 15));
                player2Time.setBounds(565, 25, 565, 25);

                chatPanel = new ChatPanel();
                chatPanel.setBounds(700, 50, 225, 550);

                resignBTN = new JButton("RESIGN");
                resignBTN.setBounds(700, 675, 90, 25);

                offerDrawBTN = new JButton("OFFER DRAW");
                offerDrawBTN.setBounds(800, 675, 125, 25);

                frame.add(player1Name);
                frame.add(player2Name);

                frame.add(player1Time);
                frame.add(player2Time);

                frame.add(resignBTN);
                frame.add(offerDrawBTN);

                frame.add(boardPanel);
                frame.add(chatPanel);
                frame.setVisible(true);
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
}


