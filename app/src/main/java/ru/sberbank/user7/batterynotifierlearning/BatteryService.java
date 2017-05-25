package ru.sberbank.user7.batterynotifierlearning;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;

public class BatteryService extends Service {

    public static final int CRITICAL_BATTERY_LEVEL = 15;
    public static final int NOTIFICATION_ID = 0;
    public static final int REQUEST_CODE = 1;
    boolean flagShowNotifications = true;

    public BatteryService() {
    }

    @Override
    public void onCreate() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);

    }
    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int capacity = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

            float fPercent = ((float) level/ (float) capacity) * 100f;
            int percent = Math.round(fPercent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if(percent< CRITICAL_BATTERY_LEVEL){
                Notification.Builder builder = new Notification.Builder(BatteryService.this);
                Intent startBattery = new Intent();
                startBattery.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startBattery.setComponent(new ComponentName("ru.sberbank.user7.battery", "ru.sberbank.user7.battery.MainActivity"));
                builder.setContentIntent(PendingIntent.getActivity(BatteryService.this, REQUEST_CODE,startBattery, PendingIntent.FLAG_CANCEL_CURRENT));
                Intent startSelf = new Intent(BatteryService.this, BatteryService.class);

                if (flagShowNotifications){
                    startSelf.setAction(Intent.ACTION_DELETE);
                    builder.setDeleteIntent(PendingIntent.getService(BatteryService.this,2,startSelf,PendingIntent.FLAG_ONE_SHOT));
                }

                builder.setSmallIcon(R.drawable.ic_stat_battery_low);
                builder.setContentTitle(getString(R.string.notify_title));
                Notification notification = builder.getNotification();

                notificationManager.notify(NOTIFICATION_ID, notification);
            }else{
                notificationManager.cancel(NOTIFICATION_ID);
                flagShowNotifications = false;
            }

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent!=null&&Intent.ACTION_DELETE.equals(intent)){
            flagShowNotifications = false;
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }
}
