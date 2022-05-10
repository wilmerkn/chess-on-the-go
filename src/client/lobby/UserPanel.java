package client.lobby;

import javax.swing.*;
import java.awt.*;

public class UserPanel extends JPanel{

    public UserPanel() {
        this.setLayout(null);

        //1 JLabel "Online users"
        //1 JList should show online users
        //2 JButtons "Challenge", "View Profile"
        JLabel userLabel = new JLabel("Online users");
        JList userList = new JList();
        JButton challengeButton = new JButton("Challenge");
        JButton profileButton = new JButton("View profile");

//        userPanel.setBounds(680, 25, 300, 650);
        userLabel.setBounds(85,20,150,100);
        userLabel.setFont(new Font("Helvetica", Font.BOLD, 20));
        this.add(userLabel);

        userList.setBounds(50,100,200, 400);
        add(userList);

        challengeButton.setBounds(85,530,150,40);
        add(challengeButton);
        challengeButton.setFont(new Font("Helvetica", Font.BOLD, 15));

        profileButton.setBounds(85,570,150,40);
        add(profileButton);
        profileButton.setFont(new Font("Helvetica", Font.BOLD, 15));



        this.setVisible(true);
    }
}
