package server.model;

public class Challenge {
    private Player challengeSender;
    private Player challengeReceiver;
    private String receiverName;

    private boolean accepted = false;
    private boolean declined = false;

    public Challenge(Player challengeSender, String receiverName) {
        this.challengeSender = challengeSender;
        this.receiverName = receiverName;
        this.accepted = false;
        this.declined = false;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setDeclined(boolean declined) {
        this.declined = declined;
    }

    public boolean isDeclined() {
        return declined;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setChallengeReceiver(Player challengeReceiver) {
        this.challengeReceiver = challengeReceiver;
    }
}
