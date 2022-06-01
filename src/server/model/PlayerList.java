package server.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerList implements Serializable {
    private final ArrayList<Player> list = new ArrayList<>();

    public void add(Player p){
        list.add(p);
    }

    public void remove(Player p) {
        list.remove(p);
    }

    public ArrayList<String> getStringList() {
        ArrayList<String> stringList = new ArrayList<>();
        for (Player p: list) {
            stringList.add(p.getUsrName());
        }
        return stringList;
    }
    public void remove(String username){
        list.removeIf(p -> username.equals(p.getUsrName()));
    }
}
