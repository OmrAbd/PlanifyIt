package com.example.planifyit.utilities;

import android.Manifest;
import android.accounts.Account;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.JsonReader;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.BundleCompat;
import androidx.core.app.NotificationCompat;

import com.example.planifyit.MainActivity;
import com.example.planifyit.NotificationReceiver;
import com.example.planifyit.R;
import com.example.planifyit.model.Task;
import com.example.planifyit.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SaveAndLoad {

    private SaveAndLoad() {
    }

    public static void loadTasks(Context context) throws IOException, JSONException {

        File directory = context.getFilesDir();
        if (!directory.exists())
            directory.mkdirs();

        File file = new File(directory, "data.json");
        if (!file.createNewFile()) {
            FileReader fr = new FileReader(file);
            JsonReader jsonReader = new JsonReader(fr);

            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {

                    jsonReader.nextName();
                    String title = jsonReader.nextString();

                    jsonReader.nextName();
                    String description = jsonReader.nextString();

                    jsonReader.nextName();
                    int year = jsonReader.nextInt();

                    jsonReader.nextName();
                    int month = jsonReader.nextInt();

                    jsonReader.nextName();
                    int day = jsonReader.nextInt();

                    jsonReader.nextName();
                    int hour = jsonReader.nextInt();

                    jsonReader.nextName();
                    int minute = jsonReader.nextInt();

                    LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute);;

                    MainActivity.dontLoad = true;


                    Task t = new Task(title, description, dateTime);
                    User.getTasks().add(t);

                }
                jsonReader.endObject();

            }
            jsonReader.endArray();

            jsonReader.close();

        }
        else {
            FileWriter fw = new FileWriter(file);
            fw.write("[]");
            fw.close();
        }



    }

    public static void saveTasks(Context context) throws IOException, JSONException {
        File directory = context.getFilesDir();
        if (!directory.exists())
            if (!directory.mkdirs())
                System.exit(-1);
        File file = new File(directory, "data.json");
        FileWriter fw = new FileWriter(file);
        JSONArray taskArray = new JSONArray();
        for (Task t : User.getTasks()) {
            JSONObject task = new JSONObject();

            String title = t.getTitle();
            String description = t.getDescription();
            int year = t.getDate().getYear();
            int month = t.getDate().getMonthValue();
            int day = t.getDate().getDayOfMonth();
            int hour = t.getDate().getHour();
            int minute = t.getDate().getMinute();

            task.put("title", title);
            task.put("desc", description);
            task.put("year", year);
            task.put("month", month);
            task.put("day", day);
            task.put("hour", hour);
            task.put("minute", minute);

            taskArray.put(task);
        }
        fw.write(taskArray.toString());
        fw.close();

    }



}
