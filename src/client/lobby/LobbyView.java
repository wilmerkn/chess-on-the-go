package client.lobby;

import javax.swing.*;
import java.awt.*;

public class LobbyView extends JFrame {
    private static final Dimension CLIENT_DIMENSION = new Dimension(1000, 800);
    JFrame lobbyFrame;
    private static PairingPanel pairingPanel;
    private final UserPanel userPanel;


    public LobbyView() {

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

    public static int getTimeControl() {
        return pairingPanel.getTimeIndex();
    }

    public UserPanel getUserPanel() {
        return userPanel;
    }

    private void init() {
        lobbyFrame.setLayout(null);
        lobbyFrame.getContentPane().setSize(CLIENT_DIMENSION);
        lobbyFrame.setBounds(new Rectangle(CLIENT_DIMENSION));
        lobbyFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        lobbyFrame.setResizable(false);
        lobbyFrame.setLocationRelativeTo(null);
    }


}
