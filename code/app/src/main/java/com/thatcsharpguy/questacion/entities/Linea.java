package com.thatcsharpguy.questacion.entities;

import java.util.ArrayList;

/**
 * Created by anton on 8/24/2016.
 */
public class Linea {

    private String _name;
    private int _id;
    private int _color;

    private ArrayList<Estacion> _estaciones;

    public Linea(String name) {
        _name = name;
        _estaciones = new ArrayList<>();
    }

    public String getName() {
        return _name;
    }

    public ArrayList<Estacion> getEstaciones() {
        return _estaciones;
    }

    public int getColor() {
        return _color;
    }

    public void setColor(int color) {
        this._color = color;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }
}
