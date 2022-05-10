package client.lobby;

import client.board.BoardView;
import client.gameview.GameView;
import client.register.RegisterView;
import server.controller.GameLogic;
import server.controller.LoginController;
import server.controller.RegisterController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LobbyView extends JFrame implements ActionListener {
    JFrame lobbyFrame;
    JPanel pairingPanel = new JPanel();
    JPanel userPanel = new JPanel();
    JButton gameButton;

    public LobbyView(){

        lobbyFrame = new JFrame("Chess On The Go - Login");

        lobbyFrame.add(pairingPanel);
        pairingPanel.setBackground(Color.LIGHT_GRAY);

        lobbyFrame.add(userPanel);
        userPanel.setBackground(Color.LIGHT_GRAY);

        this.gameButton = new JButton("Play Game");
        pairingPanel.add(gameButton);

        init();
        initListeners();
    }

    private void init(){
        lobbyFrame.setLayout(new GridLayout(2,2));
        lobbyFrame.setSize(600,600);
        lobbyFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        lobbyFrame.setResizable(false);
        lobbyFrame.setLocationRelativeTo(null);
        lobbyFrame.setVisible(true);
    }

    private void initListeners() {
        this.gameButton.addActionListener(this);
        //this.*******.addActionListener(this);
        //this.*******.addActionListener(this);
        //this.*******.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == gameButton) {
            //GameView gameView = new GameView();
            GameLogic gameLogic = new GameLogic();
        }


    }

}
