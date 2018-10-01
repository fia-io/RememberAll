package com.example.emily.notickets;

//Emily Stuckey
//8-30-2018
//throwaway demo app

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emily.notickets.data.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Task> taskArrayList = Arrays.asList(
            new Task("Pay for parking", 1, System.currentTimeMillis()),
            new Task("Wave to Svava", 3, System.currentTimeMillis()),
            new Task("Scratch Trima's belly", 4, System.currentTimeMillis())
    );
    public static final String CHANNEL_ID = "1";
    public static final int NOTIFICATION_ID = 1;


    private AlarmManager alarmManager;
    private BroadcastReceiver mReceiver;
    private PendingIntent pendingIntent1;


    private TextView textViewTaskList;

    //This is necessary for Oreo and above but anything lower doesn't need it
    //see https://developer.android.com/guide/topics/ui/notifiers/notifications
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        textViewTaskList = findViewById(R.id.tv_task_list);

        StringBuilder taskListForShowing = new StringBuilder();
        for (Task task : taskArrayList){
            taskListForShowing.append(task.toString() + "\n\n\n");
        }
        textViewTaskList.setText(taskListForShowing);

        RegisterAlarmBroadcast();

    }

    public void sendNotification(View view) {
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 30000,
                pendingIntent1);
    }

    public void sendNotification(){

        Intent openAppIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, 0);


        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_announcement_purple_24dp);

        //build the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getResources().getString(R.string.notification_title))
                .setContentText(getResources().getString(R.string.notification_text))
                .setSmallIcon(R.drawable.ic_announcement_purple_24dp)
                .setLargeIcon(largeIcon)
                //setPriority for everything before Oreo; importance in channel creation for Orer
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //send the notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, mBuilder.build());



    }

    private void RegisterAlarmBroadcast(){
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(MainActivity.class.getSimpleName(), "Alarming");
                Toast.makeText(context, "Hi!", Toast.LENGTH_SHORT).show();
                sendNotification();
            }
        };
        registerReceiver(mReceiver, new IntentFilter("com.example.emily.notickets"));
        pendingIntent1 = PendingIntent.getBroadcast(this, 0,
                new Intent("com.example.emily.notickets"), 0);
        alarmManager = (AlarmManager)(this.getSystemService(Context.ALARM_SERVICE));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
