package com.example.planifyit.utilities;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

public class CalendarUtilities{

    public static long getEventIdByTitleAndTime(Context context, String eventTitle, LocalDateTime dateTime) {
        ContentResolver contentResolver = context.getContentResolver();


        // Define the projection (columns to retrieve)
        String[] projection = {CalendarContract.Events._ID};

        // Define the selection criteria
        String selection = CalendarContract.Events.TITLE + " = ? AND "
                + CalendarContract.Events.DTSTART + " = ? AND "
                + CalendarContract.Events.DTEND + " = ?";
        String[] selectionArgs = {
                eventTitle,
                String.valueOf(dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()),
                String.valueOf(dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
        };


        Cursor cursor;

        long eventId = -1; // Default value if event ID not found

        // Query the calendar events table
        cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null);



        if (cursor != null && cursor.moveToFirst()) {
            // Retrieve the event ID from the cursor
            int eventIdColumnIndex = cursor.getColumnIndex(CalendarContract.Events._ID);
            eventId = cursor.getLong(eventIdColumnIndex);
            cursor.close();
        }

        return eventId;
    }
    public static long getEventIdByTitle(Context context, String eventTitle) {
        ContentResolver contentResolver = context.getContentResolver();


        // Define the projection (columns to retrieve)
        String[] projection = {CalendarContract.Events._ID};

        // Define the selection criteria
        String selection = CalendarContract.Events.TITLE + " = ?";
        String[] selectionArgs = {
                eventTitle,
        };


        Cursor cursor;

        long eventId = -1; // Default value if event ID not found

        // Query the calendar events table
        cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);



        if (cursor != null && cursor.moveToFirst()) {
            // Retrieve the event ID from the cursor
            int eventIdColumnIndex = cursor.getColumnIndex(CalendarContract.Events._ID);
            eventId = cursor.getLong(eventIdColumnIndex);
            cursor.close();
        }

        return eventId;
    }
    public static void addCalendarReminder(Context context, String title, String description, LocalDateTime dateAndTime) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();

        Long startTimeInMillis = dateAndTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        contentValues.put(CalendarContract.Events.CALENDAR_ID, getDefaultCalendarId(context));
        contentValues.put(CalendarContract.Events.TITLE, title);
        contentValues.put(CalendarContract.Events.DTSTART, startTimeInMillis);
        contentValues.put(CalendarContract.Events.DTEND, startTimeInMillis);
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        contentValues.put(CalendarContract.Events.HAS_ALARM, 1);
        contentValues.put(CalendarContract.Events.DESCRIPTION, description);

        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);
        long eventID = Long.parseLong(uri.getLastPathSegment());

        // Set a reminder for the event
        ContentValues reminderValues = new ContentValues();
        reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminderValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminderValues.put(CalendarContract.Reminders.MINUTES, 0);
        contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);

    }
    public static boolean removeCalendarReminder(Context context, Long eventId){

        ContentResolver contentResolver = context.getContentResolver();
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int rowsDeleted = contentResolver.delete(deleteUri, null, null);

        return rowsDeleted > 0;
    }
    public static boolean hasPermission(Context context, String permission) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private static long getDefaultCalendarId(Context context) {
        long calendarId = -1;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] projection = new String[]{CalendarContract.Calendars._ID};
        String selection = CalendarContract.Calendars.IS_PRIMARY + " = 1";

        Cursor cursor = contentResolver.query(uri, projection, selection, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex(CalendarContract.Calendars._ID);
            calendarId = cursor.getLong(idColumnIndex);
            cursor.close();
        }

        return calendarId;
    }
    public static void openAppSettings(Context context){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
        System.exit(0);
    }

}
