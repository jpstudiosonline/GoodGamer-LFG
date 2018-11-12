package com.jpstudiosonline.goodgamer.userAppArea.ChatMessages.tabFragments;

/**
 * Created by jahnplay on 11/6/2016.
 */

public class ContactMembersList {

    private String userName, clanName, ownerUserName, contactUserName;
    private int clanID, clanMemberUserID, userID, ownerParentID, contactUserID;
    private boolean canRemoveUser, canApproveUser, isUserAdmin;

    public ContactMembersList(){


    }

    public ContactMembersList(int clanID, String userName){

        this.clanID = clanID;
        this.userName = userName;
    }

    public ContactMembersList(int ownerParentID, String ownerUserName, int contactUserID, String contactUserName){

        this.ownerParentID = ownerParentID;
        this.ownerUserName = ownerUserName;
        this.contactUserID = contactUserID;
        this.contactUserName = contactUserName;
    }

    public ContactMembersList(int clanID, String userName, int clanMemberUserID, int userID, String clanName, boolean canRemoveUser, boolean canApproveUser, boolean isUserAdmin){

        this.clanID = clanID;
        this.userName = userName;
        this.clanMemberUserID = clanMemberUserID;
        this.canRemoveUser = canRemoveUser;
        this.clanName = clanName;
        this.userID = userID;
        this.canApproveUser = canApproveUser;
        this.isUserAdmin = isUserAdmin;
    }

    public int getcOwnerParentID(){

        return ownerParentID;

    }

    public void setOwnerParentID(int ownerParentID){

        this.ownerParentID = ownerParentID;
    }

    public String getOwnerUserName(){

        return ownerUserName;
    }

    public int getContactUserID () {

        return contactUserID;
    }

    public void setOwnerUserName(String ownerUserName){

        this.ownerUserName = ownerUserName;
    }

    public void  setContactUserID(int contactUserID){

        this.contactUserID = contactUserID;

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

    public String getContactUserName(){

        return contactUserName;
    }
    public void setContactUserName(String contactUserName){

        this.contactUserName = contactUserName;
    }



}