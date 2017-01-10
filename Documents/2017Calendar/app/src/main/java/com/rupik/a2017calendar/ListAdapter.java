package com.rupik.a2017calendar;

/**
 * Created by boom on 12/12/16.
 */

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by macmin5 on 25/10/16.
 */
public class ListAdapter extends BaseAdapter {
    DateObj dateObj;
    Context context;
    String[] occasions;
    String[] userEvents;
    private static LayoutInflater inflater=null;

    public ListAdapter (Context context, DateObj dateObj)
    {
        this.context = context;
        this.dateObj = dateObj;

        String occasionsString = dateObj.getOccasionName();
        if(occasionsString!=null && occasionsString.length()>0)
            occasions =  occasionsString.split(", ");

        String userEventsString = dateObj.getUserEvents();
        if(userEventsString!=null && userEventsString.length()>0)
            userEvents =  userEventsString.split(", ");

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        int count = 0;
        if(occasions!=null)
        {
            count += occasions.length;
        }
        if(userEvents!=null)
        {
            count += userEvents.length;
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        int count = 0;
        if(occasions!=null)
            count = occasions.length;
        if(position<count)
            return occasions[position];
        else
            return  userEvents[position-count];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public class Holder
    {
        TextView tv;
        ImageButton imgBtn;
        ImageButton deleteBtn;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.events_list_cell, null);
        holder.tv = (TextView) rowView.findViewById(R.id.list_cell_eventsTV);
        holder.imgBtn = (ImageButton) rowView.findViewById(R.id.list_cell_edit_btn);
        holder.deleteBtn = (ImageButton) rowView.findViewById(R.id.list_cell_delete_btn);

        if(occasions!= null && position<occasions.length)
        {
            holder.imgBtn.setVisibility(View.GONE);
            holder.deleteBtn.setVisibility(View.GONE);
        }
        else {
            holder.imgBtn.setVisibility(View.VISIBLE);
            holder.deleteBtn.setVisibility(View.VISIBLE);
        }



        final String occasionsText = (String)getItem(position);
        holder.tv.setText(occasionsText);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateObj.getDate());
                String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
                int year = cal.get(Calendar.YEAR);
                int date = cal.get(Calendar.DATE);
                String dateString = Integer.toString(date) + "-" + monthName + "-" + Integer.toString(year);

                SharedPreferences prefs = context.getSharedPreferences("Cal2016UserEvents", context.MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = prefs.edit();

                int deleteIndex = -1;
                int lastIndex = -1;
                for (int i = 0; i < 1000; i++) {
                    String dateKey = dateString + "-" + Integer.toString(i) + "-userEvent";
                    String userEvent = prefs.getString(dateKey, "");
                    if (userEvent.length() > 0) {
                        if(userEvent.contains(occasionsText))
                        {
                            deleteIndex = i;
//                            prefEditor.remove(dateKey);
//                            prefEditor.commit();
//                            ((Activity)context).finish();
//                            break;
                        }
                        lastIndex = i;
                    }
                    else {
                        break;
                    }
                }

                int deleteLastIndex = -1;
                for (int i = 0; i < 1000; i++) {
                    if(deleteIndex==-1 || lastIndex== -1)
                    {
                        //something is not right... Don't do anything
                        break;
                    }
                    if(deleteIndex == 0 && deleteIndex==lastIndex)
                    {
                        deleteLastIndex = deleteIndex;
                        break;
                    }
                    if(deleteIndex!=0 && deleteIndex==lastIndex)
                    {
                        deleteLastIndex = deleteIndex;
                        break;
                    }
                    if(i<=deleteIndex)
                    {
                        continue;
                    }

                    String dateKey = dateString + "-" + Integer.toString(i) + "-userEvent";
                    String userEvent = prefs.getString(dateKey, "");
                    if (userEvent.length() > 0) {
                        //assign to i-1th index as one index is to be deleted
                        String newDateKey = dateString + "-" + Integer.toString(i-1) + "-userEvent";
                        prefEditor.putString(newDateKey, userEvent);
                        deleteLastIndex = i;
                    }
                }
                if(deleteLastIndex!=-1) {
                    String newDateKey = dateString + "-" + Integer.toString(deleteLastIndex) + "-userEvent";
                    prefEditor.remove(newDateKey);
                    prefEditor.commit();
                }

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                        new ComponentName(context, WidgetReceiver.class));
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.calwidgetListView);

                ((Activity)context).finish();
            }
        });

        holder.imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEventActivity activity = (AddEventActivity)context;

                Calendar cal = Calendar.getInstance();
                cal.setTime(dateObj.getDate());
                String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
                int year = cal.get(Calendar.YEAR);
                int date = cal.get(Calendar.DATE);
                String dateString = Integer.toString(date) + "-" + monthName + "-" + Integer.toString(year);

                SharedPreferences prefs = context.getSharedPreferences("Cal2016UserEvents", context.MODE_PRIVATE);
                String targetDateKey = "";
                for (int i = 0; i < 1000; i++) {
                    String dateKey = dateString + "-" + Integer.toString(i) + "-userEvent";
                    String userEvent = prefs.getString(dateKey, "");
                    if (userEvent.length() > 0) {
                        if (userEvent.contains(occasionsText)) {
                            targetDateKey = dateKey;
                        }
                    }
                }

                if(targetDateKey.length()>1) {
                    activity.editEntry(occasionsText, targetDateKey);
                }
            }
        });

        return rowView;
    }
}
