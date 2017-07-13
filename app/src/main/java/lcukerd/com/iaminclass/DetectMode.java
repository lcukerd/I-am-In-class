package lcukerd.com.iaminclass;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Programmer on 16-06-2017.
 */

public class DetectMode extends Service implements SensorEventListener
{
    private SensorManager sensorManager;
    private NotificationManager mNM;
    private Sensor accelerometer;
    private Calendar calendar;
    private long time;
    private int size = 60, mode = -1;
    private final String tag = "Detector";
    private boolean screenon = true;
    private OutputStreamWriter osw;
    private String currentPose,currentval;
    private DBinteract interact;

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
        Log.d(tag,"onCreate started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        calendar = Calendar.getInstance();
        time = calendar.getTimeInMillis();
        interact = new DBinteract(this);
        sensorManager = (SensorManager) this.getSystemService(this.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometer,2000);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        this.registerReceiver(mReceiver, filter);
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/" +  "samplefile.txt");
            osw = new OutputStreamWriter(new FileOutputStream(file,true));
        } catch (Exception e) {
            Log.e(tag,"File creation error",e);
        }

        if (accelerometer==null)
            Toast.makeText(this,"No Sensor",Toast.LENGTH_SHORT).show();                          // Show error in dialogbox
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        try{
            osw.close();
        }
        catch (Exception e)
        {
            Log.e(tag,"File Close Error",e);
        }
        mNM.cancel(105);
        sensorManager.unregisterListener(this);
        Toast.makeText(this,"I am in Class stopped!!" , Toast.LENGTH_SHORT).show();
    }

    private void showNotification()
    {
        Notification notification = new Notification.Builder(this)
                .setContentTitle("App running!")
                .setSmallIcon(R.drawable.ic_library_add_black_24dp)
                .setTicker("Ticker")
                .setWhen(System.currentTimeMillis())
                .setContentText("Content")
                .setPriority( Notification.PRIORITY_MIN )
                .build();
        startForeground(105,notification);
    }
    public class LocalBinder extends Binder {
        DetectMode getService() {
            return DetectMode.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        calendar = Calendar.getInstance();
        if (calendar.getTimeInMillis()-time > 2000)
        {
            int sit=0,stand=0;
            float x,y,z;
            x=event.values[0];
            y=event.values[1];
            z=event.values[2];
            float g = (float) Math.sqrt(x*x+y*y+z*z);
            currentval = Float.toString(x).substring(0,3)+" "+Float.toString(y).substring(0,4)+" " +Float.toString(z).substring(0,4)+" "+Float.toString(g).substring(0,4);
            interact.addacceleration(posedetect(x,y,z,g),x,y,z,g);
            Cursor cursor = interact.readfromDB(1,eventDBcontract.ListofItem.columnID + " DESC");
            for (int j=0;j<size;j++)
            {
                int state=0;
                if(cursor.moveToNext())
                    state = cursor.getInt(cursor.getColumnIndex(eventDBcontract.ListofItem.columnstate));
                if (state==0)
                    sit++;
                else if (state==1)
                    stand++;
            }

            String temp;
            int tempmode = -2;
            if (sit>size/2-1)
            {
                tempmode = 0;                                                                       //ask in setting if to silent or vibrate in class
                temp = "Sitting";
            }
            else if (stand>size/2-1)
            {
                tempmode = 2;
                temp = "Standing";
            }
            else
                temp = "Don't Know";
            if (tempmode != mode)                                                                   // ask in setting if mode should change
                setaudiomode(tempmode);                                                             // back automatically when change explicitly
            mode = tempmode;
            Log.d(tag  + "data",currentval + " " + currentPose + " " + temp + " " + String.valueOf(mode)+ " " + String.valueOf(tempmode));
            //writetofile(temp +" " + currentPose + " " + currentval);                              // write only in case of error
            time = calendar.getTimeInMillis();
        }
    }

    private int posedetect(float x, float y, float z,float g)
    {
        String temp;
        if (((z>=8.5)||(z<=-9))&&(g>9.3)&&(g<=11))                                                  //sitting straight or phone on table
        {
            if ((x<=1.5)&&(x>=-1.5)&&(screenon==true))                                              //using phone ***"Show notificaiton"*** set to not in class
            {
                temp = "using phone";
                currentPose = temp;
                Log.d(tag,temp);
                return 0;
            }
            else
            {
                temp = "Normal Sitting";
                Log.d(tag,temp);
                currentPose = temp;
                return 0;
            }
        }
        else if (((y>9)||(y<-9))&&(g<11)&&(g>9.4))                                                            //standing
        {
            temp = "Normal Standing";
            Log.d(tag,temp);
            currentPose = temp;
            return 1;
        }
        else if (((g>10)&&((x>9.8)||(y>9.8)||(z>9.8)||(x<-9.8)||(y<-9.8)||(z<-9.8))))               //walking
        {
            temp = "Walking";
            Log.d(tag,temp);
            currentPose = temp;
            return 1;
        }
        else if (((x<9.9)&&(x>9.0))||((x>-9.9)&&(x<-9.0)))                                                                 //phone in hand(hand laying beside leg) while standing
        {
            temp = "Phone in hand while standing";
            Log.d(tag,temp);
            currentPose = temp;
            return 1;
        }
        else if (((x<2.2)&&(x>-2))&&(g>9.4)&&(g<=11))                                                // 4.299643 -3.112195 -8.340738 9.886385 (sitting with leg folded)
        {
            temp = "sitting with leg folded";
            Log.d(tag,temp);
            currentPose = temp;
            return 0;
        }
        else
        {
            temp = "No condition satisfied";
            Log.d(tag,temp);
            currentPose = temp;
            return 2;
        }
    }

    public class ScreenReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
            {
                screenon = false;
            }
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
            {
                screenon = true;
            }
        }

    }
    private void writetofile(String data)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
        String time = sdf.format(Calendar.getInstance().getTime());
        Log.d(tag,time);
        try {
            osw.append(time + " : " + data + "\n");
        }
        catch (Exception e)
        {
            Log.e(tag,"File Write error",e);
        }
    }
    private void setaudiomode(int mode)
    {
        AudioManager audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (mode == 0)
            audiomanage.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else if (mode == 1)
            audiomanage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        else if (mode == 2)
            audiomanage.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }


}
