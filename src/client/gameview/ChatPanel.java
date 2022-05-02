package client.gameview;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {

    public ChatPanel() {
        this.setLayout(new FlowLayout());

        JList<String> textJL = new JList<>();
        //textField.setSize(200,1000);
        textJL.setBounds(0,0,100,1000);

        JTextArea textArea = new JTextArea();
        textArea.setBounds(0, 1000, 100, 100);

        JButton sendBTN = new JButton("Send");
        add(textJL);
        add(textArea);
        add(sendBTN);
        setVisible(true);
    }
}
