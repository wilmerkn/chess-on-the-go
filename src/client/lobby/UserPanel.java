package client.lobby;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * UserPanel: Part of the LobbyView, shows online users and the option to challenge one of them
 * @version 1.0
 * @author wilmerknutas
 */

public class UserPanel extends JPanel implements ActionListener {
    JLabel userLabel;
    JList<String> userList;
    JButton challengeButton;
    LobbyView lobbyView;
    DefaultListModel listModel;

    public UserPanel(LobbyView lobbyView) {
        this.lobbyView = lobbyView;
        this.setLayout(null);

        userLabel = new JLabel("Online users");
        userList = new JList<>();
        challengeButton = new JButton("Challenge");

        userLabel.setBounds(85, 20, 150, 100);
        userLabel.setFont(new Font("Helvetica", Font.BOLD, 20));
        this.add(userLabel);

        userList.setBounds(50, 100, 200, 400);
        add(userList);

        challengeButton.setBounds(85, 530, 150, 40);
        add(challengeButton);
        challengeButton.setFont(new Font("Helvetica", Font.BOLD, 15));

        initListeners();
        this.setVisible(true);
    }

    private void initListeners() {
        this.challengeButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == challengeButton) {
            String receiverUsername = userList.getSelectedValue();
            int timeControl = lobbyView.getTimeControl();
            if (timeControl != 0 && receiverUsername != null) {
                lobbyView.getClient().challenge(receiverUsername, timeControl);
            }
            else {
                JOptionPane.showMessageDialog(null, "Please select time control and user you want to challenge.");
            }
        }
    }

    public void setOnlinePlayers(ArrayList<String> players) {
        userList.removeAll();
        System.out.println("Length " + players.size());
        listModel = new DefaultListModel();
        for (int i = 0; i < players.size(); i++)
        {
            listModel.addElement(players.get(i));
        }

        userList.setModel(listModel);
        repaint();
    }
}
