package ru.ifmo.practice.seabattle.server.battleservers;

public class GamersInformation {
    private String gamer1NickName;
    private String gamer2NickName;
    private Integer gamer1Rating;
    private Integer gamer2Rating;

    public GamersInformation(String gamer1NickName, String gamer2NickName,
                             Integer gamer1Rating, Integer gamer2Rating) {
        this.gamer1NickName = gamer1NickName;
        this.gamer2NickName = gamer2NickName;
        this.gamer1Rating = gamer1Rating;
        this.gamer2Rating = gamer2Rating;
    }

    public Integer getGamer1Rating() {
        return gamer1Rating;
    }

    public Integer getGamer2Rating() {
        return gamer2Rating;
    }

    public String getGamer1NickName() {
        return gamer1NickName;
    }

    public String getGamer2NickName() {
        return gamer2NickName;
    }
}
