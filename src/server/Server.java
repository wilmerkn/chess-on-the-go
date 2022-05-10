package server;

import server.model.Challenge;
import server.model.Message;
import server.model.Move;
import server.model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.List;

public class Server implements Runnable {

    private int port;

    private Hashtable<Player, ClientHandler> playerClientMap;

    public Server(int port) {
        this.port = port;
        playerClientMap = new Hashtable<>();
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

    private void broadcastPlayers() {
        for(ClientHandler client: playerClientMap.values()) {
            try {
                client.getOos().writeObject(playerClientMap.keySet());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        private Player player;

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
                player = (Player) ois.readObject();
                playerClientMap.put(player, this);
                broadcastPlayers();
                System.out.println(player.getUsrName() + "connected.");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                while(true) {
                    // Ta emot ChessPieceAbstract[][], Challenge, List<Players>, Message
                    // Move
                    Object object = ois.readObject();

                    if (object instanceof Challenge challenge) {

                        if(!challenge.isAccepted() && !challenge.isDeclined()) {
                            for (Player player: playerClientMap.keySet()) {
                                if(player.getUsrName().equals(challenge.getReceiverName())) {
                                    challenge.setChallengeReceiver(player);
                                    playerClientMap.get(player).getOos().writeObject(challenge);
                                }
                            }
                        } else if(challenge.isAccepted()) {
                            System.out.println("Challenge accepted");
                        }


                    } else if(object instanceof Message) {


                    } else if(object instanceof Move mv) {
                        // If move legal make move
                        // If not return null
                    }
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

        public ObjectOutputStream getOos() {
            return oos;
        }
    }

    public static void main(String[] args) {
        System.out.println("Server starting");
        new Server(112);
    }
}
