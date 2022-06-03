package server.model;

import java.io.Serializable;

public class DrawRequest implements Serializable {

    private String gameID;
    private String sender;
    private boolean accepted;

    public DrawRequest(String gameID, String sender) {
        this.gameID = gameID;
        this.sender = sender;
        accepted = false;
    }

    public void setAccepted() {
        accepted = true;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getGameID() {
        return gameID;
    }

    public String getSender() {
        return sender;
    }
}
