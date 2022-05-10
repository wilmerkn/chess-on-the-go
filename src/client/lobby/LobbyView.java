package client.lobby;

import client.gameview.BoardPanel;
import client.gameview.ChatPanel;
import server.controller.GameLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LobbyView extends JFrame implements ActionListener {

    private static final Dimension CLIENT_DIMENSION = new Dimension(1000, 800);
    JFrame lobbyFrame;
    JPanel pairingPanel = new JPanel();
    JPanel userPanel = new JPanel();
    JButton gameButton;

    public LobbyView(){

        lobbyFrame = new JFrame("Chess On The Go - Lobby");

        pairingPanel.setBounds(100, 25, 650, 650);
        pairingPanel.setBackground(Color.WHITE);
        userPanel.setBounds(810, 7, 175, 700);
        userPanel.setBackground(Color.LIGHT_GRAY);

        lobbyFrame.add(pairingPanel);
        lobbyFrame.add(userPanel);


        this.gameButton = new JButton("Play Game");
        pairingPanel.add(gameButton);

        init();
        initListeners();
    }

    private void init(){
        lobbyFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        lobbyFrame.getContentPane().setSize(CLIENT_DIMENSION);
        lobbyFrame.setBounds(new Rectangle(CLIENT_DIMENSION));
        lobbyFrame.setResizable(false);
        lobbyFrame.setLocationRelativeTo(null);
        lobbyFrame.setVisible(true);
        lobbyFrame.setLayout(null);


    }

    private void initListeners() {
        this.gameButton.addActionListener(this);

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == gameButton) {
            //depending on which time control is selected when user presses "play game", send different info to gameLogic constructor
            //implement when class Challenge is created
            GameLogic gameLogic = new GameLogic();

        }


    }

}
