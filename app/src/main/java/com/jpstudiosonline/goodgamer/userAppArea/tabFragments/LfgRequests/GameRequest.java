package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgRequests;

/**
 * Created by jahnplay on 11/3/2016.
 */

public class GameRequest {

    private String requestUserName, requestTitle, requestGroup, requestDescription, requestConsole, gameName, time, userVote;
    private int requestID, totalVotes, commentCount, userID;

    public GameRequest(){


    }

    public GameRequest (int requestID, String requestUserName, String requestGroup, String requestTitle, String requestDescription, String requestConsole,
                        String gameName, String time, int totalVotes, int commentCount, String userVote, int userID){

        this.requestID = requestID;
        this.requestTitle = requestTitle;
        this.requestGroup = requestGroup;
        this.requestUserName = requestUserName;
        this.requestDescription = requestDescription;
        this.requestConsole = requestConsole;
        this.gameName = gameName;
        this.time = time;
        this.totalVotes = totalVotes;
        this.commentCount = commentCount;
        this.userVote = userVote;
        this.userID = userID;

    }

    public int getUserID(){

        return userID;
    }

    public void setUserID(int userID){

        this.userID = userID;

    }
    public String getTitle(){

        return requestTitle;
    }

    public void setTitle (String requestTitle) {

        this.requestTitle = requestTitle;
    }

    public String getRequestGroup () {

        return requestGroup;
    }

    public void setRequestGroup ( String requestGroup) {

        this.requestGroup = requestGroup;

    }

    public int getRequestID () {

        return requestID;
    }

    public void setRequestID ( int requestID) {

        this.requestID = requestID;

    }

    public String getRequestUserName () {

        return requestUserName;
    }

    public void setRequestUserName ( String requestUserName) {

        this.requestUserName = requestUserName;

    }


    public String getRequestDescription () {

        return requestDescription;
    }

    public void setRequestDescription ( String requestDescription) {

        this.requestDescription = requestDescription;

    }

    public String getRequestConsole () {

        return requestConsole;
    }

    public void setRequestConsole( String requestConsole) {

        this.requestConsole = requestConsole;

    }

    public String getGameName () {

        return gameName;
    }

    public void setGameName( String gameName) {

        this.gameName = gameName;

    }

    public String getTime () {

        return time;

    }

    public void setTime( String time) {

        this.time = time;

    }

    public int getTotalVotes () {

        return totalVotes;

    }

    public void setTotalVotes(int totalVotes) {

        this.totalVotes = totalVotes;

    }


    public int getCommentCount () {

        return commentCount;

    }

    public void setCommentCount(int commentCount) {

        this.commentCount = commentCount;

    }

    public void setUserVote(String userVote){

        this.userVote = userVote;

    }

    public String getUserVote(){

        return userVote;

    }

}
