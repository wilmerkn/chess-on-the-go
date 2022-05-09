package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private int port;

    public Server(int port) {
        this.port = port;
        new Thread(this).start();
    }



    @Override
    public void run() {
        System.out.println("Server started");

        try(ServerSocket serverSocket = new ServerSocket(port)) {
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                new ClientHandler(socket).start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                this.ois = new ObjectInputStream(socket.getInputStream());
                this.oos = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            // Lyssnar efter objekt från klient
            try {
                while(true) {
                    Object obj = ois.readObject();
                    // Ta hand om objekt
                }
            } catch (IOException | ClassNotFoundException e) {
                disconnect();
                e.printStackTrace();
            }
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
    }

    public static void main(String[] args) {
        System.out.println("Server starting");
        new Server(112);
    }
}
