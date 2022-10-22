package com.uberapps.mytravellog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uberapps.mytravellog.EditEntryFragment.EntryUpdatedListener;

import java.util.List;
import java.util.Objects;

public class LogEntriesFragment extends ListFragment implements OnClickListener, EntryUpdatedListener {
    /**
     * A placeholder fragment containing a simple view.
     */

    // TODO: Rename and change types and number of parameters
    public static LogEntriesFragment newInstance() {
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return new LogEntriesFragment();
    }

    private BroadcastReceiver mBroadcastReceiver;

    public static final String DB_UPDATED_NOTIFICAITON = "entries have been updated by the service";

    FloatingActionButton m_CreateNewLogEntry = null;
    // CountryPicker m_CountryPickerDialog = null;
    // DatePickerDialog m_DatePicker = null;
    ListView m_EntriesList = null;

    private final String DATE_FORMAT = "dd MMM yyyy";
    TravelLogDBHelper m_dbHelper = null;

    public LogEntriesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.entries_list_fragment, container, false);

        m_CreateNewLogEntry = (FloatingActionButton) rootView.findViewById(R.id.create_new_entry_button);
        m_dbHelper = new TravelLogDBHelper(getActivity());
        m_EntriesList = (ListView) rootView.findViewById(android.R.id.list);

        List<TravelLogEntry> allEntries = m_dbHelper.getAllEntries();
        registerUpdateReceiver();
        m_CreateNewLogEntry.setOnClickListener(this);

        LogEntriesArrayAdapter adapter = new LogEntriesArrayAdapter(getActivity(), allEntries);
        setListAdapter(adapter);


        return rootView;
    }


    protected void registerUpdateReceiver() {
        if (mBroadcastReceiver != null) return;
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(DB_UPDATED_NOTIFICAITON)) reloadAllEntries();
            }
        };
        Objects.requireNonNull(getActivity()).registerReceiver(mBroadcastReceiver, new IntentFilter(LogEntriesFragment.DB_UPDATED_NOTIFICAITON));
    }

    protected void unregisterUpdateReceiver() {
        Objects.requireNonNull(getActivity()).unregisterReceiver(mBroadcastReceiver);
        mBroadcastReceiver = null;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.create_new_entry_button) {
            createNewEntry();
        }
    }

    @Override
    public void onListItemClick(ListView l, @NonNull View v, int position, long id) {
        editEntry(((TravelLogEntry) l.getItemAtPosition(position)));
    }

    private void editEntry(TravelLogEntry entryToEdit) {
        EditEntryFragment editEntry = EditEntryFragment.getInstance(entryToEdit.getID());
        editEntry.setEntryUpdateListener(this);
        editEntry.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "EDIT_ENTRY_FRAGMENT");
    }


    private void createNewEntry() {
        EditEntryFragment editEntry = EditEntryFragment.getInstance(EditEntryFragment.NEW_ENTRY);
        editEntry.setEntryUpdateListener(this);
        editEntry.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "EDIT_ENTRY_FRAGMENT");
    }

    @Override
    public void onEntryUpdated(TravelLogEntry entryUpdated, Boolean isNew) {
        if (isNew) {
            m_dbHelper.addLogEntry(entryUpdated);
        } else {
            m_dbHelper.updateLogEntry(entryUpdated);
        }
        reloadAllEntries();
    }

    @Override
    public void onEntryDelete(TravelLogEntry entryToDelete) {
        m_dbHelper.deleteEntry(entryToDelete);
        reloadAllEntries();
    }

    public void reloadAllEntries() {
        LogEntriesArrayAdapter adapter = (LogEntriesArrayAdapter) this.getListAdapter();
        List<TravelLogEntry> allEntries = m_dbHelper.getAllEntries();
        Objects.requireNonNull(adapter).clear();
        adapter.addAll(allEntries);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterUpdateReceiver();
    }

    @Override
    public void onResume() {
        reloadAllEntries();
        registerUpdateReceiver();
        super.onResume();
    }

}
