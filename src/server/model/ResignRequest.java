package server.model;

import java.io.Serializable;

public class ResignRequest implements Serializable {

    private String gameID;
    private String resigner;

    public ResignRequest(String gameID, String resigner) {
        this.gameID = gameID;
        this.resigner = resigner;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getResigner() {
        return resigner;
    }

    public void setResigner(String resigner) {
        this.resigner = resigner;
    }
}
