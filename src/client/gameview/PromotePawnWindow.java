package client.gameview;

import server.controller.GameLogic;
import server.model.ChessPiece;
import server.model.ChessPieceAbstract;
import server.model.ChessPieceColor;
import server.model.ChessPieceType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PromotePawnWindow{
    private JButton queen, rook, knight, bishop;
    private JLabel label;
    private JFrame frame;
    private GameLogic gameLogic;



    public PromotePawnWindow() {
        System.out.println("YESSSSSSSS");
        this.gameLogic = gameLogic;
        queen = new JButton("Queen");
        rook = new JButton("Rook");
        knight = new JButton("Knight");
        bishop = new JButton("Bishop");
        frame = new JFrame("Pawn promotion");


        frame.setLayout(new GridLayout(5, 2));
        frame.setSize(500,250);
        frame.add(queen);
        frame.add(rook);
        frame.add(knight);
        frame.add(bishop);
        frame.setVisible(true);
    }


    public void chooseChesspiece(GameLogic gameLogic,ChessPieceColor color, ChessPiece cp) {
        queen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               gameLogic.getModel().getMap().getMap()[gameLogic.getLocationX(cp,  gameLogic.getModel().getMap().getMap())][gameLogic.getLocationY(cp,  gameLogic.getModel().getMap().getMap())] = gameLogic.getChesspiece(color, ChessPieceType.QUEEN);
                frame.dispose();
            }
        });

        rook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLogic.getModel().getMap().getMap()[gameLogic.getLocationX(cp,  gameLogic.getModel().getMap().getMap())][gameLogic.getLocationY(cp,  gameLogic.getModel().getMap().getMap())] = gameLogic.getChesspiece(color, ChessPieceType.ROOK);
                frame.dispose();
            }
        });

        bishop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLogic.getModel().getMap().getMap()[gameLogic.getLocationX(cp,  gameLogic.getModel().getMap().getMap())][gameLogic.getLocationY(cp,  gameLogic.getModel().getMap().getMap())] = gameLogic.getChesspiece(color, ChessPieceType.BISHOP);
                frame.dispose();
            }
        });

        knight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLogic.getModel().getMap().getMap()[gameLogic.getLocationX(cp,  gameLogic.getModel().getMap().getMap())][gameLogic.getLocationY(cp,  gameLogic.getModel().getMap().getMap())] = gameLogic.getChesspiece(color, ChessPieceType.KNIGHT);
                frame.dispose();
            }
        });
    }

}
