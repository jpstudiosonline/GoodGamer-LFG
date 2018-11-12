package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests;

/**
 * Created by jahnplay on 11/6/2016.
 */

public class GamesList {

    private String gameName;
    private int gameId, requestCount;

    public GamesList(){


    }

    public GamesList(int gameId, String gameName){

        this.gameId = gameId;
        this.gameName = gameName;
    }

    public GamesList(int gameId, String gameName, int requestCount){

        this.gameId = gameId;
        this.gameName = gameName;
        this.requestCount = requestCount;
    }

    public int getRequestCount(){

        return requestCount;

    }

    public void setRequestCount(int requestCount){

        this.requestCount = requestCount;
    }

    public String getgameName(){

        return gameName;
    }

    public int getgameId () {

        return gameId;
    }

    public void setgameName(String gameName){

        this.gameName = gameName;
    }

    public void  setgameId (int gameId){

        this.gameId = gameId;

    }


}