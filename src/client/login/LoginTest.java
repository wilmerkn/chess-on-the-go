package client.login;

import server.controller.GameLogic;
import server.controller.LoginController;

public class LoginTest {
    public static void main(String[] args) {
        LoginController loginController = new LoginController();
        LoginView loginView = new LoginView(loginController);
    }
}
