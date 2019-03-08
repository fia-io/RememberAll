package io.fia.rememberall.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Task {
    int ID;
    String title;
    int priority;
    long time;

    public Task(String title, int priority, long time){
        this.title = title;
        this.priority = priority;
        this.time = time;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm");

        return title + "\n" +
                "priority=" + priority + "\n" +
                "time=" + formatter.format(time);
    }

    //TODO: we may need an id field to enforce uniqueness
    //this is only to generate a code for alarms, so
    //doesn't need to be *too* unique
    @Override
    public int hashCode() {
        return title.hashCode();
    }
}
