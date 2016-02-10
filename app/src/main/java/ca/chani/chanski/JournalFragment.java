package ca.chani.chanski;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Journal UI Fragment, both display and input.
 *
 * Activities containing this fragment MUST implement the callbacks
 * interface.
 */
public class JournalFragment extends Fragment implements AbsListView.OnItemClickListener, TextView.OnEditorActionListener {
    private static String TAG = "JournalFragment";

    private OnFragmentInteractionListener mListener;

    private AbsListView mListView;
    private ListAdapter mAdapter;
    private EditText input;
    private Spinner modeSpinner;
    private String[] modeValues;

    public static JournalFragment newInstance() {
        JournalFragment fragment = new JournalFragment();
        return fragment;
    }

    public JournalFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/

        mAdapter = new JournalAdapter(getActivity(), getLoaderManager());

        modeValues = getResources().getStringArray(R.array.journal_modes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal, container, false);

        // Set up connections
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new ActionModeHandler());
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));

        input = (EditText) view.findViewById(R.id.editText);
        input.setOnEditorActionListener(this);

        modeSpinner = (Spinner) view.findViewById(R.id.spinner);
        //TODO: prettier spinner
        //TODO: change text bg on spinner change
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.journal_modes_display, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, String.format("onItemClick %d", position));
        mListView.setItemChecked(position, true); //starts multichoice mode
    }

    /**
     * Callback handler for action mode, where items are selected and acted on.
     */
    private class ActionModeHandler implements AbsListView.MultiChoiceModeListener {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Log.d(TAG, "onCreateActionMode");
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.journal_item, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_share:
                    shareItems(mListView.getCheckedItemPositions());
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.d(TAG, "onDestroyActionMode");
        }

        /// anything that depends on what's checked gets updated here.
        @Override
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
        }
    }

    private void shareItems(SparseBooleanArray positions) {
        Log.d(TAG, positions.toString());
        String text = "";
        for (int i=0; i<positions.size(); i++) {
            //TODO can we reuse one cursor?
            if (positions.valueAt(i)) {
                Cursor cursor = (Cursor) mAdapter.getItem(positions.keyAt(i));

                //ugh date formatting; should we stick a helper somewhere?
                DateFormat formatter = DateFormat.getTimeInstance();
                long rawDate = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.JOURNAL.DATE));
                Date date = new Date(rawDate);
                String formattedDate = formatter.format(date);
                text += String.format("[%s] ", formattedDate);

                text += cursor.getString(cursor.getColumnIndex(DatabaseHelper.JOURNAL.TEXT)) + "\n";
            }
        }
        Log.d(TAG, String.format("sharing text %s", text));

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, text));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        saveJournalEntry(input.getText().toString(), (int)modeSpinner.getSelectedItemId());

        input.setText("");
        return true;
    }

    private void saveJournalEntry(String text, int mode) {
        //TODO caching?
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.JOURNAL.TEXT, text);
        values.put(DatabaseHelper.JOURNAL.MODE, modeValues[mode]);
        getActivity().getContentResolver().insert(DatabaseHelper.JOURNAL.URI, values);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(long id);
    }

}
