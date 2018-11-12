package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests;

/**
 * Created by jahnplay on 11/6/2016.
 */

public class GroupTypeList {

    private String groupName;
    private int groupID;

    public GroupTypeList(){


    }

    public GroupTypeList(int groupID, String groupName){

        this.groupID = groupID;
        this.groupName = groupName;
    }

    public String getGroupName(){

        return groupName;
    }

    public int getGroupId () {

        return groupID;
    }

    public void setGroupName(String groupName){

        this.groupName = groupName;
    }

    public void  setGroupID (int groupID){

        this.groupID = groupID;

    }


}