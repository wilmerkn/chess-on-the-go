package client.gameview;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MoveKing implements ActionListener{
    private final JFrame frame;

    public MoveKing(){
        frame = new JFrame("Check!");
        JOptionPane.showMessageDialog(frame,"King is in check! Move the king or eliminate the enemy!");
    }

    //action performed method
    @Override
    public void actionPerformed(ActionEvent e) {
        frame.dispose();
    }
}

