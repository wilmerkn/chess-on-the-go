package client.lobby;

import client.gameview.GameView;
import server.controller.GameLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class UserPanel extends JPanel implements ActionListener {
    JLabel userLabel;
    JList<String> userList;
    JButton challengeButton;
    JButton profileButton;
    LobbyView lobbyView;

    DefaultListModel listModel;

    public UserPanel(LobbyView lobbyView) {
        this.lobbyView = lobbyView;
        this.setLayout(null);

        userLabel = new JLabel("Online users");
        userList = new JList<>();
        challengeButton = new JButton("Challenge");
        profileButton = new JButton("View profile");

        userLabel.setBounds(85, 20, 150, 100);
        userLabel.setFont(new Font("Helvetica", Font.BOLD, 20));
        this.add(userLabel);

        userList.setBounds(50, 100, 200, 400);
        add(userList);

        challengeButton.setBounds(85, 530, 150, 40);
        add(challengeButton);
        challengeButton.setFont(new Font("Helvetica", Font.BOLD, 15));

        profileButton.setBounds(85, 570, 150, 40);
        add(profileButton);
        profileButton.setFont(new Font("Helvetica", Font.BOLD, 15));

        initListeners();
        this.setVisible(true);
    }

    private void initListeners() {
        this.challengeButton.addActionListener(this);
        this.profileButton.addActionListener(this);
    }

    //these buttons should start a game, but with a correct constructor that sends in an int, which is time and both players.
    // Right now the selected time is printed, need to add the users. Need the game constructor to have both those requirements first.
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == challengeButton) {
            String receiverUsername = (String) userList.getSelectedValue();

            int timeControl = lobbyView.getTimeControl();
            if (timeControl != 0 && receiverUsername != null) {
                lobbyView.getClient().challenge(receiverUsername, timeControl);
            }
            else {
                JOptionPane.showMessageDialog(null, "Either time control or user you want to challenge isn't selected.");
            }
        }
        if (e.getSource() == profileButton) {
            //should check selected profile, maybe pop up with stats? waiting for JList with users to have some testdata
        }

    }

    public JList getUserList() {
        return userList;
    }

    public void setOnlinePlayers(ArrayList<String> players) {
        System.out.println("New playersOnlineList " + players.size());
        userList.removeAll();
        System.out.println("Length " + players.size());
        listModel = new DefaultListModel();
        for (int i = 0; i < players.size(); i++)
        {
            listModel.addElement(players.get(i));
        }

        userList.setModel(listModel);
        repaint();
        //userList.add(players.toArray());
    }
}
