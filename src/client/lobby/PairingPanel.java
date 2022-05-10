package client.lobby;

import server.controller.GameLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PairingPanel extends JPanel implements ActionListener {
    public PairingPanel(){
        this.setLayout(null);
        //1 JLabel "quick pairing"
        //4 buttons "1 min", "3 min", "5 min", "10 min".
        //1 button "Log out"
        JLabel pairingLabel = new JLabel("Quick pairing");
        //rename buttons
        JButton oneMinuteButton = new JButton("1 min");
        JButton threeMinuteButton = new JButton("3 min");
        JButton fiveMinuteButton = new JButton("5 min");
        JButton tenMinuteButton = new JButton("10 min");
        JButton logOutButton = new JButton("Log out");

        pairingLabel.setBounds(235,20,200,50);
        pairingLabel.setFont(new Font("Helvetica", Font.BOLD, 25));
        add(pairingLabel);

        logOutButton.setBounds(25,575,150,50);
        logOutButton.setFont(new Font("Helvetica", Font.BOLD, 15));
        add(logOutButton);

        oneMinuteButton.setBounds(120,100,200,200);
        oneMinuteButton.setFont(new Font("Helvetica", Font.BOLD, 20));
        add(oneMinuteButton);

        threeMinuteButton.setBounds(325,100,200,200);
        threeMinuteButton.setFont(new Font("Helvetica", Font.BOLD, 20));
        add(threeMinuteButton);

        fiveMinuteButton.setBounds(120,300,200,200);
        fiveMinuteButton.setFont(new Font("Helvetica", Font.BOLD, 20));
        add(fiveMinuteButton);

        tenMinuteButton.setBounds(325,300,200,200);
        tenMinuteButton.setFont(new Font("Helvetica", Font.BOLD, 20));
        add(tenMinuteButton);


        this.setVisible(true);
    }
    private void initListeners() {
        //this.oneMinuteButton.addActionListener(this);
        //this.threeMinuteButton.addActionListener(this);
        //this.fiveMinuteButton.addActionListener(this);
        //this.tenMinuteButton.addActionListener(this);

    }
    @Override
    public void actionPerformed(ActionEvent e) {
       // if (e.getSource() == oneMinuteButton) {

       // }


    }

}
