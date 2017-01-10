package com.rupik.a2017calendar;

/**
 * Created by boom on 12/12/16.
 */

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by macmin5 on 18/10/16.
 */
public class GridAdapter extends ArrayAdapter {
    Context context;
    private int layoutResourceId;
    Date todaysDate;
    Date displayedDate;
    private ArrayList<DateObj> data = new ArrayList();

    public GridAdapter(Context context, int resource, ArrayList data, Date currentDate, Date displayedDate) {
        super(context, resource);

        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;

        todaysDate = currentDate;
        this.displayedDate = displayedDate;
    }

    static class ViewHolder{
        TextView dateTextView;
        TextView occasionTextView;
        Button tapdetectorButton;
        ImageView blueDot;
    }

    @Override
    public int getCount() {
        super.getCount();
        return data.size();
    }

    public void setGridData(ArrayList mGridData) {
        this.data = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.dateTextView = (TextView) row.findViewById(R.id.dateTextView);
            holder.occasionTextView = (TextView) row.findViewById(R.id.occasionTextView);
            holder.tapdetectorButton = (Button) row.findViewById(R.id.gridDetectTapButton);
            holder.blueDot = (ImageView)row.findViewById(R.id.blue_dot_IV);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final DateObj dateObj = data.get(position);

        String dateStr = dateObj.getDateStr();
        holder.dateTextView.setText(dateStr);

        String allEventsString ;
        String occasionName = dateObj.getOccasionName();
        String userEvents = dateObj.getUserEvents();
        boolean isUserEventsPresent = false;

        boolean isAltSat = false;
        if(position==13||position==27||position==41)
        {
            if(dateObj.getActive())
                isAltSat = true;
        }

        if(userEvents!=null && userEvents.length()>0)
        {
            isUserEventsPresent = true;
            if(occasionName!=null && occasionName.length()>0)
            {
                allEventsString = occasionName + ", " + userEvents;
            }
            else {
                allEventsString = userEvents;
            }
        }
        else {
            allEventsString = occasionName;
        }

        if(isUserEventsPresent)
        {
            holder.blueDot.setVisibility(View.VISIBLE);
        }
        else {
            holder.blueDot.setVisibility(View.INVISIBLE);
        }

        if(isAltSat)
        {
            if(allEventsString==null)
            {
                allEventsString = "Alternate Saturday: Bank Holiday";
            }
            else {
                allEventsString = allEventsString + ", Alternate Saturday: Bank Holiday";
            }
        }

        holder.occasionTextView.setText(allEventsString);

        if(dateObj.isNationalHoliday)
        {
            holder.occasionTextView.setTextColor(Color.parseColor("#d65151"));
        }
        else {
            holder.occasionTextView.setTextColor(Color.parseColor("#232323"));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todaysDate);

        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        int todaysDate = calendar.get(Calendar.DATE);

        calendar.setTime(displayedDate);

        int displayedMonth = calendar.get(Calendar.MONTH);
        int displayedYear = calendar.get(Calendar.YEAR);


        if(isAltSat){
            row.setBackgroundResource(R.drawable.date_cell_alternate_sat_bg);
        }
        else if(position%7==0) //sunday
        {
            row.setBackgroundResource(R.drawable.date_cell_alternate_sat_bg);
        }
        else {
            if ((currentMonth == displayedMonth) && (currentYear == displayedYear) && dateObj.isActive) {
                if (Integer.parseInt(dateStr) == todaysDate) {
                    row.setBackgroundResource(R.drawable.todays_date_cell_bg);
                } else {
                    row.setBackgroundResource(R.drawable.date_cell_bg_selected);
                }
            } else {
                if (dateObj.isActive) {
                    row.setBackgroundResource(R.drawable.date_cell_other_months_bg);
                } else {
                    row.setBackgroundResource(R.drawable.date_cell_bg);
                }
            }
        }

        holder.tapdetectorButton.setVisibility(View.GONE);
        holder.tapdetectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, dateObj.getDateStr(), Toast.LENGTH_SHORT).show();
            }
        });

        return row;
    }
}
