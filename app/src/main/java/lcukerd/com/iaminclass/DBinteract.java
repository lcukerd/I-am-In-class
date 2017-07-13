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

    private eventDBcontract dBcontract;
    private String tag = "DBInteract";
    private String[] projection1 = {
            eventDBcontract.ListofItem.columnID,
            eventDBcontract.ListofItem.columnstate,
            eventDBcontract.ListofItem.columnx,
            eventDBcontract.ListofItem.columny,
            eventDBcontract.ListofItem.columnz,
            eventDBcontract.ListofItem.columng
    };
    private String[] projection2 = {
            eventDBcontract.ListofItem.columnID,
            eventDBcontract.ListofItem.columndate,
            eventDBcontract.ListofItem.columnstart,
            eventDBcontract.ListofItem.columnend,
            eventDBcontract.ListofItem.columnnote
    };


    DBinteract(Context context)
    {
        dBcontract = new eventDBcontract(context);
    }

    public Cursor readfromDB(int table , String order)
    {
        SQLiteDatabase db = dBcontract.getReadableDatabase();
        Cursor cursor = null;

        if (table == 1)
        {
            cursor = db.query(eventDBcontract.ListofItem.tableName1, projection1, null, null, null, null, order);
            /*while (cursor.moveToNext())
            {
                Log.d("column return", cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnID)) + " " +
                        cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnstate)) + " " +
                        cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnx)) + " " +
                        cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columny)) + " " +
                        cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnz)) + " " +
                        cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columng)));
            }*/
        }
        else if (table == 2)
        {
            cursor = db.query(eventDBcontract.ListofItem.tableName2, projection2, null, null, null, null, order);
            /*while (cursor.moveToNext())
            {
                Log.d("column return", cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnID)) + " " +
                        cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columndate)) + " " +
                        cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnstart)) + " " +
                        cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnend)) + " " +
                        cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnnote)));
            }*/
        }
        return cursor;
    }
    public void addacceleration(int state,float x, float y, float z,float g)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(eventDBcontract.ListofItem.columnstate,state);
        values.put(eventDBcontract.ListofItem.columnx,x);
        values.put(eventDBcontract.ListofItem.columny,y);
        values.put(eventDBcontract.ListofItem.columnz,z);
        values.put(eventDBcontract.ListofItem.columng,g);

        db.insert(eventDBcontract.ListofItem.tableName1,null,values);
        Log.d(tag,"values added");

    }

    /*public Cursor readinEvent(String event,String order)
    {
        SQLiteDatabase db = dBcontract.getReadableDatabase();
        Cursor cursor = db.query(eventDBcontract.ListofItem.tableName,projection,eventDBcontract.ListofItem.columnEvent+" = '"+event+"'",null,null,null,order);
        return cursor;
    }
    public String readNote(String id)
    {
        SQLiteDatabase db = dBcontract.getReadableDatabase();
        Cursor cursor = db.query(eventDBcontract.ListofItem.tableName,projection,eventDBcontract.ListofItem.columnID+" = "+id,null,null,null,null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnnotes));

    }

    public int readstatus(String eventName)
    {
        SQLiteDatabase db = dBcontract.getReadableDatabase();
        Cursor cursor = db.query(eventDBcontract.ListofItem.tableName,projection,eventDBcontract.ListofItem.columnEvent+" = '"+eventName+"'",null,null,null,eventDBcontract.ListofItem.columnName+" ASC");
        while(cursor.moveToNext())
        {
            String data = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnName));
            if (data.length()>=2)
                if ((data.charAt(0)=='#')&&(data.charAt(1)=='%'))
                {
                    if (System.currentTimeMillis()<Long.parseLong(cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columntaken))))
                        return 0;
                    else if (System.currentTimeMillis()<Long.parseLong(cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnreturn))))
                        return 1;
                    else
                        return 0;
                }
        }
        return 0;
    }

    public void save(String eventName, String itemName, CheckBox taken, CheckBox returned, Uri photoURI , String caller , String id)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(eventDBcontract.ListofItem.columnEvent,eventName);
        values.put(eventDBcontract.ListofItem.columnName,itemName);
        values.put(eventDBcontract.ListofItem.columndatetime,getmillis());
        if (taken.isChecked())
            values.put(eventDBcontract.ListofItem.columntaken,"1");
        else
            values.put(eventDBcontract.ListofItem.columntaken,"0");
        if (returned.isChecked())
            values.put(eventDBcontract.ListofItem.columnreturn,"1");
        else
            values.put(eventDBcontract.ListofItem.columnreturn,"0");
        if (photoURI!=null) {
            values.put(eventDBcontract.ListofItem.columnFileloc, photoURI.toString());
            Log.d("File address write", photoURI.toString());
        }
        else
            values.putNull(eventDBcontract.ListofItem.columnFileloc);
        if (caller.equals("main"))
        {
            db.insert(eventDBcontract.ListofItem.tableName,null,values);
            Log.d("save operation","complete");
        }
        else
        {
            db.update(eventDBcontract.ListofItem.tableName,values,"id=?",new String[]{id});
            Log.d("update operation","complete");
        }
    }

    public void saveEvent(String eventName , String itemName , long start , long end , String id)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(eventDBcontract.ListofItem.columnEvent,eventName);
        values.put(eventDBcontract.ListofItem.columnName,"#%"+itemName);
        if (start!=-1)
            values.put(eventDBcontract.ListofItem.columntaken,start);
        if (end!=-1)
            values.put(eventDBcontract.ListofItem.columnreturn,end);

        db.update(eventDBcontract.ListofItem.tableName,values,"id=?",new String[]{id});

        Log.d("save operation","Event detail complete");
    }
    public void savenote(String id , String note)
    {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = dBcontract.getWritableDatabase();

        values.put(eventDBcontract.ListofItem.columnnotes,note);
        Log.d("Interact",String.valueOf(db.update(eventDBcontract.ListofItem.tableName,values,"id=?",new String[]{id})));
        Log.d("Interact","add note complete " + note);

    }*/



}
