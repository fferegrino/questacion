package silo.thatcsharpguy.com.questacion.entities;

/**
 * Created by anton on 8/24/2016.
 */
public class Estacion {

    private String _nombre;
    private double _latitud;
    private double _longitud;

    public Estacion(String nombre, double latitud, double longitud){
        _nombre= nombre;
        _latitud = latitud;
        _longitud = longitud;
    }

    public double getLatitud() {
        return _latitud;
    }

    public void setLatitud(double latitud) {
        this._latitud = latitud;
    }

    public String getNombre() {
        return _nombre;
    }

    public void setNombre(String nombre) {
        this._nombre = nombre;
    }

    public double getLongitud() {
        return _longitud;
    }

    public void setLongitud(double longitud) {
        this._longitud = longitud;
    }
}