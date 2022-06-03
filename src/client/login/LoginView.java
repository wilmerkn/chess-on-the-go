package client.login;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import client.Client;
import client.register.RegisterView;
import server.controller.LoginController;
import server.controller.RegisterController;


/**
 * LoginView: Shows the user the login window where they can either choose to register a new user or log in with an existing user.
 * @version 1.0
 * @author wilmerknutas
 */
public class LoginView extends JFrame implements ActionListener {

    JFrame loginFrame;
    JPanel picturePanel = new JPanel();
    JPanel textPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JButton loginButton;
    JButton registerButton;
    JLabel pictureLabel;
    ImageIcon resizedImage;
    Image scaledImage;
    private final JTextField userText;
    private final JPasswordField passText;
    private final JLabel userLabel;
    private final JLabel passLabel;
    private final LoginController loginController;
    private RegisterController registerController = new RegisterController();
    private final Client client;


    public LoginView(LoginController loginController, Client client) {
        this.loginController = loginController;
        this.client = client;
        loginFrame = new JFrame("Chess On The Go - Login");

        loginFrame.add(picturePanel);
        picturePanel.setBackground(Color.LIGHT_GRAY);

        loginFrame.add(textPanel);
        textPanel.setBackground(Color.LIGHT_GRAY);

        loginFrame.add(buttonPanel);
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        try {
            BufferedImage knightImage = ImageIO.read(new File("sprites/Chess-Knight.png"));
            scaledImage = knightImage.getScaledInstance(100, 100, Image.SCALE_DEFAULT);
            resizedImage = new ImageIcon(scaledImage);
            pictureLabel = new JLabel(resizedImage);
            picturePanel.add(pictureLabel);

        } catch (Exception e) {
            System.out.println("Can't find image for login");
        }
        textPanel.setLayout(new FlowLayout());
        this.userLabel = new JLabel("Username:");
        textPanel.add(userLabel);

        this.userText = new JTextField(20);
        textPanel.add(userText);

        this.passLabel = new JLabel("Password:");
        textPanel.add(passLabel);

        this.passText = new JPasswordField(20);
        textPanel.add(passText);

        this.loginButton = new JButton("Login");
        buttonPanel.add(loginButton);

        this.registerButton = new JButton("Register");
        buttonPanel.add(registerButton);

        initListeners();
        init();
    }

    private void init() {
        loginFrame.setLayout(new GridLayout(3, 1));
        loginFrame.setSize(300, 400);
        loginFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        loginFrame.setResizable(false);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private void initListeners() {
        this.loginButton.addActionListener(this);
        this.registerButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            client.login(userText.getText(),String.valueOf(passText.getPassword()));
        } else if (e.getSource() == registerButton) {
            RegisterView register = new RegisterView(registerController);
        }
    }

    public void closeLoginWindow() {
        loginFrame.dispose();
        loginFrame.setVisible(false);
    }
}
