package silo.thatcsharpguy.com.questacion.fragments;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import silo.thatcsharpguy.com.questacion.R;
import silo.thatcsharpguy.com.questacion.entities.Estacion;
import silo.thatcsharpguy.com.questacion.entities.Linea;

/**
 * Created by fferegrino on 9/11/16.
 */
public class EstacionesAdapter extends ArrayAdapter {

    List<Estacion> _lineas;
    Context _ctx;

    public EstacionesAdapter(Context context, int resource, List<Estacion> objects) {
        super(context, resource, objects);
        _ctx = context;
        _lineas = objects;
    }

    @Override
    public int getCount() {
        return _lineas.size();
    }

    @Override
    public Object getItem(int position) {
        return _lineas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
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

// Setting the text using the array
        tvLanguage.setText(_lineas.get(position).getNombre());

// Setting the color of the text
        tvLanguage.setTextColor(Color.rgb(75, 180, 225));


        return layout;
    }
}