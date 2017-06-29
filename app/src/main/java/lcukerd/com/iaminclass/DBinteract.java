package lcukerd.com.iaminclass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Programmer on 18-06-2017.
 */

public class DBinteract {

    private eventDBcontract dBcontract ;
    private String[] projection = {
            eventDBcontract.ListofItem.columnID,
            eventDBcontract.ListofItem.columnEvent,
            eventDBcontract.ListofItem.columnName,
            eventDBcontract.ListofItem.columntaken,
            eventDBcontract.ListofItem.columnreturn,
            eventDBcontract.ListofItem.columnFileloc,
            eventDBcontract.ListofItem.columndatetime,
            eventDBcontract.ListofItem.columnnotes
    };


    DBinteract(Context context)
    {
        dBcontract = new eventDBcontract(context);
    }

    public Cursor readfromDB(String order)
    {
        SQLiteDatabase db = dBcontract.getReadableDatabase();
        int n=0;

        Cursor cursor = db.query(eventDBcontract.ListofItem.tableName,projection,null,null,null,null,order);

        while(cursor.moveToNext())
        {
            Log.d("column return",cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnID))+" "+
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnEvent))+" "+
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnName))+" "+
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columntaken))+" "+
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnreturn))+" "+
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnFileloc))+" "+
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columndatetime))+" "+
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnnotes)));
        }
        return cursor;
    }


}
