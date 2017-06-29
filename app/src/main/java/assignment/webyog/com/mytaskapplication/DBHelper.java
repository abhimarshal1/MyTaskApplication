package assignment.webyog.com.mytaskapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ListView;

import java.sql.Blob;
import java.util.ArrayList;

/**
 * Created by Uppu on 6/25/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="TASK_WEBYOG";
    private static final int DB_VER = 1;
    public static final String DB_TABLE="tblTask";
    public static final String DB_COLUMN1 = "TaskName";
    public static final String DB_COLUMN2 = "TaskContent";
    public static final String DB_COLUMN3 = "TaskImage";
    public static final String DB_COLUMN4 = "TaskDateTime";

    public DBHelper(Context context) {

        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format("CREATE TABLE %s ( _id INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT NOT NULL,%s TEXT NOT NULL,%s BLOB,%s TEXT);",DB_TABLE,DB_COLUMN1,DB_COLUMN2,DB_COLUMN3,DB_COLUMN4);
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DELETE TABLE IF EXISTS %s",DB_TABLE);
        db.execSQL(query);
        onCreate(db);

    }

    public void insertNewTask(String taskName, String taskContent, byte[] image, String datetime){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN1,taskName);
        values.put(DB_COLUMN2,taskContent);
        values.put(DB_COLUMN3,image);
        values.put(DB_COLUMN4,datetime);
        db.insert(DB_TABLE,null,values);
        db.close();
    }


    public void UpdateTask(String taskName, String taskContent, byte[] image, String datetime){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN1,taskName);
        values.put(DB_COLUMN2,taskContent);
        values.put(DB_COLUMN3,image);
        values.put(DB_COLUMN4,datetime);
        String where = "TaskName=?";
        String[] whereArgs = new String[] {String.valueOf(taskName)};
        db.update(DB_TABLE, values, where, whereArgs);
        db.close();
    }
}
