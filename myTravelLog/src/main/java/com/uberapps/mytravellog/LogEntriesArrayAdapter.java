package com.uberapps.mytravellog;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class LogEntriesArrayAdapter extends ArrayAdapter<TravelLogEntry> {

    private static class ViewHolder {
        private TextView countryView;
        private TextView fromView;
        private TextView toView;
        private TextView totalDaysView;
    }

    LogEntriesArrayAdapter(Context context, List<TravelLogEntry> items) {
        super(context, R.layout.log_entry_list_row, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.log_entry_list_row, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.countryView = (TextView) convertView.findViewById(R.id.entry_country);
            viewHolder.fromView = (TextView) convertView.findViewById(R.id.entry_from);
            viewHolder.toView = (TextView) convertView.findViewById(R.id.entry_to);
            viewHolder.totalDaysView = (TextView) convertView.findViewById(R.id.entry_totaldays);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TravelLogEntry currentEntry = getItem(position);
        viewHolder.countryView.setText(currentEntry.getCountry());
        viewHolder.fromView.setText(SimpleDateFormat.getDateInstance().format(currentEntry.getFrom()));
        viewHolder.toView.setText(SimpleDateFormat.getDateInstance().format(currentEntry.getTo()));
        viewHolder.totalDaysView.setText(String.valueOf(currentEntry.getTotalDays()));

        return convertView;
    }
}
