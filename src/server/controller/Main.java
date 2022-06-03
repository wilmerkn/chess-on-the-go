package server.controller;

import client.Client;
/**
 * Main: starts a client, can only be used when program is fully working with an operating server.
 * As for now we are using another class called "Test2Clients" where we launch the server on a local host as well as two clients.
 * @version 1.0
 * @author wilmerknutas
 */
public class Main {
    public static void main(String[] args) {
        Client client = new Client();
    }
}
