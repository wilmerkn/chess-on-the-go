package client.lobby;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * PairingPanel: Part of the LobbyView, shows different options for time controls as well as the option to log out.
 * @version 1.0
 * @author wilmerknutas
 */
public class PairingPanel extends JPanel implements ActionListener {


    private final JButton oneMinuteButton;
    private final JButton threeMinuteButton;
    private final JButton fiveMinuteButton;
    private final JButton tenMinuteButton;
    private final JButton logOutButton;

    int timeControl = 0;

    LobbyView lobbyView;

    public PairingPanel(LobbyView lobbyView) {
        this.lobbyView = lobbyView;
        this.setLayout(null);

        JLabel pairingLabel = new JLabel("Quick pairing");

        oneMinuteButton = new JButton("1 min");
        threeMinuteButton = new JButton("3 min");
        fiveMinuteButton = new JButton("5 min");
        tenMinuteButton = new JButton("10 min");
        logOutButton = new JButton("Log out");

        pairingLabel.setBounds(235, 20, 200, 50);
        pairingLabel.setFont(new Font("Helvetica", Font.BOLD, 25));
        add(pairingLabel);

        logOutButton.setBounds(25, 575, 150, 50);
        logOutButton.setFont(new Font("Helvetica", Font.BOLD, 15));
        add(logOutButton);

        oneMinuteButton.setBounds(120, 100, 200, 200);
        oneMinuteButton.setFont(new Font("Helvetica", Font.BOLD, 20));
        add(oneMinuteButton);

        threeMinuteButton.setBounds(325, 100, 200, 200);
        threeMinuteButton.setFont(new Font("Helvetica", Font.BOLD, 20));
        add(threeMinuteButton);

        fiveMinuteButton.setBounds(120, 300, 200, 200);
        fiveMinuteButton.setFont(new Font("Helvetica", Font.BOLD, 20));
        add(fiveMinuteButton);

        tenMinuteButton.setBounds(325, 300, 200, 200);
        tenMinuteButton.setFont(new Font("Helvetica", Font.BOLD, 20));
        add(tenMinuteButton);

        initListeners();
        this.setVisible(true);
    }

    private void initListeners() {
        this.oneMinuteButton.addActionListener(this);
        this.threeMinuteButton.addActionListener(this);
        this.fiveMinuteButton.addActionListener(this);
        this.tenMinuteButton.addActionListener(this);
        this.logOutButton.addActionListener(this);
    }

    public int getTimeIndex() {
        if (timeControl != 0) {
            return timeControl;
        }
        timeControl = 0;
        return timeControl;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == oneMinuteButton) {
            timeControl = 1;
        }
        if (e.getSource() == threeMinuteButton) {
            timeControl = 3;
        }
        if (e.getSource() == fiveMinuteButton) {
            timeControl = 5;
        }
        if (e.getSource() == tenMinuteButton) {
            timeControl = 10;
        }
        if (e.getSource() == logOutButton) {
            lobbyView.restartClient();

        }
    }
}
