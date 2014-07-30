package com.carmen.provacampionamentoaccelerometro;

/**
 * Created by carmen on 27/07/14.
 */

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;



public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private TextView view;
    private List<AccelerationSample> accelerations;
    private long lastUpdate;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        accelerations = new LinkedList<AccelerationSample>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = (TextView)findViewById(R.id.tv);
        view.setBackgroundColor(Color.GREEN);


        //loadSamples();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 200000);//microseconds
        lastUpdate = System.currentTimeMillis();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    }

    private void getAccelerometer(SensorEvent event) {

        float[] values = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];



        long eventTime = ((new Date())).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;//from nanosec to millisec
        //long eventTime = (event.timestamp) / 1000000L;//from nanosec to millisec


        if(eventTime-(lastUpdate) < 20) {
            return;
        }
            lastUpdate = eventTime;
            AccelerationSample av = new AccelerationSample(x,y,z, lastUpdate );
            view.setText("Acceleration components:\n x = " + x + "\ty = " + y + "\t z = " + z+" \t"+getDate(lastUpdate,"dd/MM/yyyy hh:mm:ss.SSS"));
        //view.setText("Acceleration components:\n x = " + x + "\ty = " + y + "\t z = " + z+" \t"+eventTime);

        accelerations.add(av);

    }

    public void saveSamples() {
        File directory = getFilesDir();
        File file = new File(directory, "samples.txt");

        try {
            if(!file.exists())
                file.createNewFile();

            ObjectOutput output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            try {
                for (AccelerationSample a : accelerations) {
                    output.writeObject(a);
                }

            } finally {
                output.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSamples();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                200000);
    }


    public void loadSamples() {
        File directory = getFilesDir();
        File file = new File(directory, "samples.txt");
        if(file.exists()){
            try {
                InputStream is = new FileInputStream(file);
                InputStream buffer = new BufferedInputStream(is);
                ObjectInput input = new ObjectInputStream(buffer);

                AccelerationSample accelerationSample = null;

                try {
                    while ((accelerationSample = (AccelerationSample)input.readObject()) != null) {
                        accelerations.add(accelerationSample);
                    }
                } finally {
                    input.close();
                }
            }catch(ClassNotFoundException ex){}
            catch (IOException e) {
                e.printStackTrace();
            }
        }

       String[] accSamples = new String[accelerations.size()];

        for(int  i = 0; i < accelerations.size(); i++)
           accSamples[i] = String.valueOf(accelerations.get(i));


        ListView lv = (ListView) findViewById(R.id.samplesList);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.row_sample, R.id.sample, accSamples);
        lv.setAdapter(arrayAdapter);


    }


    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }



    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        saveSamples();
    }

    public void onDestroy() {
        saveSamples();
         super.onDestroy();

    }


    public void onStop() {
        saveSamples();
        super.onStop();

    }


    public void onRestart() {
        super.onRestart();
        loadSamples();
    }
}
