package com.example.planifyit;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.planifyit.utilities.CalendarUtilities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;

public class TaskHandlingActivity extends AppCompatActivity {

    private boolean newTask;
    private int editPosition;
    private ConstraintLayout taskHandlerView;
    private EditText titleField,  descriptionField;
    private Button datePickerButton, timePickerButton;
    private DatePicker datePicker;
    private TimePicker timePicker;

    private FloatingActionButton taskHandlerValidateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_handler);

        Intent i = getIntent();
        newTask = i.getBooleanExtra("NEWTASK", false);
        editPosition = i.getIntExtra("POSITION", -1);

        taskHandlerView = findViewById(R.id.taskHandler_parent);

        taskHandlerValidateButton = findViewById(R.id.taskHandlerValidateButton);

        titleField = findViewById(R.id.taskHandlerTitle_editText);
        descriptionField = findViewById(R.id.taskHandlerDescription_editText);
        datePicker = findViewById(R.id.taskHandlerDatePicker);
        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerButton = findViewById(R.id.taskHandlerDatePicker_button);
        timePicker = findViewById(R.id.taskHandlerTimePicker);
        timePickerButton = findViewById(R.id.taskHandlerTimePicker_button);

        titleField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(taskHandlerView);
                if ( s.length() > 0 ){
                    constraintSet.clear(titleField.getId(), ConstraintSet.BOTTOM);
                    constraintSet.connect(descriptionField.getId(), ConstraintSet.TOP, titleField.getId(), ConstraintSet.BOTTOM);
                    taskHandlerValidateButton.setForegroundTintList(getColorStateList(R.color.validate_icon));
                    Drawable bG = taskHandlerValidateButton.getBackground();
                    if(bG != null)
                        bG.setTintList(getColorStateList(R.color.button));
                    taskHandlerValidateButton.setEnabled(true) ;
                }
                else {
                    constraintSet.connect(titleField.getId(), ConstraintSet.BOTTOM, taskHandlerView.getId(), ConstraintSet.BOTTOM);
                    constraintSet.connect(descriptionField.getId(), ConstraintSet.TOP, taskHandlerView.getId(), ConstraintSet.BOTTOM);
                    taskHandlerValidateButton.setForegroundTintList(getColorStateList(R.color.black));
                    Drawable bG = taskHandlerValidateButton.getBackground();
                    if(bG != null)
                        bG.setTintList(getColorStateList(R.color.disabled));

                    //taskHandlerValidateButton.getBackground().setTintList(getColorStateList(R.color.disabled));
                    taskHandlerValidateButton.setEnabled(false);
                }
                constraintSet.applyTo(taskHandlerView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.setVisibility( View.INVISIBLE );
                datePicker.setVisibility( datePicker.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE );
                v.setRotation( (180 + v.getRotation()) % 360 );
                timePickerButton.setRotation( timePicker.getVisibility() == View.VISIBLE ? 180 : 0 );
            }
        });

        timePickerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                datePicker.setVisibility( View.INVISIBLE );
                timePicker.setVisibility( timePicker.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE );
                v.setRotation( (180 + v.getRotation()) % 360 );
                datePickerButton.setRotation( datePicker.getVisibility() == View.VISIBLE ? 180 : 0 );
            }
        });

        timePicker.setIs24HourView(shouldUse24HourFormat());

        taskHandlerValidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MainActivity.class);
                i.putExtra("TASK_Title", titleField.getText().toString());
                i.putExtra("TASK_Desc", descriptionField.getText().toString());
                i.putExtra("TASK_Year", datePicker.getYear());
                i.putExtra("TASK_Month", datePicker.getMonth());
                i.putExtra("TASK_Day", datePicker.getDayOfMonth());
                i.putExtra("TASK_Hour", timePicker.getHour());
                i.putExtra("TASK_Minute", timePicker.getMinute());

                LocalDateTime dateTime = LocalDateTime.of(datePicker.getYear(), datePicker.getMonth()+1, datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute());

                if(newTask){
                    i.putExtra("ADD_Task", true);
                    i.putExtra("TASK_Month", datePicker.getMonth());

                    if(!CalendarUtilities.hasPermission(v.getContext(), "android.permission.READ_CALENDAR")){
                        Toast.makeText(v.getContext(), "This app need the agenda permission", Toast.LENGTH_SHORT).show();
                        CalendarUtilities.openAppSettings(v.getContext());
                    }
                    CalendarUtilities.addCalendarReminder(v.getContext(), titleField.getText().toString(), descriptionField.getText().toString(), dateTime);

                }
                else {

                    Long eventId = CalendarUtilities.getEventIdByTitle(v.getContext(), titleField.getText().toString());

                    if (eventId != -1){
                        if(!CalendarUtilities.hasPermission(v.getContext(), "android.permission.WRITE_CALENDAR")){
                            Toast.makeText(v.getContext(), "This app need the agenda permission", Toast.LENGTH_SHORT).show();
                            CalendarUtilities.openAppSettings(v.getContext());
                        }
                        CalendarUtilities.removeCalendarReminder(v.getContext(), CalendarUtilities.getEventIdByTitle(v.getContext(), titleField.getText().toString()));

                    }
                    if(!CalendarUtilities.hasPermission(v.getContext(), "android.permission.READ_CALENDAR")){
                        Toast.makeText(v.getContext(), "This app need the agenda permission", Toast.LENGTH_SHORT).show();
                        CalendarUtilities.openAppSettings(v.getContext());
                    }
                    CalendarUtilities.addCalendarReminder(v.getContext(), titleField.getText().toString(), descriptionField.getText().toString(), dateTime);

                    i.putExtra("EDIT_Task", true);
                    i.putExtra("EDIT_Position", editPosition);
                }
                startActivity(i);
                finish();
            }
        });

        if(!newTask){
            titleField.setText(i.getStringExtra("TITLE"));
            titleField.setInputType(InputType.TYPE_NULL);
            descriptionField.setText(i.getStringExtra("DESC"));
            datePicker.init( i.getIntExtra("LOCAL_Year", 0), i.getIntExtra("LOCAL_Month", 0), i.getIntExtra("LOCAL_Day", 0), null );
            timePicker.setHour( i.getIntExtra("LOCAL_Hour", 0) );
            timePicker.setMinute( i.getIntExtra("LOCAL_Minute", 0) );
        }

    }

    public static boolean shouldUse24HourFormat() {
        // Get the current device's locale
        Locale currentLocale = Locale.getDefault();

        // Create a DateFormat instance with the default time format for the current locale
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, currentLocale);

        // Get the pattern used by the time format
        String pattern = ((SimpleDateFormat) timeFormat).toPattern();

        // Check if the pattern contains "H" or "k" indicating 24-hour format
        return pattern.contains("H") || pattern.contains("k");
    }


}

