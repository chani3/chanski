package ca.chani.chanski;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by chani on 2013-12-28.
 */
class DatabaseHelper extends SQLiteOpenHelper{
    private static String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 3;
    private static Uri BASE_URI = Uri.parse("content://ca.chani.chanski");

    public static final String ID = "_id";
    public static final Journal JOURNAL = new Journal();
    public static final Todo TODOS = new Todo();

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, "database.db", null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        JOURNAL.onCreate(db);
        TODOS.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("upgrade %d to %d", oldVersion, newVersion));
        assert (newVersion - oldVersion == 1);

        assert (newVersion == 3);
        db.execSQL(String.format("alter table %s add column %s text;", JOURNAL.TABLE, JOURNAL.MODE));
    }

    private static abstract class AbstractTable {
        public final String TABLE;
        public final String[] DEFAULT_COLS;
        public final Uri URI;

        protected String createSql;

        protected AbstractTable(String table, String[] default_cols) {
            TABLE = table;
            DEFAULT_COLS = default_cols;
            URI = Uri.withAppendedPath(BASE_URI, table);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(createSql);
        };
    }

    public static class Journal extends AbstractTable {
        public static final String DATE = "date";
        public static final String TEXT = "msg";
        public static final String MODE = "mode";
        Journal() {
            super("journal", new String[]{ID, DATE, TEXT});
            createSql = String.format("create table %s (%s integer primary key, %s integer not null, %s text not null, %s text);",
                    TABLE, ID, DATE, TEXT, MODE);
        }
    }

    public static class Todo extends AbstractTable {
        public static final String CREATED = "created";
        public static final String NAME = "name";
        Todo() {
            super("todolist", new String[]{ID, NAME});
            createSql = String.format("create table %s (%s integer primary key, %s integer not null, %s text not null);",
                    TABLE, ID, CREATED, NAME);
        }
    }
}
