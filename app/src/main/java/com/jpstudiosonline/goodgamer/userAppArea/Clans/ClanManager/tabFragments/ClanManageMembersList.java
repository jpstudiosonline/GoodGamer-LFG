package com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager.tabFragments;

/**
 * Created by jahnplay on 11/6/2016.
 */

public class ClanManageMembersList {

    private String userName, clanName;
    private int clanID, clanMemberUserID, userID;
    private boolean canRemoveUser, canApproveUser, isUserAdmin;

    public ClanManageMembersList(){


    }

    public ClanManageMembersList(int clanID, String userName){

        this.clanID = clanID;
        this.userName = userName;
    }

    public ClanManageMembersList(int clanID, String userName, int clanMemberUserID){

        this.clanID = clanID;
        this.userName = userName;
        this.clanMemberUserID = clanMemberUserID;
    }

    public ClanManageMembersList(int clanID, String userName, int clanMemberUserID, int userID, String clanName, boolean canRemoveUser, boolean canApproveUser, boolean isUserAdmin){

        this.clanID = clanID;
        this.userName = userName;
        this.clanMemberUserID = clanMemberUserID;
        this.canRemoveUser = canRemoveUser;
        this.clanName = clanName;
        this.userID = userID;
        this.canApproveUser = canApproveUser;
        this.isUserAdmin = isUserAdmin;
    }

    public int getclanMemberUserID(){

        return clanMemberUserID;

    }

    public void setclanMemberUserID(int clanMemberUserID){

        this.clanMemberUserID = clanMemberUserID;
    }

    public String getuserName(){

        return userName;
    }

    public int getclanID () {

        return clanID;
    }

    public void setuserName(String userName){

        this.userName = userName;
    }

    public void  setclanID (int clanID){

        this.clanID = clanID;

    }

    public void setcanApproveUser (boolean canUserAddGame){

        this.canApproveUser = canUserAddGame;

    }

    public boolean getcanApproveUser(){

        return canApproveUser;

    }

    public void setcanRemoveUser (boolean canRemoveUser){

        this.canRemoveUser = canRemoveUser;

    }

    public boolean getcanRemoveUser(){

        return canRemoveUser;

    }

    public void setClanName (String clanName){

        this.clanName = clanName;

    }

    public String getClanName(){

        return clanName;

    }

    public int getUserID(){

        return userID;
    }
    public void setUserID(int userID){

        this.userID = userID;
    }

    public boolean getIsUserAdmin(){

        return isUserAdmin;
    }
    public void setIsUserAdmin(boolean isUserAdmin){

        this.isUserAdmin = isUserAdmin;
    }



}