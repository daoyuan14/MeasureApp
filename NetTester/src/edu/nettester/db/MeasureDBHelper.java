package edu.nettester.db;

import edu.nettester.db.MeasureContract.MeasureLog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MeasureDBHelper extends SQLiteOpenHelper {

    /**
     * we will not change the db version
     */
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MeasureDB.db";
    
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    
    private static final String SQL_CREATE_MLOG =
        "CREATE TABLE " + MeasureLog.TABLE_NAME + " (" +
        MeasureLog._ID + " INTEGER PRIMARY KEY," +
        MeasureLog.COLUMN_NAME_MID + TEXT_TYPE + COMMA_SEP +
        MeasureLog.COLUMN_NAME_TIME + TEXT_TYPE + COMMA_SEP +
        MeasureLog.COLUMN_NAME_RTT + TEXT_TYPE +
        " )";
        
    private static final String SQL_DELETE_MLOG =
        "DROP TABLE IF EXISTS " + MeasureLog.TABLE_NAME;
    
    public MeasureDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MLOG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // we have no need to upgrade db
        db.execSQL(SQL_DELETE_MLOG);
        onCreate(db);
    }
}
