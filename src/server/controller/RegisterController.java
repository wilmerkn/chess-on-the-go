package server.controller;

import client.LoginView;
import client.RegisterView;
import server.model.DatabaseConnection;
import server.model.Register;

public class RegisterController {
    private final DatabaseConnection dbCon;
    private RegisterView registerView;
    public RegisterController() {
        this.dbCon = new DatabaseConnection();
    }
    public void registerUser(String username, String password, String country){
        Register.register(dbCon.getConnection(), username, password,country);

    }
    public void showRegisterWindow(){
        this.registerView = new RegisterView(this);
    }


}
