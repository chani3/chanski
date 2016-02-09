package ca.chani.chanski;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by chani on 2013-12-28.
 */
public class JournalAdapter extends SimpleCursorAdapter  implements LoaderManager.LoaderCallbacks<Cursor> {
    private static String TAG = "JournalAdapter";
    private Context context;

    static final String[] PROJECTION = DatabaseHelper.JOURNAL.DEFAULT_COLS;
    static final String[] FROM_COLS = new String[] {DatabaseHelper.JOURNAL.TEXT, DatabaseHelper.JOURNAL.DATE};
    static final int[] TO_VIEWS = new int[] {android.R.id.text1, android.R.id.text2};

    public JournalAdapter(Context context, LoaderManager loaderManager) {
        super(context, android.R.layout.simple_list_item_activated_2, null, FROM_COLS, TO_VIEWS, 0);
        this.context = context;
        setViewBinder(new JournalViewBinder()); //later I'll probably have to do all of bindView, but let's try this for now

        loaderManager.initLoader(0, null, this);
        Log.d(TAG, "adapter up");
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(context, DatabaseHelper.JOURNAL.URI, PROJECTION, null, null, null);
        Log.d(TAG, String.format("made loader: %s", loader));
        return loader;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swapCursor(data);
    }
    public void onLoaderReset(Loader<Cursor> loader) {
        swapCursor(null);
    }

    private class JournalViewBinder implements ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (cursor.getColumnName(columnIndex).equals(DatabaseHelper.JOURNAL.DATE)) {
                //yaay, a date column
                DateFormat formatter = DateFormat.getDateTimeInstance();
                long rawDate = cursor.getLong(columnIndex); //came from Calendar.getTimeInMillis
                Date date = new Date(rawDate);
                String formattedDate = formatter.format(date);
                //Log.d(TAG, "date! " + formattedDate);
                ((TextView)view).setText(formattedDate);
                return true;
            }

            return false;
        }
    }
}
