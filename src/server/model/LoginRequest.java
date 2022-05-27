package server.model;

import java.io.Serializable;

public class LoginRequest implements Serializable {

    private final String username;
    private final String password;

    private boolean accepted;

    private Player player;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
        this.accepted = false;
    }

    public void setAccepted(boolean accepted, Player player) {
        this.accepted = accepted;
        this.player = player;
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

    public Player getPlayer() {
        return player;
    }
}
