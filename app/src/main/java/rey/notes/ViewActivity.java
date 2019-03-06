package rey.notes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener
{
    EditText edit_view_title_note;
    EditText edit_view_text_note;
    EditText edit_view_datetime_note;
    ImageButton butt_view_save_note;
    ImageButton butt_view_del_note;

    private Spinner spinner_view_color_note;

    private DBClass db;

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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = new DBClass(this);

        butt_view_del_note = findViewById(R.id.butt_view_del_note);
        butt_view_del_note.setOnClickListener(this);

        butt_view_save_note = findViewById(R.id.butt_view_save_note);
        butt_view_save_note.setOnClickListener(this);

        Intent intent = getIntent();

        edit_view_title_note = findViewById(R.id.edit_view_title_note);
        edit_view_title_note.setText(intent.getStringExtra("note_bd_title"));

        edit_view_text_note = findViewById(R.id.edit_view_text_note);
        edit_view_text_note.setText(intent.getStringExtra("note_bd_text"));

        spinner_view_color_note = findViewById(R.id.spinner_view_color_note);
        spinner_view_color_note.setAdapter(new SpinnerAdapter(this));

        edit_view_datetime_note = findViewById(R.id.edit_view_datetime_note);
        edit_view_datetime_note.setText(intent.getStringExtra("note_bd_datetime"));

        spinner_view_color_note.setSelection(Integer.valueOf(intent.getStringExtra("note_bd_color")));
    }

    @Override
    public void onClick(View v)
    {
        Intent intentView = new Intent(this, MainActivity.class);
        SQLiteDatabase database = db.getWritableDatabase();
        Intent intent = getIntent();
        switch (v.getId())
        {
            case R.id.butt_view_del_note:
                database.delete(DBClass.TABLE_NOTE, DBClass.KEY_ID + " = " + intent.getStringExtra("note_bd_id"), null);
                database.close();
                startActivity(intentView);
                break;
            case R.id.butt_view_save_note:

                Date now = new Date();
                long timestamp = now.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US);
                String dateStr = sdf.format(timestamp);

                ContentValues contentValues = new ContentValues();
                contentValues.put(DBClass.KEY_TITLE, edit_view_title_note.getText().toString());
                contentValues.put(DBClass.KEY_TEXT, edit_view_text_note.getText().toString());
                contentValues.put(DBClass.KEY_COLOR, Integer.toString(spinner_view_color_note.getSelectedItemPosition()));
                contentValues.put(DBClass.KEY_DATETIME, dateStr);
                database.update(DBClass.TABLE_NOTE, contentValues, DBClass.KEY_ID + " = ?", new String[]{intent.getStringExtra("note_bd_id")});
                database.close();
                startActivity(intentView);
                break;
            default:
                database.close();
                startActivity(intentView);
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        return false;
    }

}
