package silo.thatcsharpguy.com.questacion.dataaccess;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import  jsqlite.Database;
import jsqlite.Exception;
import jsqlite.Stmt;
import silo.thatcsharpguy.com.questacion.entities.Estacion;

/**
 * Created by anton on 8/26/2016.
 */
public class MetrobusDatabase {

    private static final int DumbMeterConstant = 111195;

    jsqlite.Database _db;
    public MetrobusDatabase(File dbFile) throws Exception {

        File spatialDbFile = dbFile;
         _db = new jsqlite.Database();
        _db.open(spatialDbFile.getAbsolutePath(), jsqlite.Constants.SQLITE_OPEN_READWRITE
                | jsqlite.Constants.SQLITE_OPEN_CREATE);
    }

    public List<Estacion> getEstaciones() throws Exception {
        Stmt query = _db.prepare("SELECT nombre FROM Estaciones");
        int rowCount =  query.column_count();

        List<Estacion> estaciones = new ArrayList<Estacion>();;
        int i = 0;
        while(query.step())
        {
            estaciones.add(new Estacion(query.column_string(0),0,0));
        }
        query.close();
        return estaciones;
    }

    public Estacion getEstacionCercana(double latitud, double longitud) throws Exception
    {
        String point = "MakePoint(" + longitud + ","+latitud+",4326)";
        Stmt query = _db.prepare("SELECT E.nombre,Distance(ubicacion," + point + ") as Distancia,L.color,L.id FROM Estaciones E INNER JOIN Lineas L ON L.id = E.linea ORDER BY Distancia");
        Estacion estacion = null;
        if(query.step()) {

            estacion = new Estacion(query.column_string(0),0,0);
            estacion.setMetros(query.column_double(1) * DumbMeterConstant);
            estacion.setColor(query.column_int(2));
            estacion.setLinea(query.column_int(3));
        }

        return estacion;
    }
}
