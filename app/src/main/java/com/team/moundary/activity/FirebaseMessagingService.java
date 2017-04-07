package com.team.moundary.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.team.moundary.R;

import java.net.URLDecoder;
import java.util.Map;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    PendingIntent pendingIntent;
    Map<String, String> maps;
    String selete;

    String pusherNickname;

    int count = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
         maps = remoteMessage.getData();

         selete = maps.get("pushType");

        String categoryMessage;
        String category;
        if(selete.equals("1")){
            category = maps.get("category");
            if(category.equals("1")&& PropertyManager.getInstance().getSetting3().equals("T") ){
                categoryMessage=" 동네정보가 올라왔습니다";
            }else if(category.equals("2")&& PropertyManager.getInstance().getSetting4().equals("T") ){
                categoryMessage=" 동네정보가 올라왔습니다";
            }else if(category.equals("3")&& PropertyManager.getInstance().getSetting5().equals("T") ){
                categoryMessage = " 동네정보가 올라왔습니다";
            }else{
                categoryMessage = " 동네정보가 올라왔습니다";
            }
            sendNotification(categoryMessage,category);
        }else{
            try{
                pusherNickname = URLDecoder.decode(maps.get("pusherNickname"), "UTF-8");
            }catch(Exception e){
                Log.e("DecodingError", pusherNickname);
            }
            sendNotification();
        }

    }
    //2일때 보내는 Noti
    private void sendNotification() {

        Intent intent = new Intent(this, PushActivity.class);
      //  intent.putExtra("pusherNickname",String.valueOf(pusherNickname) + " 님이 친구신청을 보냈습니다");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo2)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo2))
                .setContentTitle("Moundary")
                .setContentText(String.valueOf(pusherNickname) + " 님이 친구신청을 보냈습니다")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
      /*  PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakelock.acquire(5000);*/

        notificationManager.notify(++count , notificationBuilder.build());
    }
    private void sendNotification(String categoryMessage, String extraCategory){
        int categoryValue = Integer.parseInt(extraCategory);
        Intent intent = new Intent(this, PushActivity.class);
        intent.putExtra("category", categoryValue);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo2)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo2))
                .setContentTitle("Moundary")
                .setContentText(categoryMessage)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
       /* PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakelock.acquire(5000);*/

        notificationManager.notify(++count , notificationBuilder.build());
    }
}