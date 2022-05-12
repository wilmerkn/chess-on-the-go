package client.register;

import server.controller.RegisterController;
import server.model.Register;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;


public class RegisterView extends JFrame implements ActionListener {
    JFrame registerFrame;
    JPanel registerPanel = new JPanel();
    JPanel picturePanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JLabel pictureLabel;
    private final JTextField userText;
    private final JPasswordField passText;
    private final JTextField countryText;
    private final JLabel userLabel;
    private final JLabel countryLabel;
    private final JLabel passLabel;
    JButton registerButton;
    JButton cancelButton;

    ImageIcon image;
    ImageIcon resizedImage;
    Image scaledImage;
    private final RegisterController registerController;


    public RegisterView(RegisterController registerController) {
        registerFrame = new JFrame("Chess On The Go - Registration");

        this.registerController = registerController;
        try {
            BufferedImage knightImage = ImageIO.read(new File("sprites/Chess-Knight.png"));
            scaledImage = knightImage.getScaledInstance(100, 100, Image.SCALE_DEFAULT);
            resizedImage = new ImageIcon(scaledImage);
            pictureLabel = new JLabel(resizedImage);
            picturePanel.add(pictureLabel);

        } catch (Exception e) {
            System.out.println("Can't find image for login");
        }

        registerPanel.setLayout(new FlowLayout());
        this.userLabel = new JLabel("Username:");
        registerPanel.add(userLabel);

        this.userText = new JTextField(20);
        registerPanel.add(userText);

        this.passLabel = new JLabel("Password:");
        registerPanel.add(passLabel);

        this.passText = new JPasswordField(20);
        registerPanel.add(passText);

        this.countryLabel = new JLabel("Country:");
        registerPanel.add(countryLabel);

        this.countryText = new JTextField(20);
        registerPanel.add(countryText);

        this.registerButton = new JButton("Register");
        buttonPanel.add(registerButton);

        this.cancelButton = new JButton("Cancel");
        buttonPanel.add(cancelButton);


        init();
        initListeners();

        registerFrame.add(picturePanel);
        registerFrame.add(registerPanel);
        registerFrame.add(buttonPanel);

        picturePanel.setBackground(Color.LIGHT_GRAY);
        registerPanel.setBackground(Color.LIGHT_GRAY);
        buttonPanel.setBackground(Color.LIGHT_GRAY);


    }

    private void init() {
        registerFrame.setLayout(new GridLayout(3, 1));
        registerFrame.setSize(300, 500);
        registerFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        registerFrame.setResizable(false);
        registerFrame.setLocationRelativeTo(null);
        registerFrame.setVisible(true);
    }

    private void initListeners() {
        this.registerButton.addActionListener(this);
        this.cancelButton.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == registerButton) {
            this.registerController.registerUser(userText.getText(), String.valueOf(passText.getPassword()), countryText.getText());
            this.closeRegisterWindow();
            System.out.println("New user registered");


        }
        if (e.getSource() == cancelButton) {
            registerFrame.dispose();
            System.out.println("Going back to login-screen");
        }
    }

    public void closeRegisterWindow() {
        registerFrame.dispose();
    }

}
