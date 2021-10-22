package com.uberapps.mytravellog;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DashboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {


    protected TextView m_CurrentYearTextView = null;
    protected TextView m_LastSixMonthTextView = null;

    private OnFragmentInteractionListener mListener;

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment DashboardFragment.
//     */
//    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.dashboard_fragment, container, false);

        m_CurrentYearTextView = (TextView) rootView.findViewById(R.id.dashboard_current_year_text);
        m_LastSixMonthTextView = (TextView) rootView.findViewById(R.id.dashboard_last_6_months_text);

        reloadData();
        return rootView;
    }

    public void reloadData() {
        TravelLogDBHelper dbHelper = new TravelLogDBHelper(getActivity());
        HashMap<String,HashMap<String,Integer>> countriesSummary = dbHelper.getLocationsSummary();
        if (m_LastSixMonthTextView != null) m_LastSixMonthTextView.setText(buildSummaryString(Objects.requireNonNull(countriesSummary.get(TravelLogDBHelper.FIRST_SIX_MONTH))));
        if (m_CurrentYearTextView != null) m_CurrentYearTextView.setText(buildSummaryString(Objects.requireNonNull(countriesSummary.get(TravelLogDBHelper.THIS_YEAR))));
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//
//        // Make sure that we are currently visible
//        if (!this.isVisible()) {
//            // If we are becoming invisible, then...
//            if (isVisibleToUser) {
//                reloadData();
//            }
//        }
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        reloadData();
    }

    public String buildSummaryString(HashMap<String, Integer> hashmapForPeriod) {
        String retVal = new String("");
        for (HashMap.Entry<String,Integer> entry : hashmapForPeriod.entrySet()) {
            retVal = retVal.concat(entry.getKey() + " - " + ((Integer)entry.getValue()).toString() + "\n");
        }
        return retVal;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mListener = (OnFragmentInteractionListener) activity;
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
    public void onResume() {
        super.onResume();
        reloadData();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
