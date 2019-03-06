package rey.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// База данных
public class DBClass extends SQLiteOpenHelper
{
    // Название БД
    private static final String DATABASE_NAME = "BD_NOTES";
    // Версия БД
    private static final int DATABASE_VERSION = 1;

    // Название таблицы
    static final String TABLE_NOTE = "NOTE";
    // Поле 1
    static final String KEY_ID = "ID";
    // Поле 2
    static final String KEY_TITLE = "TITLE";
    // Поле 3
    static final String KEY_TEXT = "TEXT";
    // Поле 4
    static final String KEY_COLOR = "COLOR";
    // Поле 5
    static final String KEY_DATETIME = "DATETIME";

    DBClass(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // При создании БД выполнить запрос (создать таблицу)
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(
                "create table " + TABLE_NOTE +
                        "("
                        + KEY_ID + " integer primary key, "
                        + KEY_TITLE + " text,"
                        + KEY_TEXT + " text,"
                        + KEY_COLOR + " text,"
                        + KEY_DATETIME + " text"
                        + ")"
        );
    }

    // Удалять существущую таблицу при создании новой
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        database.execSQL("drop table if exists " + TABLE_NOTE);

        onCreate(database);
    }
}
