package client.gameview;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {

    public ChatPanel() {
        //this.setLayout(null);
        this.setBackground(Color.gray);
        JList<String> textJL = new JList<>();
        //textField.setSize(200,1000);
        textJL.setBounds(20, 20, 815, 450);
        textJL.add(new JLabel("ett, ett, ett"));
        textJL.add(new JLabel("två, ett, ett"));
        textJL.add(new JLabel("tre, ett, ett"));
        textJL.add(new JLabel("tre, ett, ett"));

        JTextArea textArea = new JTextArea();
        textArea.setBounds(20, 250, 200, 100);

        JButton sendBTN = new JButton("Send");
        add(textJL);
        add(textArea);
        add(sendBTN);

        setVisible(true);
    }
}
