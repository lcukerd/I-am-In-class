package lcukerd.com.iaminclass;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Programmer on 18-06-2017.
 */

public class eventDBcontract extends SQLiteOpenHelper{

    private static final String SQL_CREATE_ENTRIES1 =
            "CREATE TABLE " + ListofItem.tableName1 + " (" +
                    ListofItem.columnID + " INTEGER PRIMARY KEY," +
                    ListofItem.columnstate + " INTEGER, " +
                    ListofItem.columnx + " REAL, " +
                    ListofItem.columny + " REAL," +
                    ListofItem.columnz + " REAL, " +
                    ListofItem.columng + " REAL );";
    private static final String SQL_CREATE_ENTRIES2 =
            "CREATE TABLE " + ListofItem.tableName2 + " (" +
                    ListofItem.columnID + " INTEGER PRIMARY KEY," +
                    ListofItem.columndate + " INTEGER, " +
                    ListofItem.columnstart + " INTEGER, " +
                    ListofItem.columnend + " INTEGER," +
                    ListofItem.columnnote + " TEXT );";

    public static int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Iaminclass.db";

    public eventDBcontract(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES1);
        db.execSQL(SQL_CREATE_ENTRIES2);
        Log.d("Database","created");
    }

    public void onUpgrade(SQLiteDatabase db,int oldVersion , int newVersion)
    {
    }

    public static class ListofItem
    {
        public static final String tableName1 = "dayrecord",
                columnID="ID",
                columnx = "X",
                columny = "Y",
                columnz = "Z",
                columng = "G",
                columnstate = "state";
        public static final String tableName2 = "LifetimeRecord",
                columnstart = "Start",
                columnend = "End",
                columnnote = "Note",
                columndate = "Date";
    }
}

