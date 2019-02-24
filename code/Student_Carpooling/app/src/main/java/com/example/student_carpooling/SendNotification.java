package com.example.student_carpooling;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class SendNotification {

    public SendNotification(String message, String heading, String notificationKey){

        //testing

        try {
            JSONObject notifcationContent = new JSONObject("{'contents':{'en':'" + message + "'},"+
                    "'include_player_ids':['"+ notificationKey + "'],"+ "'headings':{'en': '"+ heading + "'}}");
            OneSignal.postNotification(notifcationContent, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
