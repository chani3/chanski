package ca.chani.chanski;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by chani on 2013-12-28.
 */
class DatabaseHelper extends SQLiteOpenHelper{
    private static String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 2;

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

        assert (newVersion == 2);
        TODOS.onCreate(db);
    }

    //TODO this probably belongs elsewhere?
    public void addJournalEntry(String text) {
        ContentValues values = new ContentValues();
        values.put(JOURNAL.DATE, Calendar.getInstance().getTimeInMillis());
        values.put(JOURNAL.TEXT, text);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(JOURNAL.TABLE, null, values);

        context.getContentResolver().notifyChange(DatabaseProvider.JOURNAL_URI, null);
    }
    //TODO this probably belongs elsewhere?
    public void addTodo(String text) {
        ContentValues values = new ContentValues();
        values.put(TODOS.CREATED, Calendar.getInstance().getTimeInMillis());
        values.put(TODOS.NAME, text);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TODOS.TABLE, null, values);

        context.getContentResolver().notifyChange(DatabaseProvider.TODOS_URI, null);
    }

    private static abstract class AbstractTable {
        public final String TABLE;
        public final String[] DEFAULT_COLS;

        protected String createSql;

        protected AbstractTable(String table, String[] default_cols) {
            TABLE = table;
            DEFAULT_COLS = default_cols;
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(createSql);
        };
    }

    public static class Journal extends AbstractTable {
        public static final String DATE = "date";
        public static final String TEXT = "msg";
        Journal() {
            super("journal", new String[]{ID, DATE, TEXT});
            createSql = String.format("create table %s (%s integer primary key, %s integer not null, %s text not null);",
                    TABLE, ID, DATE, TEXT);
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
