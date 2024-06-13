package es.eduardo.gymtracker.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.Color;

import java.util.List;

/**
 * ArrayAdapter implementation for displaying months in a Spinner or similar AdapterView.
 */
public class MonthAdapter extends ArrayAdapter<String> {

    /**
     * Constructs a new MonthAdapter with the specified context and list of items (months).
     *
     * @param context The current context.
     * @param items   The list of months to be displayed.
     */
    public MonthAdapter(Context context, List<String> items) {
        super(context, 0, items);
    }

    /**
     * Returns the view for the month item at the specified position in a Spinner.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return The view corresponding to the month item at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent, Color.WHITE);
    }

    /**
     * Returns the drop down view for the month item at the specified position in a Spinner.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return The drop down view corresponding to the month item at the specified position.
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent, Color.BLACK);
    }

    /**
     * Initializes and returns the view for the month item at the specified position.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @param textColor   The color of the text to be set in the TextView.
     * @return The initialized view for the month item at the specified position.
     */
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
