package ca.chani.chanski;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by chani on 2013-12-28.
 * TODO turn this into a generic dbprovider
 */
public class DatabaseProvider extends ContentProvider {
    private static String TAG = "DatabaseProvider";
    public static String AUTHORITY = "ca.chani.chanski";

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int JOURNAL_CODE = 10;
    private static final int TODOS_CODE = 20;
    static {
        uriMatcher.addURI(AUTHORITY, DatabaseHelper.JOURNAL.TABLE, JOURNAL_CODE);
        uriMatcher.addURI(AUTHORITY, DatabaseHelper.TODOS.TABLE, TODOS_CODE);
    }

    private DatabaseHelper helper;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate " + this);
        helper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query " + uri);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(uri2table(uri), projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private String uri2table(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case JOURNAL_CODE:
                return DatabaseHelper.JOURNAL.TABLE;
            case TODOS_CODE:
                return DatabaseHelper.TODOS.TABLE;
            default:
                return null;
        }
    }

    private String uri2tsCol(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case JOURNAL_CODE:
                return DatabaseHelper.JOURNAL.DATE;
            case TODOS_CODE:
                return DatabaseHelper.TODOS.CREATED;
            default:
                return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "getType");
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String timestampField = uri2tsCol(uri);
        if (timestampField != null) {
            values.put(timestampField, Calendar.getInstance().getTimeInMillis());
        }

        long id = db.insert(uri2table(uri), null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        Uri ret = Uri.withAppendedPath(uri, String.valueOf(id));
        Log.d(TAG, ret.toString());
        return ret;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
