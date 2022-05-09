package client.gameview;

import client.board.BoardUtils;
import client.board.BoardView;
import server.controller.GameLogic;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;

public class GameView {
    private static final Dimension CLIENT_DIMENSION = new Dimension(1000, 800);
    private BoardPanel boardPanel;
    private ChatPanel chatPanel;

    public GameView(GameLogic gameLogic) {
                JFrame frame = new JFrame();
                frame.setTitle("Chess On The Go");
                frame.getContentPane().setSize(CLIENT_DIMENSION);
                frame.setBounds(new Rectangle(CLIENT_DIMENSION));
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setLayout(null);
                //frame.getContentPane().setBackground(new Color(35,35,35));

                boardPanel = new BoardPanel(gameLogic);
                boardPanel.setBounds(7, 25, 650, 650);
                chatPanel = new ChatPanel();
                chatPanel.setBounds(810, 7, 175, 700);

                frame.add(boardPanel);
                frame.add(chatPanel);
                frame.setVisible(true);
                }

                public BoardPanel getBoardPanel() {
                return boardPanel;
                }
}


