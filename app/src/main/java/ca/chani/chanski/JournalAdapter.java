package ca.chani.chanski;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

/**
 * Created by chani on 2013-12-28.
 */
public class JournalAdapter extends SimpleCursorAdapter  implements LoaderManager.LoaderCallbacks<Cursor> {
    private static String TAG = "JournalAdapter";
    private Context context;

    static final String[] PROJECTION = new String[] {DatabaseHelper.ID, DatabaseHelper.JOURNAL_DATE, DatabaseHelper.JOURNAL_TEXT};
    static final String[] FROM_COLS = new String[] {DatabaseHelper.JOURNAL_TEXT};
    static final int[] TO_VIEWS = new int[] {android.R.id.text1};

    public JournalAdapter(Context context, LoaderManager loaderManager) {
        super(context, android.R.layout.simple_list_item_1, null, FROM_COLS, TO_VIEWS, 0);
        this.context = context;

        loaderManager.initLoader(0, null, this);
        Log.d(TAG, "adapter up");
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse("content://" + JournalProvider.URI);
        CursorLoader loader = new CursorLoader(context, uri, PROJECTION, null, null, null);
        Log.d(TAG, String.format("made loader: %s", loader));
        return loader;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swapCursor(data);
    }
    public void onLoaderReset(Loader<Cursor> loader) {
        swapCursor(null);
    }
}
