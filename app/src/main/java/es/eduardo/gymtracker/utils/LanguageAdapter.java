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

public class LanguageAdapter extends ArrayAdapter<LanguageItem> {
    public LanguageAdapter(Context context, List<LanguageItem> items) {
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