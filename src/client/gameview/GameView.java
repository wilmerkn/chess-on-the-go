package client.gameview;

import client.Client;
import client.board.BoardUtils;
import client.board.BoardView;
import client.register.RegisterView;
import server.controller.GameLogic;
import server.controller.RegisterController;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class GameView implements ActionListener{
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

        player1Time = new JLabel();
        player1Time.setFont(new Font("Sans Serif", Font.BOLD, 15));
        player1Time.setBounds(565, 700, 565, 25);

        player2Time = new JLabel();
        player2Time.setFont(new Font("Sans Serif", Font.BOLD, 15));
        player2Time.setBounds(565, 25, 565, 25);

        chatPanel = new ChatPanel();
        chatPanel.setBounds(700, 50, 225, 550);

        resignBTN = new JButton("RESIGN");
        resignBTN.setBounds(700, 675, 90, 25);
        resignBTN.addActionListener(this);

        offerDrawBTN = new JButton("OFFER DRAW");
        offerDrawBTN.setBounds(800, 675, 125, 25);
        offerDrawBTN.addActionListener(this);

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

    public void setPlayer1Name(String player1Name) {
        this.player1Name.setText(player1Name);
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name.setText(player2Name);
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public void setPlayer1Time(String player1Time) {
        this.player1Time.setText(player1Time);
    }

    public void setPlayer2Time(String player2Time) {
        this.player2Time.setText(player2Time);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == resignBTN) {
            this.client.startPlayer1Time();
        }
        if (e.getSource() == offerDrawBTN) {
            this.client.startPlayer2Time();
        }
    }
}


