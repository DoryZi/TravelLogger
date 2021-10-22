package com.uberapps.mytravellog;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.countrypicker.CountryPicker;
import com.countrypicker.CountryPickerListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EditEntryFragment extends DialogFragment implements OnClickListener {
	  /**
     * A placeholder fragment containing a simple view.
     */
	
	TextView m_NewCountry = null;
	TextView m_NewDateFrom = null;
	TextView m_NewDateTo = null;
	Button m_OkButton = null;
	Button m_DeleteButton = null;
    Button m_CancelButton = null;
	
	CountryPicker m_CountryPickerDialog = null;
	DatePickerDialog m_DatePicker = null;
	Date m_NewDateFromObj = null;
	Date m_NewDateToObj = null;
	
	TravelLogEntry m_EntryToEdit = null;
	Boolean m_IsNew;
	EntryUpdatedListener m_EntryUpdateListener = null;
	public final static int NEW_ENTRY = -1000;
	public final static String EDIT_ENTRY_ID = "travelLogEntryId";
    private final String DATE_FORMAT = "dd MMM yyyy";
	TravelLogDBHelper m_dbHelper = null;
    public EditEntryFragment() {
    }
    
 // Container Activity must implement this interface
    public interface EntryUpdatedListener {
        public void onEntryUpdated(TravelLogEntry entryUpdated, Boolean isNew);
        
        public void onEntryDelete(TravelLogEntry entryToDelete);
    }
    
    public void setEntryUpdateListener(EntryUpdatedListener listener) {
    	m_EntryUpdateListener = listener;
    }
    
    public static EditEntryFragment getInstance(int entryIdToEdit) {
    	  EditEntryFragment f = new EditEntryFragment();
          Bundle args = new Bundle();
          args.putInt(EditEntryFragment.EDIT_ENTRY_ID, entryIdToEdit);
          f.setArguments(args);
          return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_entry_fragment, container, false);


        
        m_NewCountry = (TextView) rootView.findViewById(R.id.new_entry_country);
        m_NewDateFrom = (TextView) rootView.findViewById(R.id.new_entry_from);
        m_NewDateTo = (TextView) rootView.findViewById(R.id.new_entry_to);
        m_OkButton = (Button) rootView.findViewById(R.id.edit_ok_button);
        m_CancelButton = (Button) rootView.findViewById(R.id.edit_cancel_button);
        m_dbHelper = new TravelLogDBHelper(getActivity());
        m_DeleteButton = (Button) rootView.findViewById(R.id.edit_delete_button);	
        
        Bundle bundle = getArguments();
        int editEntryId = bundle.getInt(EditEntryFragment.EDIT_ENTRY_ID);
        m_IsNew = editEntryId == EditEntryFragment.NEW_ENTRY;
        if (m_IsNew) {
            TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            String currentCountryISO = tm.getNetworkCountryIso();
            getDialog().setTitle("Create New Log Entry");
        	m_EntryToEdit = new TravelLogEntry(
                    CountryPicker.getCountryFromCode(currentCountryISO, getActivity()),
        			new Date(), 
        			new Date());
        	m_DeleteButton.setVisibility(View.GONE);
        } else {
            getDialog().setTitle("Edit Log Entry Info");
            m_DeleteButton.setVisibility(View.VISIBLE);
            m_EntryToEdit = m_dbHelper.findEntry(editEntryId);
        }

        m_NewCountry.setText(m_EntryToEdit.getCountry());
        m_NewDateFromObj = m_EntryToEdit.getFrom();
        m_NewDateToObj = m_EntryToEdit.getTo();
        m_NewDateFrom.setText(SimpleDateFormat.getDateInstance().format(m_EntryToEdit.getFrom()));
        m_NewDateTo.setText(SimpleDateFormat.getDateInstance().format(m_EntryToEdit.getTo()));

        
         
        m_NewDateFrom.setOnClickListener(this);
        m_NewCountry.setOnClickListener(this);
        m_NewDateTo.setOnClickListener(this);
        m_OkButton.setOnClickListener(this);
        m_DeleteButton.setOnClickListener(this);
        m_CancelButton.setOnClickListener(this);
        
		m_CountryPickerDialog = new CountryPicker();
		m_CountryPickerDialog.setListener(new CountryPickerListener() {
			@Override
			public void onSelectCountry(String name, String code) {
				// TODO Auto-generated method stub
				m_NewCountry.setText(name);
				m_CountryPickerDialog.dismiss();
			}
		});
        return rootView;
    }
    
  
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_entry_country:
			showEditCountryFragment();
			break;
		case R.id.new_entry_from:
		case R.id.new_entry_to:
			showChangeDateFragment(v.getId());
			break;
		case R.id.edit_ok_button:
            if (validateInput()) updateEntry();
			break;
		case R.id.edit_delete_button:
			deleteEntry();
			break;
        case R.id.edit_cancel_button:
            dismiss();
            break;
		}
		
	}

    boolean validateInput() {
        boolean valid = true;
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        if (m_NewDateTo.getText().toString().isEmpty()) {
            m_NewDateTo.setError("Must provide a to date");
            valid = false;
            m_NewDateTo.startAnimation(shake);
        }

        if (m_NewDateFrom.getText().toString().isEmpty()) {
            m_NewDateFrom.setError("Must provide a from date");
            m_NewDateFrom.startAnimation(shake);
            valid = false;
        }



        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(m_NewDateToObj);
        setCalendarToStartOfDate(toCalendar);
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(m_NewDateFromObj);
        setCalendarToStartOfDate(fromCalendar);

        if (valid && (toCalendar.before(fromCalendar))) {
            m_NewDateFrom.setError("From date must be before to date");
            m_NewDateTo.setError("To date must be after from date");
            m_NewDateFrom.startAnimation(shake);
            m_NewDateTo.startAnimation(shake);
            valid = false;
        }


        if (m_NewCountry.getText().toString().isEmpty()) {
            m_NewCountry.setError("Must provide a country");
            m_NewCountry.startAnimation(shake);
            valid = false;
        }

        return valid;
    }


    void setCalendarToStartOfDate(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }


    void showEditCountryFragment(){
		m_CountryPickerDialog.show(getActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
	}
	void showChangeDateFragment(int idToShowFor) {
		String curDate = null;
		OnDateSetListener dateChangeListener = null;

		switch (idToShowFor) {
		case R.id.new_entry_from:
			curDate = m_NewDateFrom.getText().toString();
			dateChangeListener = new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					m_NewDateFromObj = updateTimeView(m_NewDateFrom,year,monthOfYear,dayOfMonth);
					m_DatePicker.dismiss();
					
				}
			};
			break;
		case R.id.new_entry_to:
			curDate = m_NewDateFrom.getText().toString();
			dateChangeListener = new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					m_NewDateToObj = updateTimeView(m_NewDateTo,year,monthOfYear,dayOfMonth);
					m_DatePicker.dismiss();
				}
			};
		}
		
		Calendar cal = Calendar.getInstance();
		  
		try {
			 cal.setTime(!curDate.isEmpty() ? 
					 	SimpleDateFormat.getDateInstance().parse(curDate) :
					 	new Date());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			cal.setTime(new Date());
		}
		m_DatePicker = new DatePickerDialog(getActivity(),
				dateChangeListener,
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH));
				cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		m_DatePicker.show();
		
		
	}
	void updateEntry() {
		if (m_NewCountry.getText().toString().isEmpty() ||
			m_NewDateFrom.getText().toString().isEmpty() ||
			m_NewDateTo.getText().toString().isEmpty()) return;
			
		m_EntryToEdit.setFrom(m_NewDateFromObj);
		m_EntryToEdit.setTo(m_NewDateToObj); 
		m_EntryToEdit.setCountry(m_NewCountry.getText().toString());
		if (m_EntryUpdateListener != null) m_EntryUpdateListener.onEntryUpdated(m_EntryToEdit, m_IsNew);
		dismiss();
	}
	
	void deleteEntry() {
		if (m_EntryUpdateListener != null) m_EntryUpdateListener.onEntryDelete(m_EntryToEdit);
		dismiss();
	}
	
	Date updateTimeView(TextView viewToUpdate, int year, int monthOfYear, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		cal.set(Calendar.MONTH, monthOfYear);
		cal.set(Calendar.YEAR, year);
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date dateObject = cal.getTime();
		viewToUpdate.setText(SimpleDateFormat.getDateInstance().format(dateObject));
		return dateObject;
	}
}
