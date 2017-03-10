package com.thatcsharpguy.questacion.entities;

/**
 * Created by anton on 8/24/2016.
 */
public final class HardcodedData {
    public static Linea Linea1;

    static {
        Linea1 = new Linea("Linea 1");

        Linea1.getEstaciones().add(new Estacion("Colonia del Valle", 19.385506, -99.17528100000001));


    }
}
