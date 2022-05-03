package client.gameview;

import client.board.BoardUtils;
import client.board.BoardView;
import server.controller.GameLogic;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;

public class GameView {
    private final static Dimension CLIENT_DIMENSION = new Dimension(1000, 800);

    public GameView() {
        JFrame frame = new JFrame();
        frame.setTitle("Chess On The Go");
        frame.setSize(CLIENT_DIMENSION);
        frame.setBounds(new Rectangle(CLIENT_DIMENSION));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);
        //frame.getContentPane().setBackground(new Color(35,35,35));

        BoardPanel boardPanel = new BoardPanel();
        boardPanel.setBounds(7, 25, 650, 650);
        ChatPanel chatPanel = new ChatPanel();
        chatPanel.setBounds(810, 7, 175, 700);

        frame.add(boardPanel);
        frame.add(chatPanel);
        frame.setVisible(true);

    }
}
