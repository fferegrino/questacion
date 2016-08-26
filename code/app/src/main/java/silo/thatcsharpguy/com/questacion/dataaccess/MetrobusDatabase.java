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

    jsqlite.Database _db;
    public MetrobusDatabase(File dbFile) throws Exception {

        File spatialDbFile = dbFile;
         _db = new jsqlite.Database();
        _db.open(spatialDbFile.getAbsolutePath(), jsqlite.Constants.SQLITE_OPEN_READWRITE
                | jsqlite.Constants.SQLITE_OPEN_CREATE);
    }

    public List<Estacion> getEstaciones() throws Exception {
        Stmt countQuery = _db.prepare("SELECT nombre FROM Estaciones");
        int rowCount =  countQuery.column_count();

        List<Estacion> estaciones = new ArrayList<Estacion>();;
        int i = 0;
        while(countQuery.step())
        {
            estaciones.add(new Estacion(countQuery.column_string(0),0,0));
        }
        countQuery.close();
        return estaciones;
    }
}
