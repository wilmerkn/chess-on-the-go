package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;


public class RegisterView extends JFrame implements ActionListener{
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


    public RegisterView(){
        registerFrame = new JFrame("Chess On The Go - Registration");

        try{
            image = new ImageIcon(getClass().getResource("Chess-Knight.png"));
            scaledImage = image.getImage().getScaledInstance(100,100,Image.SCALE_DEFAULT);
            resizedImage = new ImageIcon(scaledImage);
            pictureLabel = new JLabel(resizedImage);
            picturePanel.add(pictureLabel);

        } catch(Exception e){
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

        this.countryLabel = new JLabel("Username:");
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

    private void init(){
        registerFrame.setLayout(new GridLayout(3,1));
        registerFrame.setSize(300,500);
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
            System.out.println("Registered");
        }
        if (e.getSource() == cancelButton) {
            System.out.println("Going back to login-screen");
        }
    }
}
