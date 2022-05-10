package client.gameview;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {

    public ChatPanel() {

        this.setLayout(null);
        JTextArea chatTA = new JTextArea();
        JTextField messageTF = new JTextField();
        JButton sendBTN = new JButton("SEND");


        chatTA.setBounds(0, 0, 225, 450);
        messageTF.setBounds(0, 460, 155, 30);
        sendBTN.setBounds(160, 460, 65, 30);

        add(chatTA);
        add(messageTF);
        add(sendBTN);

        setVisible(true);
    }
}
