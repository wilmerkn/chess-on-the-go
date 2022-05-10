package client;

import server.controller.LoginController;
import server.model.Challenge;
import server.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private static String HOST = "123.123.23.231";
    private static int PORT = 1234;

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public Client() {
        LoginController loginController = new LoginController();
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
    public static void main(String[] args) {
        //testkör här för att komma in via login etc /wilmer
        //För er som testar, är lite osäker på om det kommer funka med databasconnection i och med att det är localhost för mig
        new Client();
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
}
