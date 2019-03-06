package rey.notes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    ImageButton butt_new_note;
    ListView lv_notes;
    EditText edt_main_filter;

    private ArrayList<String> al_id;
    private ArrayList<String> al_title;
    private ArrayList<String> al_text;
    private ArrayList<String> al_color;
    private ArrayList<String> al_datetime;

    private DBClass db;

    RelativeLayout filter_laylout;


    // Запрет возврата назад
    @Override
    public void onBackPressed()
    {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.menu_about:
                AlertDialog.Builder dialog;
                String title = getString(R.string.menu_main_about_title);
                String message = getString(R.string.menu_main_about_message);
                String button1String = getString(R.string.menu_main_about_ok);

                // Создаём диалог
                dialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
                // Заголовок
                dialog.setTitle(title);
                // Сообщение
                dialog.setMessage(message);
                // Кнопка "Да"
                dialog.setPositiveButton(button1String, null);
                // Диалог может быть отменён
                dialog.setCancelable(true);
                // Показываем диалог
                dialog.show();
                return true;
            case R.id.menu_exit:
                //эмулируем нажатие на HOME, сворачивая приложение
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        filter_laylout = findViewById(R.id.filter_laylout);

        edt_main_filter = findViewById(R.id.edt_main_filter);
        // При вводе текста в строку фильтра - выводить отфильтрованное сразу
        edt_main_filter.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String filter = edt_main_filter.getText().toString();
                setArrayLV(
                        DBClass.KEY_TITLE + " LIKE ? or " + DBClass.KEY_TEXT + " LIKE ?",
                        new String[]{"%" + filter + "%", "%" + filter + "%"},
                        false
                );
                setAdapterLV();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        butt_new_note = findViewById(R.id.butt_new_note);
        butt_new_note.setOnClickListener(this);
        butt_new_note.bringToFront();

        setArrayLV(null, null, true);

        lv_notes = findViewById(R.id.lv_notes);

        // При нажатии на элемент LV открыть его
        lv_notes.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                        intent.putExtra("note_bd_id", al_id.get(position));
                        intent.putExtra("note_bd_title", al_title.get(position));
                        intent.putExtra("note_bd_text", al_text.get(position));
                        intent.putExtra("note_bd_color", al_color.get(position));
                        intent.putExtra("note_bd_datetime", al_datetime.get(position));
                        startActivity(intent);
                    }
                }
        );

        // При долгом нажатии на элемент LV вызвать диалог его удаления
        lv_notes.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener()
                {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
                    {
                        AlertDialog.Builder dialog;
                        String title = getString(R.string.delete_dialog_title);
                        String message = getString(R.string.delete_dialog_message);
                        String button1String = getString(R.string.delete_dialog_yes);
                        String button2String = getString(R.string.delete_dialog_no);

                        // Создаём диалог
                        dialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);

                        // Заголовок
                        dialog.setTitle(title);
                        // Сообщение
                        dialog.setMessage(message);
                        // Кнопка "Да"
                        dialog.setPositiveButton(button1String, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int arg1)
                            {
                                SQLiteDatabase database = db.getWritableDatabase();
                                database.delete(DBClass.TABLE_NOTE, DBClass.KEY_ID + " = " + al_id.get(position), null);
                                database.close();
                                setArrayLV(null, null, true);
                                setAdapterLV();
                            }
                        });
                        // Кнопка "Нет"
                        dialog.setNegativeButton(button2String, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int arg1)
                            {
                                Toast.makeText(MainActivity.this, R.string.delete_dialog_no_message, Toast.LENGTH_LONG).show();
                            }
                        });
                        // Диалог может быть отменён
                        dialog.setCancelable(true);
                        // Действие при отмене
                        dialog.setOnCancelListener(
                                new DialogInterface.OnCancelListener()
                                {
                                    public void onCancel(DialogInterface dialog)
                                    {
                                        Toast.makeText(MainActivity.this, R.string.delete_dialog_close_message, Toast.LENGTH_LONG).show();
                                    }
                                }
                        );
                        // Показываем диалог
                        dialog.show();
                        return true;
                    }
                }
        );

        setAdapterLV();

        SharedPreferences sPrefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        boolean tutorial = sPrefs.getBoolean("tutorial", true);

        if (tutorial)
        {
            ImageView main_background_tutorial = findViewById(R.id.main_background_tutorial);
            main_background_tutorial.setVisibility(View.VISIBLE);
            main_background_tutorial.bringToFront();
            EditText main_text_tutorial = findViewById(R.id.main_text_tutorial);
            main_text_tutorial.setVisibility(View.VISIBLE);
            main_text_tutorial.bringToFront();
        }
        else
        {
            ImageView main_background_tutorial = findViewById(R.id.main_background_tutorial);
            main_background_tutorial.setVisibility(View.INVISIBLE);
            EditText main_text_tutorial = findViewById(R.id.main_text_tutorial);
            main_text_tutorial.setVisibility(View.INVISIBLE);
        }

    }

    // Переопределяем адаптер LV
    private void setAdapterLV()
    {
        lv_notes.setAdapter(
                new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, al_title)
                {
                    int[] int_note_colors = getResources().getIntArray(R.array.int_note_colors);
                    ArrayList<Integer> colors;

                    @NonNull
                    @Override
                    public View getView(int position, View convertView, @NonNull ViewGroup parent)
                    {
                        if (convertView == null)
                        {
                            convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.lv_row_layout, parent, false);
                        }

                        if (colors == null)
                        {
                            colors = new ArrayList<>();
                            for (int re : int_note_colors)
                            {
                                colors.add(re);
                            }
                        }

                        TextView note_color = convertView.findViewById(R.id.note_color);
                        note_color.setBackgroundColor(colors.get(Integer.valueOf(al_color.get(position))));

                        TextView note_title = convertView.findViewById(R.id.note_title);
                        note_title.setText(al_title.get(position));
//                        note_title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                        TextView note_text = convertView.findViewById(R.id.note_text);
                        note_text.setText(al_text.get(position));

                        TextView note_datetime = convertView.findViewById(R.id.note_datetime);
                        note_datetime.setText(al_datetime.get(position));

                        return convertView;
                    }
                }

        );
    }

    // Заполнить массивы значениями из БД
    private void setArrayLV(String selection, String[] selectionArgs, boolean hide_filter)
    {
        // Если массивы не инициализированны - инициализировать
        if (al_id == null)
        {
            al_id = new ArrayList<>();
            al_text = new ArrayList<>();
            al_title = new ArrayList<>();
            al_color = new ArrayList<>();
            al_datetime = new ArrayList<>();
        }
        // Иначе - очистить
        else
        {
            al_id.clear();
            al_title.clear();
            al_text.clear();
            al_color.clear();
            al_datetime.clear();
        }

        db = new DBClass(this);
        SQLiteDatabase database = db.getWritableDatabase();
        Cursor cursor = database.query(DBClass.TABLE_NOTE, null, selection, selectionArgs, null, null, null);

        // Если БД не пуста
        if ((cursor.moveToFirst()) && (cursor.getCount() > 0))
        {
            if (hide_filter)
            {
                filter_laylout.setVisibility(View.VISIBLE);
            }
            // Создаём экземпляры столбцов
            int idIndex = cursor.getColumnIndex(DBClass.KEY_ID);
            int idTitle = cursor.getColumnIndex(DBClass.KEY_TITLE);
            int idText = cursor.getColumnIndex(DBClass.KEY_TEXT);
            int idColor = cursor.getColumnIndex(DBClass.KEY_COLOR);
            int idDatetime = cursor.getColumnIndex(DBClass.KEY_DATETIME);
            // Забираем все записи БД в массивы
            do
            {
                al_id.add(cursor.getString(idIndex));
                al_title.add(cursor.getString(idTitle));
                al_text.add(cursor.getString(idText));
                al_color.add(cursor.getString(idColor));
                al_datetime.add(cursor.getString(idDatetime));
            }
            while (cursor.moveToNext());
        }
        else
        {
            if (hide_filter)
            {
                filter_laylout.setVisibility(View.INVISIBLE);
            }
        }

        cursor.close();
        db.close();
    }

    // При клике переходить в активити добавления заметки
    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

}
