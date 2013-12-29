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
    private static final int DATABASE_VERSION = 1;

    public static final String ID = "_id";
    public static final String JOURNAL_TABLE = "journal";
    public static final String JOURNAL_DATE = "date";
    public static final String JOURNAL_TEXT = "msg";

    public DatabaseHelper(Context context) {
        super(context, "database.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("create table %s (%s integer primary key, %s integer not null, %s text not null);",
                JOURNAL_TABLE, ID, JOURNAL_DATE, JOURNAL_TEXT);

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "todo: upgrades");
    }

    //TODO this probably belongs elsewhere?
    public void addJournalEntry(String text) {
        ContentValues values = new ContentValues();
        values.put(JOURNAL_DATE, Calendar.getInstance().getTimeInMillis());
        values.put(JOURNAL_TEXT, text);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(JOURNAL_TABLE, null, values);
    }
}
