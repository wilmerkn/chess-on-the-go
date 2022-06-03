package server.model;

import java.io.Serializable;


public class ChallengeRequest implements Serializable {
    private final String senderUsername;
    private String receiverUsername;
    private final int timeControl;

    private boolean accepted = false;

    public ChallengeRequest(String senderUsername, String receiverUsername, int timeControl) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.timeControl = timeControl;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void accept() {
        this.accepted = true;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public int getTimeControl() {
        return timeControl;
    }
}
