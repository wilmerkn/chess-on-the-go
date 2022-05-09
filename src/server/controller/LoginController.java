package server.controller;

import client.lobby.LobbyView;
import client.login.LoginView;
import server.model.DatabaseConnection;
import server.model.Login;



public class LoginController {
    private final DatabaseConnection dbCon;
    private final LoginView loginView;
    private RegisterController registerController = new RegisterController();


    public LoginController() {
        this.dbCon = new DatabaseConnection();
        this.loginView = new LoginView(this, registerController);
    }
//kollar med databas att kontot finns via model, finns kontot öppnas brädet
    public void checkLogin(String user, String password){
        boolean loginOk;

        loginOk = Login.loginCheck(dbCon.getConnection(), user,password);

        if (loginOk) {
            loginView.closeLoginWindow();
            LobbyView lobbyView = new LobbyView();
        }
    }
}
