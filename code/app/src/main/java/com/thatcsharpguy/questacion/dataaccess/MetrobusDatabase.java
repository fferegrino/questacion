package com.thatcsharpguy.questacion.dataaccess;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import jsqlite.Database;
import jsqlite.Exception;
import jsqlite.Stmt;
import com.thatcsharpguy.questacion.entities.Estacion;
import com.thatcsharpguy.questacion.entities.Linea;

/**
 * Created by anton on 8/26/2016.
 */
public class MetrobusDatabase {

    private static final int DumbMeterConstant = 111195;
    private static final Double MaxDistance = Double.MAX_VALUE;

    jsqlite.Database _db;

    public MetrobusDatabase(File dbFile) throws Exception {

        File spatialDbFile = dbFile;
        _db = new jsqlite.Database();
        _db.open(spatialDbFile.getAbsolutePath(), jsqlite.Constants.SQLITE_OPEN_READWRITE
                | jsqlite.Constants.SQLITE_OPEN_CREATE);
    }

    public List<Estacion> getEstaciones() throws Exception {
        Stmt query = _db.prepare("SELECT nombre FROM Estaciones");
        int rowCount = query.column_count();

        List<Estacion> estaciones = new ArrayList<Estacion>();
        ;
        int i = 0;
        while (query.step()) {
            estaciones.add(new Estacion(query.column_string(0), 0, 0));
        }
        query.close();
        return estaciones;
    }


    public List<Estacion> getEstaciones(int linea) throws Exception {
        Stmt query = _db.prepare("SELECT E.id,E.nombre,L.color,L.id FROM Estaciones E INNER JOIN Lineas L ON L.id = E.linea WHERE E.linea = " + linea);
        int rowCount = query.column_count();

        List<Estacion> estaciones = new ArrayList<Estacion>();
        ;
        int i = 0;
        while (query.step()) {
            Estacion estacion = new Estacion(query.column_string(1), 0, 0);
            estacion.setId(query.column_int(0));
            estacion.setColor(query.column_int(2));
            estacion.setLinea(query.column_int(3));
            estaciones.add(estacion);
        }
        query.close();
        return estaciones;
    }

    public List<Linea> getLineas() throws Exception {
        Stmt query = _db.prepare("SELECT id,nombre,color FROM Lineas");
        int rowCount = query.column_count();

        List<Linea> lineas = new ArrayList<Linea>();
        ;
        int i = 0;
        while (query.step()) {
            Linea l = new Linea(query.column_string(1));
            l.setId(query.column_int(0));
            l.setColor(query.column_int(2));
            lineas.add(l);
        }
        query.close();
        return lineas;
    }

    private static final String defaultIcon = "<path fill=\"#FFF\" d=\"M9.8 54.4c-.3 0-2.2-1-2.5-1.5l-1-2c-.3-1-.3-27 0-28 0-.8.3-1.7.5-2 .2-.8 2.5-3 3-3.5.5-.6 1.6-1 2.4-1H60l1.6.5 2.8 2.7 1 2c.2 1 .4 7.5.4 7.5l-6.4 6 6.4 5.8s0 4-.3 5c0 .8-.4 2-.7 2.6-.2.7-2.5 3.7-3 4-.7.5-1.6 1-2 1-1 .4-24.5.4-25.4.4-.8 0-1-1-1-1V24s-5.6 0-6 .3l-.7.8c-.2.7-.3 29-.3 29H25c-1 0-3.7-1-4-1.3-.5-.7-1-1.4-1-2-.4-.6 0-27 0-27s-6 .3-6.6.6l-.6 1-.2 29s-2.6 0-3-.3zM57 47c1 0 1-2 1-2l.2-5.2h-12v-8.2h12s0-5.3-.2-6l-1-1c-.5-.2-16.3 0-16.3 0V47H57z\"/>";

    public Estacion getEstacionCercana(double latitud, double longitud, double threshold) throws Exception {
        String point = "MakePoint(" + longitud + "," + latitud + ",4326)";
        String q = "SELECT E.nombre, " + // 0
                "Distance(ubicacion," + point + ") as Distancia, " + // 1
                "L.color, " + // 2
                "L.id, " + // 3
                "E.svg " + // 4
                "FROM Estaciones E INNER JOIN Lineas L ON L.id = E.linea ORDER BY Distancia";
        Stmt query = _db.prepare(q);
        Estacion estacion = null;
        if (query.step()) {

            estacion = new Estacion(query.column_string(0), 0, 0);
            estacion.setMetros(query.column_double(1) * DumbMeterConstant);
            estacion.setMetros(query.column_double(1) * DumbMeterConstant);
            estacion.setColor(query.column_int(2));
            estacion.setLinea(query.column_int(3));
            String svg = query.column_string(4);
            estacion.setIcon(svg != null ? svg : defaultIcon);
        }

        if (threshold > 0 && estacion.getMetros() > threshold) {
            return null;
        }

        return estacion;
    }
}
