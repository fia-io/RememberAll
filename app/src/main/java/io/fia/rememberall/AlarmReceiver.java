package io.fia.rememberall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String description = intent.getStringExtra(MainActivity.TASK);
        Log.d(AlarmReceiver.class.getSimpleName(), "Alarm firing");
        MainActivity.sendNotification(context, MainActivity.class, description);
    }
}
