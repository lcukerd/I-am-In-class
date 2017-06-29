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

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ListofItem.tableName + " (" +
                    ListofItem.columnID + " INTEGER PRIMARY KEY," +
                    ListofItem.columnEvent + " TEXT, " +
                    ListofItem.columnName + " TEXT, " +
                    ListofItem.columntaken + " INTEGER," +
                    ListofItem.columnreturn + " INTEGER, " +
                    ListofItem.columnFileloc + " TEXT, " +
                    ListofItem.columndatetime + " INTEGER, " +
                    ListofItem.columnnotes + " TEXT );";
    private ContentValues values;

    public static int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ClassRecord.db";

    public eventDBcontract(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d("Database","created");
        for (int i=0;i<9;i++) {
            values = new ContentValues();
            db.insert(eventDBcontract.ListofItem.tableName, null, values);
        }

    }

    public void onUpgrade(SQLiteDatabase db,int oldVersion , int newVersion)
    {
    }

    public static class ListofItem
    {
        public static final String tableName = "ClassRecord",
                columnID="ID",
                columnEvent = "Name_of_event",
                columntaken = "Item_taken",
                columnreturn = "Item_brought_back",
                columnName = "Name_of_Item",
                columnFileloc = "File_Location",
                columndatetime = "Creation_millisec",
                columnnotes = "Note";
    }
}

