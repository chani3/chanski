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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * A fragment representing a list of Items.
 * <p />
 * Activities containing this fragment MUST implement the callbacks
 * interface.
 */
public class TodoFragment extends Fragment implements AbsListView.OnItemClickListener, TextView.OnEditorActionListener {
    private static String TAG = "TodoFragment";

    private OnFragmentInteractionListener mListener;

    private AbsListView mListView;
    private ListAdapter mAdapter;
    private EditText input;

    public static TodoFragment newInstance() {
        TodoFragment fragment = new TodoFragment();
        return fragment;
    }

    public TodoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new TodoAdapter(getActivity(), getLoaderManager());
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
        addTodo(input.getText().toString());

        input.setText("");
        return true;
    }

    private void addTodo(String text) {
        //TODO caching?
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TODOS.NAME, text);
        getActivity().getContentResolver().insert(DatabaseProvider.TODOS_URI, values);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(long id);
    }

}
