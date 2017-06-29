package assignment.webyog.com.mytaskapplication;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Uppu on 6/26/2017.
 */

public class CustomAdapter extends CursorAdapter {



    String dateStatus,timeStatus;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    String datepart,timepart;

    public CustomAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.todo_list_contents, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvtaskName = (TextView) view.findViewById(R.id.name);
        TextView tvtaskTime = (TextView) view.findViewById(R.id.textClock);
        String Name = cursor.getString(cursor.getColumnIndexOrThrow("TaskName"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("TaskDateTime"));

        // Extract properties from cursor
        try {

            Date ddate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ddate);
            Calendar today = Calendar.getInstance();
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE,-1);
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DATE,1);
            String[] d=date.split(" ");
            datepart=d[0];
            timepart=d[1];

            //Comparisons to declare a date as yesterday/tomorrow or other
            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                dateStatus="";
            } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                dateStatus="yesterday";
            }
            else if (calendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR)) {
                dateStatus="tomorrow";
            }
            else{
                String[] d2=datepart.split("-");
                int mon=Integer.parseInt(d2[1])-1;
                DateFormatSymbols dfs = new DateFormatSymbols();
                String[] months = dfs.getMonths();
                dateStatus=d2[2]+" "+months[mon];
            }
            String[] t=timepart.split(":");
            int hour = Integer.parseInt(t[0]);
            int min = Integer.parseInt(t[1]);
            if(hour>12)
                if(min<10)
                    timeStatus=String.valueOf(hour%12)+":0"+String.valueOf(min)+" pm";
                else
                    timeStatus=String.valueOf(hour%12)+":"+String.valueOf(min)+" pm";
            else {
                if (min < 10)
                    timeStatus = hour + ":0" + min + " am";
                else
                    timeStatus = hour + ":" + min + " am";
            }


            // Populate fields with extracted properties*/
            tvtaskName.setText(Name);
            if(dateStatus.equals(""))
                tvtaskTime.setText(timeStatus);
            else
               tvtaskTime.setText(dateStatus+" , "+timeStatus);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
