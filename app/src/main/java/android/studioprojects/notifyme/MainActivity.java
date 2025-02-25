package android.studioprojects.notifyme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {


    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.android.example.notifyme.ACTION_UPDATE_NOTIFICATION";

    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";

    private static final int NOTIFICATION_ID = 0;

    private Button button_notify;
    private Button button_cancel;
    private Button button_update;

    private NotificationManager mNotifyManager;

    private NotificationReceiver mReceiver = new NotificationReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        createNotificationChannel();
        registerReceiver(mReceiver,
                new IntentFilter(ACTION_UPDATE_NOTIFICATION));


        button_notify = findViewById(R.id.notify);
        button_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });

        button_update = (Button) findViewById(R.id.update);
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNotification();
            }
        });

        button_cancel = (Button) findViewById(R.id.cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancelNotification();
            }
        });


        setNotificationButtonState(true, false, false);
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }


    public void createNotificationChannel() {


        mNotifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            getString(R.string.notification_channel_name),
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    (getString(R.string.notification_channel_description));

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    public void sendNotification() {


        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this,
                NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();


        notifyBuilder.addAction(R.drawable.ic_update,
                getString(R.string.update), updatePendingIntent);


        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());

        setNotificationButtonState(false, true, true);
    }

    private NotificationCompat.Builder getNotificationBuilder() {


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity
                (this, NOTIFICATION_ID, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notifyBuilder = new NotificationCompat
                .Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(R.drawable.ic_android)
                .setAutoCancel(true).setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return notifyBuilder;
    }


    public void updateNotification() {


        Bitmap androidImage = BitmapFactory
                .decodeResource(getResources(), R.drawable.mascot_1);

        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();


        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle(getString(R.string.notification_updated)));

        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());

        setNotificationButtonState(false, false, true);
    }


    public void cancelNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID);

        setNotificationButtonState(true, false, false);
    }

    void setNotificationButtonState(Boolean isNotifyEnabled, Boolean
            isUpdateEnabled, Boolean isCancelEnabled) {
        button_notify.setEnabled(isNotifyEnabled);
        button_update.setEnabled(isUpdateEnabled);
        button_cancel.setEnabled(isCancelEnabled);
    }

    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {
        }


        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }
    }
}
