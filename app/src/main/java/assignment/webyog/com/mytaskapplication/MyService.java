package assignment.webyog.com.mytaskapplication;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Uppu on 6/30/2017.
 */

public class MyService extends IntentService {

    public static Notification notification;
    public static long time=0;
    public Bundle bd;

    public MyService () {
        super("MyService");
    }
    public MyService(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        bd = intent.getExtras();
        if(bd!=null) {
            time = (Long) bd.get("time");
            //this is for unique id of every pending intent, using system time in millis
            final int id = (int) System.currentTimeMillis();
            Intent notificationIntent = new Intent(this, BroadCastReceiver.class);
            notificationIntent.putExtra(BroadCastReceiver.NOTIFICATION_ID, id);
            notificationIntent.putExtra(BroadCastReceiver.NOTIFICATION, notification);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags , startId);
        return Service.START_STICKY;
    }
}
