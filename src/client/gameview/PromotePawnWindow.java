/**
 /PromotePawnWindow
 /@version 1.0
 /*@author mirkosmiljanic
 */


package client.gameview;
import server.Server;
import server.model.ChessPiece;
import server.model.ChessPieceColor;
import server.model.ChessPieceType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PromotePawnWindow {
    private final JButton queen;
    private final JButton rook;
    private final JButton knight;
    private final JButton bishop;
    private final JFrame frame;
    private ChessPiece chessPiece;

    public PromotePawnWindow() {
        queen = new JButton("Queen");
        rook = new JButton("Rook");
        knight = new JButton("Knight");
        bishop = new JButton("Bishop");
        frame = new JFrame("Pawn promotion");

        frame.setLayout(new GridLayout(5, 2));
        frame.setSize(500, 250);
        frame.add(queen);
        frame.add(rook);
        frame.add(knight);
        frame.add(bishop);
        frame.setVisible(true);
    }


    private boolean buttonIsSelected = false;

    public void chooseChesspiece(Server server, ChessPieceColor color) {
        while(buttonIsSelected==false) {
            queen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chessPiece = server.getChesspiece(color, ChessPieceType.QUEEN);
                    buttonIsSelected = true;
                    frame.dispose();
                }
            });

            rook.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chessPiece = server.getChesspiece(color, ChessPieceType.ROOK);
                    buttonIsSelected = true;
                    frame.dispose();

                }
            });

            bishop.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chessPiece = server.getChesspiece(color, ChessPieceType.BISHOP);
                    buttonIsSelected = true;
                    frame.dispose();

                }
            });

            knight.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chessPiece = server.getChesspiece(color, ChessPieceType.KNIGHT);
                    buttonIsSelected = true;
                    frame.dispose();

                }
            });
        }
    }

    public ChessPiece getChessPiece() {
        return chessPiece;
    }

}
