package com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments;

/**
 * Created by jahnplay on 10/1/2017.
 */

public class ChatMessage {
    public boolean left;
    public String message;
    public String dateSent, fromUserName;

    public ChatMessage(boolean left, String message, String dateSent) {
        super();
        this.left = left;
        this.message = message;
        this.dateSent = dateSent;
    }

    public ChatMessage(boolean left, String message, String dateSent, String fromUserName) {
        super();
        this.left = left;
        this.message = message;
        this.dateSent = dateSent;
        this.fromUserName = fromUserName;
    }
}