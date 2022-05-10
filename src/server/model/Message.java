package server.model;

public class Message {
    private final Player sender;
    private final String text;

    public Message(Player sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", sender, text);
    }
}
