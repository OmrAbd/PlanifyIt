package com.example.planifyit.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planifyit.R;
import com.example.planifyit.TaskHandlingActivity;
import com.example.planifyit.model.Task;
import com.example.planifyit.model.User;
import com.example.planifyit.utilities.CalendarUtilities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;


public class TasksRecyclerViewAdapter extends RecyclerView.Adapter<TasksRecyclerViewAdapter.ViewHolder> {

    public TasksRecyclerViewAdapter(){
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.taskTitle.setText(User.getTasks().get(position).getTitle());
        holder.taskDescription.setText(User.getTasks().get(position).getDescription());

        holder.taskYear.setText(String.valueOf(User.getTasks().get(position).getDate().getYear()));
        holder.taskMonth.setText( (User.getTasks().get(position).getDate().getMonthValue() < 10 ? "0" : "") + User.getTasks().get(position).getDate().getMonthValue());
        holder.taskDay.setText( (User.getTasks().get(position).getDate().getDayOfMonth() < 10 ? "0" : "") + User.getTasks().get(position).getDate().getDayOfMonth());

        if(TaskHandlingActivity.shouldUse24HourFormat()){
            holder.taskHour.setText( (User.getTasks().get(position).getDate().getHour() < 10 ? "0" : "") + User.getTasks().get(position).getDate().getHour());
            holder.taskAMPM.setVisibility(View.GONE);

        }
        else {
            int hour = User.getTasks().get(position).getDate().getHour();
            if (hour >= 12){
                holder.taskAMPM.setText("PM");
                if (hour == 12){
                    holder.taskHour.setText("12");
                }else {
                    holder.taskHour.setText( (hour - 12 < 10 ? "0" : "") + (hour - 12) );
                }
            }else {
                holder.taskAMPM.setText("AM");
                if (hour == 0){
                    holder.taskHour.setText("12");
                }else {
                    holder.taskHour.setText( (hour < 10 ? "0" : "") + hour );
                }
            }
        }

        holder.taskMinute.setText( (User.getTasks().get(position).getDate().getMinute() < 10 ? "0" : "") + User.getTasks().get(position).getDate().getMinute());


        holder.taskEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = holder.taskTitle.getText().toString();
                String description = holder.taskDescription.getText().toString();
                String year = holder.taskYear.getText().toString();
                String month = holder.taskMonth.getText().toString();
                String day = holder.taskDay.getText().toString();
                String hour = holder.taskHour.getText().toString();
                String minute = holder.taskMinute.getText().toString();
                if(LocalDateTime.of(
                        Integer.parseInt(holder.taskYear.getText().toString()),
                        Integer.parseInt(holder.taskMonth.getText().toString()),
                        Integer.parseInt(holder.taskDay.getText().toString()),
                        Integer.parseInt(holder.taskHour.getText().toString()),
                        Integer.parseInt(holder.taskMinute.getText().toString())
                ).isBefore(LocalDateTime.now())
                ){
                    holder.taskYear.setTextColor(holder.itemView.getContext().getColor(R.color.bad));
                    holder.taskMonth.setTextColor(holder.itemView.getContext().getColor(R.color.bad));
                    holder.taskDay.setTextColor(holder.itemView.getContext().getColor(R.color.bad));
                    holder.taskHour.setTextColor(holder.itemView.getContext().getColor(R.color.bad));
                    holder.taskMinute.setTextColor(holder.itemView.getContext().getColor(R.color.bad));
                    holder.lateIcon.setVisibility(View.VISIBLE);
                }else {
                    holder.taskYear.setTextColor(holder.itemView.getContext().getColor(R.color.button));
                    holder.taskMonth.setTextColor(holder.itemView.getContext().getColor(R.color.button));
                    holder.taskDay.setTextColor(holder.itemView.getContext().getColor(R.color.button));
                    holder.taskHour.setTextColor(holder.itemView.getContext().getColor(R.color.button));
                    holder.taskMinute.setTextColor(holder.itemView.getContext().getColor(R.color.button));
                    holder.lateIcon.setVisibility(View.INVISIBLE);
                }

                Context context = v.getContext();
                Intent intent = new Intent(context, TaskHandlingActivity.class);
                intent.putExtra("POSITION", holder.getAdapterPosition());
                intent.putExtra("TITLE", title);
                intent.putExtra("DESC", description);

                intent.putExtra("LOCAL_Year", Integer.parseInt(year));
                intent.putExtra("LOCAL_Month", Integer.parseInt(month) - 1);
                intent.putExtra("LOCAL_Day", Integer.parseInt(day));
                intent.putExtra("LOCAL_Hour", Integer.parseInt(hour));
                intent.putExtra("LOCAL_Minute", Integer.parseInt(minute));
                context.startActivity(intent);

            }
        });

        holder.taskValidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task tToRemove = User.getTasks().get(holder.getAdapterPosition());

                Long eventID = CalendarUtilities.getEventIdByTitleAndTime(v.getContext(), tToRemove.getTitle(), tToRemove.getDate());

                if(eventID != -1){
                    if(!CalendarUtilities.hasPermission(v.getContext(), "android.permission.WRITE_CALENDAR")){
                        Toast.makeText(v.getContext(), "This app need the agenda permission", Toast.LENGTH_SHORT).show();
                        CalendarUtilities.openAppSettings(v.getContext());
                    }
                    CalendarUtilities.removeCalendarReminder(v.getContext(), eventID);
                }

                User.getTasks().remove(tToRemove);

                notifyItemRemoved(holder.getAdapterPosition());
            }
        });

        holder.taskExtendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.taskDescription.setMaxLines( holder.taskDescription.getMaxLines() != 3 ? 3 : 10 );
                v.setRotation( holder.taskDescription.getMaxLines() != 3 ? 180 : 0 );
            }
        });

        if(LocalDateTime.of(
                Integer.parseInt(holder.taskYear.getText().toString()),
                Integer.parseInt(holder.taskMonth.getText().toString()),
                Integer.parseInt(holder.taskDay.getText().toString()),
                Integer.parseInt(holder.taskHour.getText().toString()),
                Integer.parseInt(holder.taskMinute.getText().toString())
            ).isBefore(LocalDateTime.now())
        ){
            holder.taskYear.setTextColor(holder.itemView.getContext().getColor(R.color.bad));
            holder.taskMonth.setTextColor(holder.itemView.getContext().getColor(R.color.bad));
            holder.taskDay.setTextColor(holder.itemView.getContext().getColor(R.color.bad));
            holder.taskHour.setTextColor(holder.itemView.getContext().getColor(R.color.bad));
            holder.taskMinute.setTextColor(holder.itemView.getContext().getColor(R.color.bad));
            holder.lateIcon.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return User.getTasks().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView taskTitle, taskDescription;

        private ImageView lateIcon;
        private LinearLayout taskDateLayout;
        private TextView taskYear, taskMonth, taskDay, taskHour, taskMinute, taskAMPM;
        private FloatingActionButton taskEditButton, taskValidateButton, taskExtendButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lateIcon = itemView.findViewById(R.id.taskLateIcon);

            taskTitle = itemView.findViewById(R.id.taskRecyclerViewItem_title);
            taskDescription = itemView.findViewById(R.id.taskRecyclerViewItem_description);

            taskDateLayout = itemView.findViewById(R.id.taskRecyclerViewItem_dateLinearLayout);

            taskYear = taskDateLayout.findViewById(R.id.taskRecyclerViewItem_dateLinearLayout_year);
            taskMonth = taskDateLayout.findViewById(R.id.taskRecyclerViewItem_dateLinearLayout_month);
            taskDay = taskDateLayout.findViewById(R.id.taskRecyclerViewItem_dateLinearLayout_day);
            taskHour = taskDateLayout.findViewById(R.id.taskRecyclerViewItem_dateLinearLayout_hour);
            taskMinute = taskDateLayout.findViewById(R.id.taskRecyclerViewItem_dateLinearLayout_minute);

            taskAMPM = taskDateLayout.findViewById(R.id.taskRecyclerViewItem_dateLinearLayout_AMPM);

            taskEditButton = itemView.findViewById(R.id.taskEditButton);
            taskValidateButton = itemView.findViewById(R.id.taskValidateButton);
            taskExtendButton = itemView.findViewById(R.id.taskExtendButton);

        }

    }



}
