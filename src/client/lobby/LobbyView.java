package client.lobby;

import client.Client;

import javax.swing.*;
import java.awt.*;

/**
 * LobbyView: Creates a GUI for the lobby, using the PairingPanel as well as the UserPanel and puts them on a new JFrame.
 * @version 1.0
 * @author wilmerknutas
 */
public class LobbyView{
    private static final Dimension CLIENT_DIMENSION = new Dimension(1000, 800);
    private final JFrame lobbyFrame;
    private static PairingPanel pairingPanel;
    private final UserPanel userPanel;
    private Client client;


    public LobbyView(Client client) {
        this.client = client;

        lobbyFrame = new JFrame("Chess On The Go - Lobby");
        init();

        pairingPanel = new PairingPanel(this);
        pairingPanel.setBounds(7, 25, 650, 650);
        pairingPanel.setBackground(Color.LIGHT_GRAY);

        userPanel = new UserPanel(this);
        userPanel.setBounds(680, 25, 300, 650);
        userPanel.setBackground(Color.LIGHT_GRAY);

        lobbyFrame.add(pairingPanel);
        lobbyFrame.add(userPanel);

        lobbyFrame.setVisible(true);
    }

    public int getTimeControl() {
        return pairingPanel.getTimeIndex();
    }

    public UserPanel getUserPanel() {
        return userPanel;
    }

    public Client getClient() {
        return client;
    }

    private void init() {
        lobbyFrame.setLayout(null);
        lobbyFrame.getContentPane().setSize(CLIENT_DIMENSION);
        lobbyFrame.setBounds(new Rectangle(CLIENT_DIMENSION));
        lobbyFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        lobbyFrame.setResizable(true);
        lobbyFrame.setLocationRelativeTo(null);
    }

    public void restartClient() {
        this.lobbyFrame.dispose();
        this.client.disconnect();
        this.client = new Client();
    }
    public void diposeFrame(){
        this.lobbyFrame.dispose();
    }
}
