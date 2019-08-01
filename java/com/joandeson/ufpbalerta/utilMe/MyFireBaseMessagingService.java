package com.joandeson.ufpbalerta.utilMe;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by JoHN on 18/09/2018.
 */

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().size() > 0){
            if(remoteMessage.getData().get("content")!=null) {
                Log.d("dblog_content", remoteMessage.getData().get("content"));
            }
        }

    }
}