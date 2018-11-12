package com.jpstudiosonline.goodgamer.userAppArea.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.UserAreaActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameRequestsActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.ViewLfgRequestActivity;

/**
 * Created by jahnplay on 11/16/2016.
 */

//Class is extending GcmListenerService
public class GCMPushReceiverService extends GcmListenerService {

    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
        String title = data.getString("title");
        String message = data.getString("message");
        int gameID = Integer.parseInt(data.getString("gameID"));
        String gameName = data.getString("gameName");
        String requestID = "0";
        requestID = data.getString("parentrequestID");
        //Displaying a notiffication with the message

        if (!requestID.equals("0")){

            String username = data.getString("username");
            String grouptype = data.getString("grouptype");
            String description = data.getString("description");
            String console = data.getString("console");
            String game = data.getString("game");
            String totalcomments = data.getString("totalcomments");
            String upvotes = data.getString("upvotes");
            String created = data.getString("created");
            String requestUserID = data.getString("requestUserID");


            sendReplyNotification(Integer.parseInt(requestUserID), title, message, Integer.parseInt(requestID), username, grouptype, description, console, game, Integer.parseInt(totalcomments), Integer.parseInt(upvotes), created);


        } else {

            sendNotification(title, message, gameID, gameName);

        }

    }

    //This method is generating a notification and displaying the notification
    private void sendNotification(String title, String message, int gameID, String gameName) {

        Intent intent;
        int gameIDcheck = 0;
        gameIDcheck = gameID;

        if (gameIDcheck != 0){

            intent = new Intent(this, GameRequestsActivity.class);
            intent.putExtra("gameName", gameName);
            intent.putExtra("gameID", gameID);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        } else {

            intent = new Intent(this, UserAreaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        }

        //int color = 0xff123456;

        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notif_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setColor(Color.RED)
                .setSound(sound)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(Color.RED, 3000, 3000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }

    //This method is generating a notification and displaying the notification
    private void sendReplyNotification(int requestUserID, String title, String message, int requestID, String username, String grouptype, String description, String console, String gameName
    , int commentcount, int upvotes, String created) {

        Intent intent;

         intent = new Intent(this, ViewLfgRequestActivity.class);
         intent.putExtra("gameName", gameName);
         intent.putExtra("lfgRequestID", requestID);
         intent.putExtra("lfgRequestTitle", title);
         intent.putExtra("requestUser", username);
         intent.putExtra("requestConsole", console);
         intent.putExtra("requestDescription", description);
         intent.putExtra("requestGroup", grouptype);
         intent.putExtra("requestTime", created);
         intent.putExtra("totalVotes", upvotes);
         intent.putExtra("commentCount", commentcount);
         intent.putExtra("requestUserID", requestUserID);
         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);



        //int color = 0xff123456;

        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notif_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setColor(Color.RED)
                .setSound(sound)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(Color.RED, 3000, 3000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }
}
