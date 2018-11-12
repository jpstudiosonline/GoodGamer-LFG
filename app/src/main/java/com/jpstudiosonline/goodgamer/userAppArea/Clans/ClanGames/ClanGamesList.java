package com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanGames;

/**
 * Created by jahnplay on 11/6/2016.
 */

public class ClanGamesList {

    private String gameName, clanName;
    private int gameId, requestCount;
    private boolean canDeleteGame, canAddGame;

    public ClanGamesList(){


    }

    public ClanGamesList(int gameId, String gameName){

        this.gameId = gameId;
        this.gameName = gameName;
    }

    public ClanGamesList(int gameId, String gameName, int requestCount){

        this.gameId = gameId;
        this.gameName = gameName;
        this.requestCount = requestCount;
    }

    public ClanGamesList(int gameId, String gameName, int requestCount, boolean canAddGame, boolean canDeleteGame, String clanName){

        this.gameId = gameId;
        this.gameName = gameName;
        this.requestCount = requestCount;
        this.canAddGame = canAddGame;
        this.canDeleteGame = canDeleteGame;
        this.clanName = clanName;
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

    public void setCanAddGame (boolean canUserAddGame){

        this.canAddGame = canUserAddGame;

    }

    public boolean getCanAddGame(){

        return canAddGame;

    }

    public void setCanDeleteGame (boolean canDeleteGame){

        this.canDeleteGame = canDeleteGame;

    }

    public boolean getCanDeleteGame(){

        return canDeleteGame;

    }

    public void setClanName (String clanName){

        this.clanName = clanName;

    }

    public String getClanName(){

        return clanName;

    }



}