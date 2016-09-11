package silo.thatcsharpguy.com.questacion.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.vision.text.Line;

import java.util.List;

import jsqlite.Exception;
import silo.thatcsharpguy.com.questacion.MainActivity;
import silo.thatcsharpguy.com.questacion.R;
import silo.thatcsharpguy.com.questacion.entities.Estacion;
import silo.thatcsharpguy.com.questacion.entities.Linea;

public class SetNotificationDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    Spinner _lineaSpinner;
    Spinner _estacionSpinner;

    private int _selectedLinea;
    private int _selectedEstacionId;

    static Linea[] _lineas;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(_lineas == null) {
            List<Linea> lns = null;
            try {
                lns = MainActivity.MetrobusDatabase.getLineas();
                _lineas = new Linea[lns.size()];
                lns.toArray(_lineas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.set_notification_dialog, null);
        builder.setView(layout);

        _lineaSpinner = (Spinner) layout.findViewById(R.id.linea_spinner);
        _lineaSpinner.setAdapter(new LineaAdapter(getActivity(), R.layout.linea_item,_lineas));
        _lineaSpinner.setOnItemSelectedListener(this);

        _estacionSpinner= (Spinner) layout.findViewById(R.id.estacion_spinner);

        builder.setMessage(R.string.create_calendar_message)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("SPinner", "Linea " + _selectedLinea + " estacion " + _selectedEstacionId);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }


    List<Estacion> _estaciones;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            case R.id.linea_spinner:
                try {
                    _selectedLinea = _lineas[position].getId();
                     _estaciones = MainActivity.MetrobusDatabase.getEstaciones(_selectedLinea);
                    _estacionSpinner.setAdapter(new EstacionesAdapter(getActivity(), R.layout.linea_item,_estaciones));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.estacion_spinner:
                _selectedEstacionId = _estaciones.get(position).getId();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}