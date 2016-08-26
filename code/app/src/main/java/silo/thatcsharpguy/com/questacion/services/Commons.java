package silo.thatcsharpguy.com.questacion.services;

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
