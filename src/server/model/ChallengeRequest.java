package server.model;

import java.io.Serializable;
/**
 * ChallengeRequest: Class used to send challenges between clients via the server.
 * @Version 1.0
 * @Author Lucas Kylberg
 */
public class ChallengeRequest implements Serializable {
    private String senderUsername;
    private String receiverUsername;
    private int timeControl;
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

    public int getTimeControl() {
        return timeControl;
    }
}
