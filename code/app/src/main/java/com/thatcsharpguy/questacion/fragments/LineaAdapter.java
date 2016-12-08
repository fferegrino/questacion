package com.thatcsharpguy.questacion.fragments;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thatcsharpguy.questacion.Commons;
import com.thatcsharpguy.questacion.R;
import com.thatcsharpguy.questacion.Utils;
import com.thatcsharpguy.questacion.entities.Linea;

/**
 * Created by fferegrino on 9/11/16.
 */
public class LineaAdapter extends ArrayAdapter {

    Linea[] _lineas;
    Context _ctx;

    public LineaAdapter(Context context, int resource, Linea[] objects) {
        super(context, resource, objects);
        _ctx = context;
        _lineas = objects;
    }

    @Override
    public int getCount() {
        return _lineas.length;
    }

    @Override
    public Object getItem(int position) {
        return _lineas[position];
    }

    @Override
    public long getItemId(int position) {
        return _lineas[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {

// Inflating the layout for the custom Spinner
        LayoutInflater inflater = LayoutInflater.from(_ctx);
        View layout = inflater.inflate(R.layout.linea_item, parent, false);

// Declaring and Typecasting the textview in the inflated layout
        TextView tvLanguage = (TextView) layout
                .findViewById(R.id.tv_nombre_linea);

        ImageView imgView = (ImageView) layout
                .findViewById(R.id.img_linea);

        tvLanguage.setText(_lineas[position].getName());


        int color = 0xFF000000 | _lineas[position].getColor();
        tvLanguage.setTextColor(Utils.darkenColor(color, 0.8f));

        imgView.setBackgroundColor(color);
        imgView.setImageResource(Commons.LineaIconMapper[_lineas[position].getId()]);


        return layout;
    }
}
