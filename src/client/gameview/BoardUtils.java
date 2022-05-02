package client.gameview;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BoardUtils {

    public static BufferedImage fitImageToJPanel(JPanel panel, BufferedImage image) {
        int width = (int) (panel.getWidth() * 0.9);
        int height = (int) (panel.getHeight() * 0.9);
        //ska se över varför det inte går att starta ett bräde direkt på mac

        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    public static JLabel cloneIconJL(JLabel oldJL) {
        return new JLabel(oldJL.getIcon());
    }

    public static List<String> pieceNotations() {
        List<String> notations = new ArrayList<>();
        notations.add("BB");
        notations.add("BK");
        notations.add("BN");
        notations.add("BP");
        notations.add("BQ");
        notations.add("BR");
        notations.add("WB");
        notations.add("WK");
        notations.add("WN");
        notations.add("WP");
        notations.add("WQ");
        notations.add("WR");
        return notations;
    }

    public static HashMap<String, JLabel> pieceNotationToJL() {
        HashMap<String, JLabel> pieceNotationToJL = new HashMap<>();

        for (String pieceNotation: pieceNotations()) {
            try {
                BufferedImage knightImage = ImageIO.read(new File(String.format("sprites/%s.png", pieceNotation)));
                //knightImage = BoardUtils.fitImageToJPanel(this, knightImage);
                JLabel pieceJL = new JLabel(new ImageIcon(knightImage));
                pieceNotationToJL.put(pieceNotation, pieceJL);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pieceNotationToJL;
    }
}
