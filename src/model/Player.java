package model;

public class Player {
    private String usrName;
    private String passW;
    private int gamesplayed;
    private int gameswon;
    private boolean turn;

    private int pbScore;
    private String pbTime;

    public Player(String usrName){
        this.usrName = usrName;
        this.gamesplayed = Integer.valueOf(0); //internally similar to gamesplayed = 0
        this.gameswon = Integer.valueOf(0);
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

    public int getGamesPlayed()
    {
        return gamesplayed;
    }

    public int getGamesWon()
    {
        return gameswon;
    }

    public int getWinPercentage()
    {
        //formula to get the percentage
        return 0;
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

    public void setGamesPlayed(int gamesP)
    {
        this.gamesplayed = gamesP;
    }

    public void setGamesWon(int gamesW)
    {
        this.gameswon = gamesW;
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
