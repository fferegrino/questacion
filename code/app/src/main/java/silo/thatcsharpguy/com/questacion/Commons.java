package silo.thatcsharpguy.com.questacion;

import android.os.Environment;
import android.widget.ProgressBar;

import java.io.File;

/**
 * Created by anton on 8/26/2016.
 */
public final class Commons {

    private static final String DatabaseName = "mb.sqlite";
    private static final String QuestacionFolderName = "Questacion";
    public static final String DatabaseUri ="https://github.com/fferegrino/questacion/blob/master/sqlite/mb.sqlite?raw=true";

    public static final int[] LineaIconMapper = new int[7];

    static {
        LineaIconMapper[0] = R.drawable.ic_stat_1;
        LineaIconMapper[1] = R.drawable.ic_stat_1;
        LineaIconMapper[2] = R.drawable.ic_stat_2;
        LineaIconMapper[3] = R.drawable.ic_stat_3;
        LineaIconMapper[4] = R.drawable.ic_stat_4;
        LineaIconMapper[5] = R.drawable.ic_stat_5;
        LineaIconMapper[6] = R.drawable.ic_stat_6;
    }


    private static File _questacionFolder;
    public static File getQuestacionFolder(){
        if(_questacionFolder == null)
        {
            _questacionFolder = new File(Environment.getExternalStorageDirectory(), QuestacionFolderName);
        }
        return _questacionFolder;
    }

    private  static File _mainDatabaseFile;
    public  static File getMainDatabaseFile(){
        if(_mainDatabaseFile == null)
            _mainDatabaseFile = new File(getQuestacionFolder(),DatabaseName);
        return _mainDatabaseFile;
    }
}
