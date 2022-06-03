package server.controller;

import server.model.DatabaseConnection;
import server.model.Login;


/**
 * LoginController: Responsible for creating a database connection with the login information
 * @version 1.0
 * @author wilmerknutas
 */
public class LoginController {
    private final DatabaseConnection dbCon;

    public LoginController() {
        this.dbCon = new DatabaseConnection();
    }

    public boolean checkLogin(String user, String password){
        boolean loginOk;
        loginOk = Login.loginCheck(dbCon.getConnection(), user,password);
        return loginOk;
    }
}
