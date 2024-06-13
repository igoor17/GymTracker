package es.eduardo.gymtracker.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;

import java.util.List;

import es.eduardo.gymtracker.R;
/**
 * Adapter class for populating a spinner or dropdown with LanguageItem objects.
 */
public class LanguageAdapter extends ArrayAdapter<LanguageItem> {

    /**
     * Constructor for LanguageAdapter.
     *
     * @param context The context where the adapter is being used.
     * @param items   The list of LanguageItem objects to be displayed.
     */
    public LanguageAdapter(Context context, List<LanguageItem> items) {
        super(context, 0, items);
    }

    /**
     * Get the view that displays the data at the specified position in the spinner/dropdown.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent view that this view will eventually be attached to.
     * @return The view corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Display in spinner with white text color
        return initView(position, convertView, parent, Color.WHITE);
    }

    /**
     * Get a dropdown view that displays the data at the specified position.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent view that this view will eventually be attached to.
     * @return The dropdown view corresponding to the data at the specified position.
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Display in dropdown with black text color
        return initView(position, convertView, parent, Color.BLACK);
    }

    /**
     * Initialize the view for the spinner/dropdown item.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent view that this view will eventually be attached to.
     * @param textColor   The text color for the language name TextView.
     * @return The initialized view for the spinner/dropdown item.
     */
    private View initView(int position, View convertView, ViewGroup parent, int textColor) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_language, parent, false);
        }

        ImageView flagImage = convertView.findViewById(R.id.flag_image);
        TextView languageName = convertView.findViewById(R.id.language_name);

        LanguageItem currentItem = getItem(position);

        if (currentItem != null) {
            flagImage.setImageResource(currentItem.getFlag());
            languageName.setText(currentItem.getLanguage());
            languageName.setTextColor(textColor);
        }

        return convertView;
    }
}