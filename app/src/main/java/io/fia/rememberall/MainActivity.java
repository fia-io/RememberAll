package io.fia.rememberall;

//Emily Stuckey
//8-30-2018
//throwaway demo app

import android.app.AlarmManager;
import android.app.DialogFragment;
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
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import io.fia.rememberall.Utils.DatePickerFragment;
import io.fia.rememberall.Utils.TimePickerFragment;
import io.fia.rememberall.data.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Task> taskArrayList = new ArrayList<>(Arrays.asList(
            new Task("Pay for parking", 1, System.currentTimeMillis()),
            new Task("Wave to Svava", 3, System.currentTimeMillis()),
            new Task("Scratch Trima's belly", 4, System.currentTimeMillis())
    ));
    public static final String CHANNEL_ID = "1";
    public static final int NOTIFICATION_ID = 1;
    public static final String TASK = "task";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    private TextView textViewTaskList;
    private TextView textViewDate;
    private TextView textViewTime;
    private EditText editTextDesc;

    private String taskToAlarm;

    private boolean alarmRepeats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        textViewTaskList = findViewById(R.id.tv_task_list);
        textViewDate = findViewById(R.id.tv_alarm_date);
        textViewTime = findViewById(R.id.tv_alarm_time);
        editTextDesc = findViewById(R.id.et_task_desc);
        alarmRepeats = false;

        updateTaskList();


    }

    public void updateTaskList(){
        StringBuilder taskListForShowing = new StringBuilder();
        for (Task task : taskArrayList){
            taskListForShowing.append(task.toString() + "\n\n\n");
        }
        textViewTaskList.setText(taskListForShowing);

    }

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



    public static void sendNotification(Context context, Class<?> cls, String description){

        Log.d(MainActivity.class.getSimpleName(), "sendNotification firing");
        Intent openAppIntent = new Intent(context, MainActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);



        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, 0);


        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_announcement_purple_24dp);

        //build the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setContentText(context.getResources().getString(R.string.notification_text) + ": " + description)
                .setSmallIcon(R.drawable.ic_announcement_purple_24dp)
                .setLargeIcon(largeIcon)
                //setPriority for everything before Oreo; importance in channel creation for Orer
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //send the notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, mBuilder.build());

    }

    public void onRadioButtonClicked(View view){

        if (view.getId() == R.id.rb_single_alarm){
            alarmRepeats = false;
        } else if (view.getId() == R.id.rb_repeating_alarm){
            alarmRepeats = true;
        }
    }

    //TODO: enforce future date/time
    public void setAlarm(View view){
        if (textViewDate.getText().toString().isEmpty() ||
                textViewTime.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.something_not_set, Toast.LENGTH_SHORT).show();
        }
        else {
            Log.d(MainActivity.class.getSimpleName(), "Setting alarm");
            String date = textViewDate.getText().toString();
            String time = textViewTime.getText().toString();
            String fulltime = date + " " + time;
            long reminderTime = System.currentTimeMillis();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy K:mm aa");

            try {
                Date mDate = simpleDateFormat.parse(fulltime);
                reminderTime = mDate.getTime();
            } catch (ParseException e){
                Toast.makeText(this, "Error parsing date/time", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            if (editTextDesc.getText().toString().isEmpty()){
                taskToAlarm = "";
            } else {
                taskToAlarm = editTextDesc.getText().toString();
            }

            final long alarmTime = reminderTime;

            taskArrayList.add(new Task(taskToAlarm, 4, reminderTime));
            updateTaskList();


            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra(TASK, taskToAlarm);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                    taskToAlarm.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
            if (alarmRepeats){
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent);
            }
            else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
            }

            Log.d(LOG_TAG, "alarm set for " + fulltime);
            long timeToWait = (alarmTime - System.currentTimeMillis())/60000;
            Log.d(LOG_TAG, "Should be " + timeToWait + " minutes from now");

            Toast.makeText(this, getResources().getString(R.string.alarm_set), Toast.LENGTH_SHORT).show();
            editTextDesc.setText("");
            textViewTime.setText("");
            textViewDate.setText("");
        }

    }

    public void showTimePickerDialog(View v){
        android.support.v4.app.DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.show(getSupportFragmentManager(),
                getResources().getString(R.string.time_picker));
    }

    public void showDatePickerDialog(View v){
        android.support.v4.app.DialogFragment dialogFragment= new DatePickerFragment();
        dialogFragment.show(getSupportFragmentManager(),
                getResources().getString(R.string.date_picker));
    }

}
