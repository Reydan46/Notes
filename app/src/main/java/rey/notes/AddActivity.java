package rey.notes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText edit_add_title_note;
    EditText edit_add_text_note;
    ImageButton butt_add_save_note;

    private Spinner spinner_add_color_note;

    private DBClass db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = new DBClass(this);

        edit_add_title_note = findViewById(R.id.edit_add_title_note);
        edit_add_text_note = findViewById(R.id.edit_add_text_note);

        butt_add_save_note = findViewById(R.id.butt_add_save_note);
        butt_add_save_note.setOnClickListener(this);

        spinner_add_color_note = findViewById(R.id.spinner_add_color_note);
        spinner_add_color_note.setAdapter(new SpinnerAdapter(this));

        SharedPreferences sPrefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        boolean tutorial = sPrefs.getBoolean("tutorial", true);

        if (tutorial)
        {
            ImageView add_background_tutorial = findViewById(R.id.add_background_tutorial);
            add_background_tutorial.setVisibility(View.VISIBLE);
            EditText add_text_tutorial = findViewById(R.id.add_text_tutorial);
            add_text_tutorial.setVisibility(View.VISIBLE);
        }
        else
        {
            ImageView add_background_tutorial = findViewById(R.id.add_background_tutorial);
            add_background_tutorial.setVisibility(View.INVISIBLE);
            EditText add_text_tutorial = findViewById(R.id.add_text_tutorial);
            add_text_tutorial.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {

        String title = edit_add_title_note.getText().toString();
        String text = edit_add_text_note.getText().toString();
        String pos_spinner = Integer.toString(spinner_add_color_note.getSelectedItemPosition());


        Date now = new Date();
        long timestamp = now.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String dateStr = sdf.format(timestamp);

        if (title.equals(""))
        {
            title = dateStr;
        }

        SQLiteDatabase database = db.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBClass.KEY_TITLE, title);
        contentValues.put(DBClass.KEY_TEXT, text);
        contentValues.put(DBClass.KEY_COLOR, pos_spinner);
        contentValues.put(DBClass.KEY_DATETIME, dateStr);

        database.insert(DBClass.TABLE_NOTE, null, contentValues);

        db.close();


        SharedPreferences sPrefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        boolean tutorial = sPrefs.getBoolean("tutorial", true);

        if (tutorial)
        {
            SharedPreferences.Editor editor = sPrefs.edit();
            editor.putBoolean("tutorial", false);
            editor.apply();
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
