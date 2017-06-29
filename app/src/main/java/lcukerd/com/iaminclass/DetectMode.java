package lcukerd.com.iaminclass;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
    private float history[][];
    private int counter=0;
    private final String tag = "Detector";
    private boolean screenon = true;

    /*public DetectMode(Context context)
    {
        calendar = Calendar.getInstance();
        time = calendar.getTimeInMillis();
        history = new float[10][3];
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometer,2000);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        context.registerReceiver(mReceiver, filter);

        if (accelerometer==null)
            Toast.makeText(context,"No Sensor",Toast.LENGTH_SHORT).show();                          // Show error in dialogbox
    }*/
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
        history = new float[10][3];
        sensorManager = (SensorManager) this.getSystemService(this.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometer,2000);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        this.registerReceiver(mReceiver, filter);

        if (accelerometer==null)
            Toast.makeText(this,"No Sensor",Toast.LENGTH_SHORT).show();                          // Show error in dialogbox
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mNM.cancel(105);

        sensorManager.unregisterListener(this);
        Toast.makeText(this,"I am in class stopped!!" , Toast.LENGTH_SHORT).show();
    }

    private void showNotification()
    {
        Notification notification = new Notification.Builder(this)
                .setContentTitle("I am in Class running!")
                .setSmallIcon(R.drawable.ic_library_add_black_24dp)
                .setTicker("Ticker")
                .setWhen(System.currentTimeMillis())
                .setContentText("Content")
                .build();
        //mNM.notify(105,notification);
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
        if (calendar.getTimeInMillis()-time > 1000)
        {
            int sit=0,stand=0;
            float x,y,z;
            x=history[counter][0] = event.values[0];
            y=history[counter][1] = event.values[1];
            z=history[counter++][2] = event.values[2];
            float g = (float) Math.sqrt(x*x+y*y+z*z);
            Log.d(tag  + "data",Float.toString(x)+" "+Float.toString(y)+" " +Float.toString(z)+" "+Float.toString(g));

            for (int j=0;j<10;j++)
            {
                int state = posedetect(history[j][0],history[j][1],history[j][2]);
                if (state==0)
                    sit++;
                else if (state==1)
                    stand++;
            }
            if (sit>4)
                Log.d(tag,"Sitting");
            else if (stand>4)
                Log.d(tag,"Standing");
            else
                Log.d(tag,"Don't Know");
            if (counter==10)
                counter=0;

            time = calendar.getTimeInMillis();
        }
        else
        {
            //Log.d("Time ",Long.toString(calendar.getTimeInMillis())+" "+ Long.toString(time));
        }
    }

    private int posedetect(float x, float y, float z)
    {
        float g = (float) Math.sqrt(x*x+y*y+z*z);
        if (((z>=8.5)||(z<=-9))&&(g>9.4)&&(g<=11))                                                  //sitting straight or phone on table
        {
            if ((x<=1.5)&&(x>=-1.5)&&(screenon==true))                                              //using phone ***"Show notificaiton"*** set to not in class
            {
                Log.d(tag,"Using phone");
                return 0;
            }
            else
            {
                Log.d(tag,"Normal sitting");
                return 0;
            }
        }
        else if ((y>9)||(y<-9)||(g<9.5))                                                            //standing
        {
            Log.d(tag,"Normal Standing");
            return 1;
        }
        else if (((g>10)&&((x>9.8)||(y>9.8)||(z>9.8)||(x<-9.8)||(y<-9.8)||(z<-9.8))))               //walking
        {
            Log.d(tag,"Walking");
            return 1;
        }
        else if ((x>9.8)||(x<-9.8))                                                                 //phone in hand(hand laying beside leg) while standing
        {
            Log.d(tag,"Phone in hand while standing");
            return 1;
        }
        else if (((x<2.2)&&(x>-2))&&(g>9.4)&&(g<=11))                                                // 4.299643 -3.112195 -8.340738 9.886385 (sitting with leg folded)
        {
            Log.d(tag,"sitting with leg folded");
            return 0;
        }
        else
        {
            Log.d(tag,"No condition satisfied");
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


}
