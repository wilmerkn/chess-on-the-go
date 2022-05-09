package client;

import server.controller.LoginController;

public class Client {

    public static void main(String[] args) {
        //testkör här för att komma in via login etc /wilmer
        //För er som testar, är lite osäker på om det kommer funka med databasconnection i och med att det är localhost för mig
        LoginController loginController = new LoginController();
    }
}
