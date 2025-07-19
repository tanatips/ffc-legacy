package th.in.ffc.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import th.in.ffc.R;
import th.in.ffc.app.FFCFragmentActivity;
import th.in.ffc.intent.Action;
import th.in.ffc.security.CryptographerService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class EncryptDbService extends Service {
    public static final String NAME = "th.in.ffc.service.EncryptDbService";
    public static final String EXTRA_SUCCESS = "success";
    public static final String EXTRA_MESSAGE = "msg";

    private static final int NOTIFICATION_ID = 9999;
    private static final String CHANNEL_ID = "EncryptDbServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("EncryptDbService", "onTaskRemoved called - App was swiped away");

        try {
            Intent encrypter = new Intent(getApplicationContext(), CryptographerService.class);
            encrypter.setAction(Action.ENCRYPT);
            startService(encrypter);
            Log.d("EncryptDbService", "CryptographerService started successfully");
        } catch (Exception e) {
            Log.e("EncryptDbService", "Error starting CryptographerService: " + e.getMessage());
        }

        // หยุด service หลังจากทำงานเสร็จ
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("EncryptDbService", "Service started");
        return START_STICKY; // เปลี่ยนเป็น START_STICKY
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("EncryptDbService", "Service destroyed");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "EncryptDb Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Service for handling app encryption");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FFC Background Service")
                .setContentText("Monitoring app state")
                .setSmallIcon(R.drawable.ic_launcher) // ใช้ icon ที่มีในแอป
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }
}