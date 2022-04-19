package server.controller;

import client.board.BoardView;

public class LoginController {
    public static void checkLogin(String user, String password){
        System.out.println("Username = " + user + "\n" + "Password = " + password);
        BoardView gw = new BoardView();

    }
}
