package client;

import client.lobby.LobbyView;
import client.login.LoginView;
import server.model.ChallengeRequest;
import server.model.LoginRequest;
import server.model.Message;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 1234;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    LoginView loginView;
    LobbyView lobbyView;



    public Client() {
        //LoginController loginController = new LoginController();

        loginView = new LoginView(this);
        connect();
        new ServerListener().start();
    }

    // public??
    public void login(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        try {
            oos.writeObject(loginRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void connect() {
        try {
            socket = new Socket(HOST, PORT);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Skicka player till server
    }

    private void disconnect() {

    }

    private class ServerListener extends Thread {

        @Override
        public void run() {

            while(true) {
                try {
                    Object obj = ois.readObject();
                    if(obj instanceof LoginRequest) {
                        LoginRequest request = (LoginRequest) obj;
                        if(!request.isAccepted()) {
                            JOptionPane.showMessageDialog(null, "Login failed");
                            System.out.println("Login failed");
                        } else {
                            System.out.println("Login ok");
                            loginView.dispose();
                            lobbyView = new LobbyView();
                            // skicka lista med online användare
                        }
                    } else if(obj instanceof ChallengeRequest) {
                        ChallengeRequest challenge = (ChallengeRequest) obj;
                        String challengerName = challenge.getChallengeSender().getUsrName();
                        int answer = JOptionPane.showConfirmDialog(
                                null,
                                "Incoming challenge",
                                String.format("Do you want to accept incoming challenge from %s?", challengerName),
                                JOptionPane.YES_NO_OPTION
                        );

                        if(answer == 0) { // Challenge accepted
                            challenge.setAccepted(true);

                        } else if (answer == 1) { // Challenge declined

                        }

                        System.out.println("Challenge");
                        // Lägg till namn i Users online

                    }else if (obj instanceof Message) {
                        // Lägg message i textArea
                    }
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public static void main(String[] args) {
        new Client();
    }
}
