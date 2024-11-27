package com.firstapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] items; // Model names
    private int[] images; // Model images

    public CustomSpinnerAdapter(Context context, String[] items, int[] images) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        this.images = images;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.spinnerItemImage);
        TextView textView = convertView.findViewById(R.id.spinnerItemText);

        // Set image and text for the spinner item
        imageView.setImageResource(images[position]);
        textView.setText(items[position]);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
