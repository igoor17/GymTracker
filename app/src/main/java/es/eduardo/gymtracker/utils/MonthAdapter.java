package es.eduardo.gymtracker.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.Color;

import java.util.List;

import es.eduardo.gymtracker.R;

public class MonthAdapter extends ArrayAdapter<String> {
    public MonthAdapter(Context context, List<String> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent, Color.WHITE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent, Color.BLACK);
    }

    private View initView(int position, View convertView, ViewGroup parent, int textColor) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView monthName = convertView.findViewById(android.R.id.text1);

        String currentItem = getItem(position);

        if (currentItem != null) {
            monthName.setText(currentItem);
            monthName.setTextColor(textColor);
        }

        return convertView;
    }
}