package ca.chani.chanski;

import android.app.Activity;
import android.app.Fragment;
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
        Log.e(TAG, String.format("editor action: %d %s", actionId, input.getText()));
        saveJournalEntry(input.getText().toString());

        input.setText("");
        return true;
    }

    private void saveJournalEntry(String text) {
        //TODO caching?
        DatabaseHelper helper = new DatabaseHelper(getActivity());
        helper.addJournalEntry(text);
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(long id);
    }

}
