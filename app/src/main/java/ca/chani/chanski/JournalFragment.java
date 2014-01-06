package ca.chani.chanski;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import ca.chani.chanski.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p />
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
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(id);
        }
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
