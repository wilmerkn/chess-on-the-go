package client.board;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

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
}
