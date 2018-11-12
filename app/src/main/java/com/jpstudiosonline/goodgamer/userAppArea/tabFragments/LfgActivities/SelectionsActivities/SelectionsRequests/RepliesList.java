package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests;

/**
 * Created by jahnplay on 11/13/2016.
 */

public class RepliesList {

    private int replyID, parentRequestID, replyUserID, votes;
    private String replyUserName, replyComment, replyCreatedTime;

    public RepliesList(int replyID, int parentRequestID, int replyUserID, int votes, String replyUserName, String replyComment, String replyCreatedTime){

        this.replyID = replyID;
        this.parentRequestID = parentRequestID;
        this.replyUserID = replyUserID;
        this.votes = votes;
        this.replyUserName = replyUserName;
        this.replyComment = replyComment;
        this.replyCreatedTime = replyCreatedTime;


    }

    public RepliesList(String replyUserName, String replyComment, int replyUserID, String replyCreatedTime){

        this.replyUserName = replyUserName;
        this.replyUserID = replyUserID;
        this.replyComment = replyComment;
        this.replyCreatedTime = replyCreatedTime;


    }

    public int getReplyUserID(){

        return replyUserID;
    }

    public void setReplyUserID(int replyUserID){

        this.replyUserID = replyUserID;

    }

    public int getReplyID(){

        return replyID;
    }

    public int getParentRequestID(){

        return parentRequestID;

    }

    public int getVotes(){

        return votes;
    }

    public String getReplyUserName(){

        return replyUserName;
    }

    public String getReplyComment(){

        return replyComment;
    }

    public String getReplyCreatedTime(){

        return replyCreatedTime;

    }

    public void setReplyID(int replyID){

        this.replyID = replyID;

    }

    public void setParentRequestID(int parentRequestID){

        this.parentRequestID = parentRequestID;

    }

    public void setVotes(int votes){

        this.votes = votes;

    }

    public void setReplyUserName(String replyUserName){

        this.replyUserName = replyUserName;
    }

    public void setReplyComment(String replyComment){

        this.replyComment = replyComment;

    }

    public void setReplyCreatedTime(String replyCreatedTime){

        this.replyCreatedTime = replyCreatedTime;

    }

}
