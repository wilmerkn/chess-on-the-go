package model;

public class Player {
    private String usrName;
    private String passW;
    private boolean turn;

    private int pbScore;
    private String pbTime;

    public Player(){

    }

    public String getPassW() {
        return passW;
    }

    public int getPbScore() {
        return pbScore;
    }

    public String getPbTime() {
        return pbTime;
    }

    public String getUsrName() {
        return usrName;
    }

    public void setPbScore(int pbScore) {
        this.pbScore = pbScore;
    }

    public void setPbTime(String pbTime) {
        this.pbTime = pbTime;
    }

    public void setPassW(String passW) {
        this.passW = passW;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public void setUsrName(String usrName) {
        this.usrName = usrName;
    }

    @Override
    public String toString() {
        String outText = String.format(""); //fix this
        return outText;
    }
}
