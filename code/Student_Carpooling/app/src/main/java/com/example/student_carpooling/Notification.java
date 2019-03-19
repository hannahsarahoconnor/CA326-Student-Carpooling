package com.example.student_carpooling;

import com.onesignal.OneSignal;
import org.json.JSONException;
import org.json.JSONObject;

public class Notification {

    public Notification(String message, String heading, String notificationKey){
        try {
            //create a json object that holds the content of the notifcation
            //message itself, the heading, and the notification key of the recipient
            JSONObject notifcationContent = new JSONObject("{'contents':{'en':'" + message + "'},"+
                    "'include_player_ids':['"+ notificationKey + "'],"+ "'headings':{'en': '"+ heading + "'}}");
            OneSignal.postNotification(notifcationContent, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
