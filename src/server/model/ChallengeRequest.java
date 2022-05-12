package server.model;

public class ChallengeRequest {
    private Player challengeSender;
    private Player challengeReceiver;
    private String receiverName;

    private boolean accepted = false;
    private boolean declined = false;

    public ChallengeRequest(Player challengeSender, String receiverName) {
        this.challengeSender = challengeSender;
        this.receiverName = receiverName;
        this.accepted = false;
        this.declined = false;
    }

    public Player getChallengeSender() {
        return challengeSender;
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
