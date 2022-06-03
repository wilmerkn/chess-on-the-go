package server.model;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * ChatLog: Stores messages sent between two clients in a game.
 * Version: 1.0
 * Author: Lucas Kylberg
 */
public class ChatLog implements Serializable {
    private final ArrayList<Message> list = new ArrayList<>();
    public void add(Message m){
        list.add(m);
    }

    public ArrayList<String> getStringList() {
        ArrayList<String> stringList = new ArrayList<>();
        for (Message m: list) {
            stringList.add(m.toString());
        }
        return stringList;
    }
}
