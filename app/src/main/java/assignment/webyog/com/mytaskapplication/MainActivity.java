package assignment.webyog.com.mytaskapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import static assignment.webyog.com.mytaskapplication.DBHelper.DB_COLUMN1;
import static assignment.webyog.com.mytaskapplication.DBHelper.DB_COLUMN2;
import static assignment.webyog.com.mytaskapplication.DBHelper.DB_COLUMN3;
import static assignment.webyog.com.mytaskapplication.DBHelper.DB_COLUMN4;
import static assignment.webyog.com.mytaskapplication.DBHelper.DB_TABLE;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.list_todo);
        dbHelper = new DBHelper(this);
        load();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AddTask.class));
                MainActivity.this.finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                TextView tv=(TextView)view.findViewById(R.id.name);
                String Name= tv.getText().toString();
                Intent intent = new Intent(MainActivity.this, AddTask.class);
                intent.putExtra("ttname", Name);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Loads all the created task from the database as soon as the application Main Activity starts
    public void load(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Query for items from the database and get a cursor back
         Cursor mycursor = db.query(true, DB_TABLE, new String[] {"_id",DB_COLUMN1 ,DB_COLUMN2, DB_COLUMN3,DB_COLUMN4}, null, null, DB_COLUMN1, null, DB_COLUMN4, null);
        ;
        mycursor.moveToFirst();
        // Setup cursor adapter using cursor from last step
        CustomAdapter adapter = new CustomAdapter(this, mycursor);
        // Attach cursor adapter to the ListView
        listView.setAdapter(adapter);
        adapter.changeCursor(mycursor);
        db.close();
    }

    @Override
    public void onBackPressed() {

        finish();
    }



}
