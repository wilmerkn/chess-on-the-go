/**
 /CheckmateWindow: implements ActionListener
 /@version 1.0
 /*@author mirkosmiljanic
 */
package server.model;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class CheckmateWindow implements ActionListener {

    private JFrame frame;

    public CheckmateWindow(String winner){
        frame = new JFrame("Checkmate!");
        JOptionPane.showMessageDialog(frame, winner + " WON!");
    }

    //action performed method
    @Override
    public void actionPerformed(ActionEvent e) {
        frame.dispose();
    }
}
