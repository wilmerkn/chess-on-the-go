package client.gameview;

import client.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ChatPanel extends JPanel {

    JList<String> chatJL;
    JTextField messageTF;
    JButton sendBTN;

    DefaultListModel<String> listModel;

    public ChatPanel(Client client) {

        this.setLayout(null);
        chatJL = new JList<>();
        messageTF = new JTextField();
        sendBTN = new JButton("SEND");


        chatJL.setBounds(0, 0, 225, 500);
        messageTF.setBounds(0, 520, 155, 30);
        sendBTN.setBounds(160, 520, 65, 30);

        add(chatJL);
        add(messageTF);
        add(sendBTN);

        sendBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageText = messageTF.getText();
                client.sendMessage(messageText);
                messageTF.setText("");
            }
        });

        setVisible(true);
    }

    public void setChatPanelText(ArrayList<String> messages) {
        chatJL.removeAll();
        System.out.println("Length " + messages.size());
        listModel = new DefaultListModel<>();

        for (String message : messages) {
            listModel.addElement(message);
        }

        chatJL.setModel(listModel);
        repaint();
    }
}
