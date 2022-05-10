package client;

import client.login.LoginView;
import server.controller.LoginController;
import server.model.Challenge;
import server.model.Message;
import server.model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 1234;

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    LoginView loginView;


    public Client() {
        //LoginController loginController = new LoginController();
        loginView = new LoginView(this);
        connect();
        new ServerListener().start();
    }

    // public??
    public void login(String username, String password) {
        Player player = new Player(username);
        player.setPassW(password);
        try {
            oos.writeObject(player);
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
                    if(obj instanceof Challenge) {
                        // accept eller decline
                    } else if (obj instanceof Message) {
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
