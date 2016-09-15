package silo.thatcsharpguy.com.questacion.entities;

/**
 * Created by anton on 8/24/2016.
 */
public class Estacion {

    private String _nombre;
    private String _icon;
    private double _latitud;
    private double _longitud;
    private int _id;
    private int _linea;

    private double _metros;
    private int _color;

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

    public double getMetros() {
        return _metros;
    }

    public void setMetros(double metros) { this._metros = metros; }

    public int getColor() {
        return _color;
    }

    public void setColor(int color) { this._color = color; }

    public int getLinea() {
        return _linea;
    }

    public void setIcon(String icon) { this._icon = icon; }

    public String getIcon() {
        return _icon;
    }

    public void setLinea(int linea) { this._linea = linea; }

    public int getId() { return _id; }

    public void setId(int id) { this._id = id; }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Estacion))return false;
        Estacion e = (Estacion)other;

        return e.getNombre().equals(this.getNombre()) &&
                e.getLinea() == this.getLinea();
    }
}
