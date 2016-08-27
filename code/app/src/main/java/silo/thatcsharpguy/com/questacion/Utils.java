package silo.thatcsharpguy.com.questacion;

import android.graphics.Color;

/**
 * Created by anton on 8/26/2016.
 */
public final class Utils {

    public static int darkenColor(int color, float factor){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor; // value component
        return Color.HSVToColor(hsv);
    }
}
