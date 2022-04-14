package view.board;

import javax.swing.*;
import javax.swing.plaf.synth.ColorType;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BoardUtils {

    public static BufferedImage fitImageToJPanel(JPanel panel, BufferedImage image) {
        int width = (int) (panel.getWidth() * 0.9);
        int height = (int) (panel.getHeight() * 0.9);

        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }
}
