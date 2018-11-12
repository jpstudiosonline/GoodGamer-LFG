package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests;

/**
 * Created by jahnplay on 11/6/2016.
 */

public class ConsoleList {

    private String consoleName;
    private int consoleId;

    public ConsoleList(){


    }

    public ConsoleList(int consoleId, String consoleName){

        this.consoleId = consoleId;
        this.consoleName = consoleName;
    }

    public String getConsoleName(){

        return consoleName;
    }

    public int getConsoleId () {

        return consoleId;
    }

    public void setConsoleName(String consoleName){

        this.consoleName = consoleName;
    }

    public void  setConsoleId (int consoleId){

        this.consoleId = consoleId;

    }


}