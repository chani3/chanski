package ca.chani.chanski;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by chani on 2013-12-28.
 * TODO turn this into a generic dbprovider
 */
public class DatabaseProvider extends ContentProvider {
    private static String TAG = "DatabaseProvider";

    private static String AUTHORITY = "ca.chani.chanski";
    private static Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static Uri JOURNAL_URI = Uri.withAppendedPath(BASE_URI, DatabaseHelper.JOURNAL.TABLE);
    public static Uri TODOS_URI = Uri.withAppendedPath(BASE_URI, DatabaseHelper.TODOS.TABLE);

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int JOURNAL_CODE = 10;
    private static final int TODOS_CODE = 20;
    static {
        uriMatcher.addURI(AUTHORITY, DatabaseHelper.JOURNAL.TABLE, JOURNAL_CODE);
        uriMatcher.addURI(AUTHORITY, DatabaseHelper.TODOS.TABLE, TODOS_CODE);
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate " + this);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query " + uri);
        DatabaseHelper helper = new DatabaseHelper(getContext());
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

    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "getType");
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
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
