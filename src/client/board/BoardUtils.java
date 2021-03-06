package client.board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardUtils {

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
                JLabel pieceJL = new JLabel(new ImageIcon(knightImage));
                pieceNotationToJL.put(pieceNotation, pieceJL);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pieceNotationToJL;
    }

}
