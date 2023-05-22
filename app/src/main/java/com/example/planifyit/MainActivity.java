package com.example.planifyit;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planifyit.adapters.TasksRecyclerViewAdapter;
import com.example.planifyit.model.Task;
import com.example.planifyit.model.User;
import com.example.planifyit.utilities.SaveAndLoad;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    private int editPosition;
    private boolean editTask;
    private boolean addTask;

    public static boolean dontLoad;

    private LocalDateTime updatedDateTime;
    private RecyclerView taskRecyclerView;

    private FloatingActionButton newTaskButton;

    private static TasksRecyclerViewAdapter tasksRecyclerViewAdapter = new TasksRecyclerViewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        editTask = i.getBooleanExtra("EDIT_Task", false);
        addTask = i.getBooleanExtra("ADD_Task", false);
        editPosition = i.getIntExtra("EDIT_Position", -1);

        if(editTask || addTask){
            updatedDateTime = LocalDateTime.of(
                    i.getIntExtra("TASK_Year", -1),
                    i.getIntExtra("TASK_Month", -1) + 1,
                    i.getIntExtra("TASK_Day", -1),
                    i.getIntExtra("TASK_Hour", -1),
                    i.getIntExtra("TASK_Minute", -1)
                    );
            if (editTask){
                User.getTasks().get(editPosition).setTitle(i.getStringExtra("TASK_Title"));
                User.getTasks().get(editPosition).setDescription(i.getStringExtra("TASK_Desc"));
                User.getTasks().get(editPosition).setDate(updatedDateTime);
                tasksRecyclerViewAdapter.notifyItemChanged(editPosition);
                i.removeExtra("EDIT_Position");
            }

            if (addTask){
                i.removeExtra("ADD_Task");
                User.getTasks().add(new Task(i.getStringExtra("TASK_Title"), i.getStringExtra("TASK_Desc"), updatedDateTime)  );
                tasksRecyclerViewAdapter.notifyItemInserted(User.getTasks().size() - 1);
            }
            i.removeExtra("TASK_Title");
            i.removeExtra("TASK_Desc");
            i.removeExtra("TASK_Year");
            i.removeExtra("TASK_Month");
            i.removeExtra("TASK_Day");
            i.removeExtra("TASK_Hour");
            i.removeExtra("TASK_Minute");

        }else {
            if(!dontLoad){
                try {
                    SaveAndLoad.loadTasks(this);
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            else
                dontLoad = false;

            i.removeExtra("DONT_LOAD");
        }

        taskRecyclerView = findViewById(R.id.taskRecyclerView);

        newTaskButton = findViewById(R.id.mainActivityNewTaskButton);
        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNewTask();
            }
        });

        // Sort the task to display them by time order
        User.getTasks().sort(Comparator.comparing(Task::getDate));


        taskRecyclerView.setAdapter(tasksRecyclerViewAdapter);
        taskRecyclerView.setLayoutManager(getResources().getConfiguration().screenWidthDp < 300 ? new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) : new GridLayoutManager(this, 2) );

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.screenWidthDp < 300){
            taskRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }else {
            taskRecyclerView.setLayoutManager(new GridLayoutManager(this,newConfig.screenWidthDp / 300));
        }

    }

    public void makeNewTask(){
        Intent intent = new Intent(this, TaskHandlingActivity.class);
        intent.putExtra("NEWTASK", true);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            SaveAndLoad.saveTasks(this);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }
}