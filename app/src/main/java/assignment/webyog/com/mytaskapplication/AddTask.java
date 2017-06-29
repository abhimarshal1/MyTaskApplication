package assignment.webyog.com.mytaskapplication;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.support.design.widget.Snackbar;
import android.widget.TimePicker;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static assignment.webyog.com.mytaskapplication.DBHelper.DB_COLUMN1;
import static assignment.webyog.com.mytaskapplication.DBHelper.DB_COLUMN2;
import static assignment.webyog.com.mytaskapplication.DBHelper.DB_COLUMN3;
import static assignment.webyog.com.mytaskapplication.DBHelper.DB_COLUMN4;
import static assignment.webyog.com.mytaskapplication.DBHelper.DB_TABLE;

public class AddTask extends AppCompatActivity {


    private ImageButton btnCamera;
    private TextView taskName, date, time;
    private EditText taskNote;
    private Button btnsetReminder;
    private FloatingActionButton btnSave;
    final int THUMBSIZE = 64;
    byte[] byteArray;
    String pickedDate, pickedTime,pickedDateTime;
    LinearLayout imagelayout;
    Calendar dateTime = Calendar.getInstance();
    private DBHelper dbhelper;
    String getName;
    boolean editmode=false;
    private Boolean setReminderFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        setReminderFlag=true;
        dbhelper = new DBHelper(this);
        taskName = (EditText) findViewById(R.id.tName);
        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        btnCamera = (ImageButton) findViewById(R.id.CameraButton);
        taskNote = (EditText) findViewById(R.id.taskNote);
        imagelayout = (LinearLayout) findViewById(R.id.imageLayout);
        btnsetReminder = (Button) findViewById(R.id.setReminder);
        btnSave = (FloatingActionButton) findViewById(R.id.SaveButton);
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            //Intent found with extra means it is in editmode not a new task creation
            getName = (String) bd.get("ttname");
            loadTask(getName);
        }
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                insertInDB(view);

            }
        });

        btnsetReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setReminderDate();

            }
        });


    }

    private void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    private void setReminderDate() {
        DatePickerDialog mDate=new DatePickerDialog(this, d, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH));
        mDate.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        mDate.show();
    }

    private void setReminderTime() {
        TimePickerDialog mTime= new TimePickerDialog(this, t, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), true);
        mTime.show();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar dateTime = Calendar.getInstance();
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            pickedDate = formatter.format(dateTime.getTime());
            System.out.println("Date :"+pickedDate);
            setReminderTime();
        }
    };

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar dateTime = Calendar.getInstance();
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);
            pickedTime = String.valueOf(hourOfDay)+":"+String.valueOf(minute)+":"+"00";
            pickedDateTime=pickedDate+" "+pickedTime;

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(bp,
                    THUMBSIZE, THUMBSIZE);
            ImageView image = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
            params.setMargins(10, 0, 0, 0);
            image.setLayoutParams(params);
            image.setMaxHeight(THUMBSIZE);
            image.setMaxWidth(THUMBSIZE);
            image.setImageBitmap(ThumbImage);

            // Adds the view to the layout
            imagelayout.addView(image);

        }
    }

    // Storing details of task in the database
    protected void insertInDB(View view) {
        try {
        int count = imagelayout.getChildCount();
        byte imageInByte[];
        Snackbar snackbar=null;
        String tname = taskName.getText().toString();
        String tnote = taskNote.getText().toString();
        if(pickedDateTime==null){
            setReminderFlag=false;
            pickedDateTime =getMyDate();
        }

            if (!tname.equals("") && !tnote.equals("")) {
                if(count==0){
                    dbhelper.insertNewTask(tname, tnote, null, pickedDateTime);
                }
                else {
                    for (int i = 0; i < count; i++) {
                        View v = imagelayout.getChildAt(i);
                        ImageView img = (ImageView) v;
                        Bitmap bm = ((BitmapDrawable) img.getDrawable()).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        imageInByte = stream.toByteArray();
                        if (editmode == false)
                            dbhelper.insertNewTask(tname, tnote, imageInByte, pickedDateTime);
                        else
                            dbhelper.UpdateTask(tname, tnote, imageInByte, pickedDateTime);
                    }
                }
                if(setReminderFlag)
                {
                    java.util.Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(pickedDate+" "+pickedTime);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    System.out.println("Reminder time :"+calendar.getTime());
                    Notification my=getNotification(this,tname,tnote);
                    scheduleNotification(my,calendar);
                    snackbar = Snackbar.make(view, "Task Saved and Reminder set Successfully", Snackbar.LENGTH_LONG)
                            .setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    super.onDismissed(snackbar, event);


                                    startActivity(new Intent(AddTask.this, MainActivity.class));
                                    AddTask.this.finish();
                                }
                            });
                    snackbar.show();
                }
                else if(!setReminderFlag && !editmode) {
                     snackbar = Snackbar.make(view, "Task Saved Successfully But No Reminder Set", Snackbar.LENGTH_LONG)
                            .setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    super.onDismissed(snackbar, event);


                                    startActivity(new Intent(AddTask.this, MainActivity.class));
                                    AddTask.this.finish();
                                }
                            });
                    snackbar.show();
                }
                else{
                    snackbar = Snackbar.make(view, "Task Updated Successfully", Snackbar.LENGTH_LONG)
                            .setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    super.onDismissed(snackbar, event);


                                    startActivity(new Intent(AddTask.this, MainActivity.class));
                                    AddTask.this.finish();
                                }
                            });
                    snackbar.show();
                }


            } else {
                snackbar = Snackbar
                        .make(view, "Atleast Task Name and Task Note Fields can't be Empty", Snackbar.LENGTH_LONG);

                snackbar.show();
            }


        } catch (Exception e) {
            System.out.println("error :" + e.getMessage());
        }


    }

    //loading task details from database if opened for task update
    public void loadTask(String name) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        editmode=true;
        Cursor mycursor = db.query
                (
                        DB_TABLE,
                        new String[]{"_id", DB_COLUMN1, DB_COLUMN2, DB_COLUMN3, DB_COLUMN4},
                        DB_COLUMN1 + "=" + "'" + getName + "'",
                        null, null, null, null, null
                );
        mycursor.moveToFirst();
        String Name = mycursor.getString(mycursor.getColumnIndexOrThrow("TaskName"));
        String Note = mycursor.getString(mycursor.getColumnIndexOrThrow("TaskContent"));
        taskName.setText(Name);
        taskNote.setText(Note);
        //Loading all the images set for the task from the database
        if(mycursor.getBlob(mycursor.getColumnIndexOrThrow("TaskImage"))!=null) {
            do {
                byte[] bytes = mycursor.getBlob(mycursor.getColumnIndexOrThrow("TaskImage"));
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(bitmap,
                        THUMBSIZE, THUMBSIZE);
                ImageView image = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
                params.setMargins(10, 0, 0, 0);
                image.setLayoutParams(params);
                image.setMaxHeight(THUMBSIZE);
                image.setMaxWidth(THUMBSIZE);
                image.setImageBitmap(ThumbImage);
                imagelayout.addView(image);
                db.close();
            } while (mycursor.moveToNext());
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddTask.this, MainActivity.class));
        AddTask.this.finish();
    }

    //Function to fetch creation time for a task whose reminder is not set by the user
    public String getMyDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    //Schedule notification
    private void scheduleNotification(Notification notification, Calendar c) {

        MyService.notification=notification;
        Intent i = new Intent(this, MyService.class);
        //Pass Reminder time along with Intent in order to build PendingIntent for Reminder
        i.putExtra("time", c.getTimeInMillis());
        this.startService(i);
    }

    //configuring Notification
    private Notification getNotification(Context context,String title,String content) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        Intent intent = new Intent(this, AddTask.class);
        intent.putExtra("ttname", title);
        PendingIntent pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_input_name);
        builder.setLargeIcon(icon);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setAutoCancel(true);
        builder.setContentIntent(pending);
        return builder.build();
    }
}
