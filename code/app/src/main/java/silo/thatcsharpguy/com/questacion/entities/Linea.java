package silo.thatcsharpguy.com.questacion.entities;

import java.util.ArrayList;

/**
 * Created by anton on 8/24/2016.
 */
public class Linea {

    private static final int Color = 0xff990000;
    private String _name;

    private ArrayList<Estacion> _estaciones;

    public Linea(String name){
        _name = name;
        _estaciones = new ArrayList<>();
    }

    public String getName(){
        return _name;
    }

    public ArrayList<Estacion> getEstaciones(){
        return _estaciones;
    }

}
