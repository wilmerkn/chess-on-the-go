package server.model;

import java.io.Serializable;

public class LoginRequest implements Serializable {

    private final String username;
    private final String password;

    private boolean accepted;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
