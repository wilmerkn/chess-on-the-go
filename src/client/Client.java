package client;

import client.lobby.LobbyView;
import client.login.LoginView;
import server.controller.LoginController;
import server.model.ChallengeRequest;
import server.model.LoginRequest;
import server.model.Message;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 1234;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    LoginView loginView;
    LobbyView lobbyView;
    public Client() {
        LoginController loginController = new LoginController();
        loginView = new LoginView(loginController, this);
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
        // Send player to server
    }

    private void disconnect() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Client disconnected");
            e.printStackTrace();
        }
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
                            lobbyView = new LobbyView();
                            loginView.closeLoginWindow();

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
                    } else if(obj instanceof ArrayList) {
                        ArrayList<String> players = (ArrayList<String>) obj; // No problem
                        System.out.println("CLIENT: players length" + players.size());

                        lobbyView.getUserPanel().setOnlinePlayers(players);


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
