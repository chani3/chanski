package ca.chani.chanski;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by chani on 2013-12-28.
 * TODO turn this into a generic dbprovider
 */
public class JournalProvider extends ContentProvider {
    private static String TAG = "JournalProvider";

    public static String URI = "ca.chani.chanski";
    public static Uri CONTENT_URI = Uri.parse("content://" + URI);
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
        Cursor cursor = db.query(DatabaseHelper.JOURNAL_TABLE, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
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
