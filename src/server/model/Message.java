package server.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Message: Class for a message being sent between clients in a game.
 * Version: 1.0
 * Author: Lucas Kylberg
 */
public class Message implements Serializable {
    private final String gameID;
    private final Player sender;
    private final String timestamp;
    private final String text;

    public Message(String gameID, Player sender, String text) {
        this.gameID = gameID;
        this.sender = sender;
        this.timestamp = new SimpleDateFormat("[HH:mm]").format(new Date());
        this.text = text;
    }

    public String getGameID() {
        return gameID;
    }

    @Override
    public String toString() {
        return String.format("%s %s: %s", timestamp, sender.getUsrName(), text);
    }
}
