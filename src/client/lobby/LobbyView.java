package client.lobby;

import client.board.BoardView;
import client.gameview.BoardPanel;
import client.gameview.ChatPanel;
import client.gameview.GameView;
import client.register.RegisterView;
import server.controller.GameLogic;
import server.controller.LoginController;
import server.controller.RegisterController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LobbyView extends JFrame{
    private static final Dimension CLIENT_DIMENSION = new Dimension(1000, 800);
    JFrame lobbyFrame;
    private PairingPanel pairingPanel;
    private UserPanel userPanel;

    public LobbyView(){

        lobbyFrame = new JFrame("Chess On The Go - Lobby");
        init();

        pairingPanel = new PairingPanel();
        pairingPanel.setBounds(7, 25, 650, 650);
        pairingPanel.setBackground(Color.LIGHT_GRAY);

        userPanel = new UserPanel();
        userPanel.setBounds(680, 25, 300, 650);
        userPanel.setBackground(Color.LIGHT_GRAY);

        lobbyFrame.add(pairingPanel);
        lobbyFrame.add(userPanel);

        lobbyFrame.setVisible(true);

    }

    private void init(){
        lobbyFrame.setLayout(null);
        lobbyFrame.getContentPane().setSize(CLIENT_DIMENSION);
        lobbyFrame.setBounds(new Rectangle(CLIENT_DIMENSION));
        lobbyFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        lobbyFrame.setResizable(false);
        lobbyFrame.setLocationRelativeTo(null);
    }



}
