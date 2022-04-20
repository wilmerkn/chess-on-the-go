package client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

import server.controller.LoginController;


public class LoginView extends JFrame implements ActionListener {


    JFrame loginFrame;
    JPanel picturePanel = new JPanel();
    JPanel textPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JButton loginButton;
    JButton registerButton;
    JLabel pictureLabel;
    ImageIcon image;
    ImageIcon resizedImage;
    Image scaledImage;
    private final JTextField userText;
    private final JPasswordField passText;
    private final JLabel userLabel;
    private final JLabel passLabel;

    public LoginView(){

        loginFrame = new JFrame("Chess On The Go - Login");

        loginFrame.add(picturePanel);
        picturePanel.setBackground(Color.LIGHT_GRAY);

        loginFrame.add(textPanel);
        textPanel.setBackground(Color.LIGHT_GRAY);

        loginFrame.add(buttonPanel);
        buttonPanel.setBackground(Color.LIGHT_GRAY);


        try{
            BufferedImage knightImage = ImageIO.read(new File("sprites/Chess-Knight.png"));
            scaledImage = knightImage.getScaledInstance(100,100,Image.SCALE_DEFAULT);
            resizedImage = new ImageIcon(scaledImage);
            pictureLabel = new JLabel(resizedImage);
            picturePanel.add(pictureLabel);

        } catch(Exception e){
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

        init();
        initListeners();
    }

    private void init(){
        loginFrame.setLayout(new GridLayout(3,1));
        loginFrame.setSize(300,400);
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
            System.out.println("Logging in");
            //osäker på om det är "secure" att skicka pass så här
            LoginController.checkLogin(userText.getText(),String.valueOf(passText.getPassword()));
            loginFrame.setVisible(false);
        }
        if (e.getSource() == registerButton) {
            System.out.println("Register");
            RegisterView register = new RegisterView();
        }

    }
}
