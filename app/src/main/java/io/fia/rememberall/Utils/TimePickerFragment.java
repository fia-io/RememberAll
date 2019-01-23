package io.fia.rememberall.Utils;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.widget.TextView;
import android.widget.TimePicker;

import io.fia.rememberall.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//TODO: modify to conform to local time
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute,
                android.text.format.DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        TextView tv_time = getActivity().findViewById(R.id.tv_alarm_time);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(0, 0, 0, hourOfDay, minute, 0);
        Date chosenTime = cal.getTime();
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
        String formattedTime = df.format(chosenTime);
        tv_time.setText(formattedTime);

    }
}
