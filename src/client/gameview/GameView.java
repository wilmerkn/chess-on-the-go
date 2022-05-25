package client.gameview;

import client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameView implements ActionListener{
    private static final Dimension CLIENT_DIMENSION = new Dimension(1000, 800);
    private BoardPanel boardPanel;
    private ChatPanel chatPanel;

    private JLabel myName;
    private JLabel myTime;
    private JLabel opponentName;
    private JLabel opponentTime;

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
        frame.setResizable(true);
        frame.setLayout(null);
        //frame.getContentPane().setBackground(new Color(35,35,35));

        boardPanel = new BoardPanel(client);
        boardPanel.setBounds(7, 50, 650, 650);

        myName = new JLabel("Player 1");
        myName.setFont(new Font("Sans Serif", Font.BOLD, 15));
        myName.setBounds(15, 700, 565, 25);

        opponentName = new JLabel("Player 2");
        opponentName.setFont(new Font("Sans Serif", Font.BOLD, 15));
        opponentName.setBounds(15, 25, 565, 25);

        myTime = new JLabel();
        myTime.setFont(new Font("Sans Serif", Font.BOLD, 15));
        myTime.setBounds(565, 700, 565, 25);

        opponentTime = new JLabel();
        opponentTime.setFont(new Font("Sans Serif", Font.BOLD, 15));
        opponentTime.setBounds(565, 25, 565, 25);

        chatPanel = new ChatPanel();
        chatPanel.setBounds(700, 50, 225, 550);

        resignBTN = new JButton("RESIGN");
        resignBTN.setBounds(700, 675, 90, 25);
        resignBTN.addActionListener(this);

        offerDrawBTN = new JButton("OFFER DRAW");
        offerDrawBTN.setBounds(800, 675, 125, 25);
        offerDrawBTN.addActionListener(this);

        frame.add(myName);
        frame.add(opponentName);

        frame.add(myTime);
        frame.add(opponentTime);

        frame.add(resignBTN);
        frame.add(offerDrawBTN);

        frame.add(boardPanel);
        frame.add(chatPanel);
        frame.setVisible(true);
    }

    public void setMyName(String myName) {
        this.myName.setText(myName);
    }

    public void setOpponentName(String opponentName) {
        this.opponentName.setText(opponentName);
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public void setMyTime(String myTime) {
        this.myTime.setText(myTime);
    }

    public void setOpponentTime(String opponentTime) {
        this.opponentTime.setText(opponentTime);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == resignBTN) {
            this.client.startMyTime();
        }
        if (e.getSource() == offerDrawBTN) {
            this.client.startOpponentTime();
        }
    }
}


