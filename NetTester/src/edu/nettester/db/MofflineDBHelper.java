package edu.nettester.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.nettester.db.MeasureContract.OfflineDel;
import edu.nettester.util.Constant;

public class MofflineDBHelper extends SQLiteOpenHelper implements Constant  {
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MofflineDB.db";
    
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    
    private static final String SQL_CREATE_MLOG =
            "CREATE TABLE " + OfflineDel.TABLE_NAME + " (" +
            OfflineDel._ID + " INTEGER PRIMARY KEY," +
            OfflineDel.MUID + TEXT_TYPE + COMMA_SEP +
            OfflineDel.MID + TEXT_TYPE +
            " )";
    
    private static final String SQL_DELETE_MLOG =
            "DROP TABLE IF EXISTS " + OfflineDel.TABLE_NAME;
    
    public MofflineDBHelper(Context context) {
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
    
    public Cursor fetchAllLogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query(
        		OfflineDel.TABLE_NAME,  // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                                   // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
                );
        
        return cur;
    }
}
