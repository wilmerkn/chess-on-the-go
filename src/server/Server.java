package server;

import server.controller.GameLogic;
import server.controller.LoginController;
import server.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Server implements Runnable {

    private static final int PORT = 1234;

    private Hashtable<Player, ClientHandler> playerClientMap; // Testa concurrentHashmap
    private List<GameLogic> games = new ArrayList<>();

    private LoginController loginController = new LoginController();

    private ArrayList<String> playerList = new ArrayList<>();



    public Server() {
        playerClientMap = new Hashtable<>();
        new Thread(this).start();

    }

    @Override
    public void run() {
        System.out.println("Server started");

        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
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
            try {
                while(true) {
                    Object object = ois.readObject();

                    if(object instanceof LoginRequest) {
                        LoginRequest loginReq = (LoginRequest) ois.readObject();
                        System.out.println("#¤%&/!#(");
                        boolean loginOk = loginController.checkLogin(loginReq.getUsername(), loginReq.getPassword());
                        System.out.println("loginOk:" + loginOk);

                        if(loginOk) {
                            loginReq.setAccepted(true);
                            System.out.println("Server: login true");
                            oos.writeObject(loginReq);

                            player = new Player(loginReq.getUsername());
                            playerClientMap.put(player, this);
                            playerList.add(player.getUsrName());

                            Thread.sleep(1000);

                            broadcastPlayers();

                            System.out.println(player.getUsrName() + " connected.");
                        } else {
                            oos.writeObject(loginReq);
                            System.out.println("Login failed");
                        }
                    }

                    if (object instanceof ChallengeRequest challenge) {

                        if(!challenge.isAccepted() && !challenge.isDeclined()) {
                            for (Player player: playerClientMap.keySet()) {
                                if(player.getUsrName().equals(challenge.getReceiverName())) {
                                    challenge.setChallengeReceiver(player);
                                    playerClientMap.get(player).getOos().writeObject(challenge);
                                }
                            }
                        } else if(challenge.isAccepted()) {
                            System.out.println("Challenge accepted");
                            GameLogic gl = new GameLogic();
                            Player player1; // Ta från challenge
                            Player player2; // Ta från challenge

                            //gl.addPlayers() ?
                            games.add(gl);
                        }

                    } else if(object instanceof Message) {
                        Message msg = (Message) object;
                        // Inte viktigt nu

                    } else if(object instanceof Move) {
                        //Move mv = (Move) object;
                        // If move legal make move
                        // If not return null
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                //disconnect();
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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

        private void broadcastPlayers() {

            for(ClientHandler client: playerClientMap.values()) {
                try {
                    System.out.println("broadcastPlayers " + playerList.size());
                    client.getOos().reset();
                    client.getOos().writeObject(playerList);
                    client.getOos().flush();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Server starting");
        new Server();
    }
}
