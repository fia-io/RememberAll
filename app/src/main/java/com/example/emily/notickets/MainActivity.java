package com.example.emily.notickets;

//Emily Stuckey
//8-30-2018
//throwaway demo app

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.emily.notickets.data.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Task> taskArrayList = Arrays.asList(
            new Task("pay your ticket", 1, System.currentTimeMillis()),
            new Task("wave to Svava", 3, System.currentTimeMillis()),
            new Task("scratch Trima's belly", 4, System.currentTimeMillis())
    );

    private TextView textViewTaskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewTaskList = findViewById(R.id.tv_task_list);

        StringBuilder taskListForShowing = new StringBuilder();
        for (Task task : taskArrayList){
            taskListForShowing.append(task.toString() + "\n\n\n");
        }
        textViewTaskList.setText(taskListForShowing);
    }
}
