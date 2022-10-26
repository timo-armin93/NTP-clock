// Tim Toikkanen
// NPT clock App

package com.example.ntpclock;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;
import java.time.Clock;
import java.util.Date;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    private TextView UITxt;
    private UIHandler UIHandler;


   private static final String NTP_SERVER = "se.pool.ntp.org"; // NTP server address

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UITxt = (TextView) findViewById(R.id.Clock); // set clock attribute ID
        Date date = new Date(); // create date object
        UIHandler = new UIHandler(); // create handler object




    }
    // start thread method
    public void startThread(View view) {
        MyThread thread = new MyThread(3); // count seconds
        thread.start(); // start thread
        System.out.println("System time: " + new Date());

    }

    class MyThread extends Thread{
        int seconds;

        MyThread(int seconds){
           this.seconds = seconds;
        }
        @Override
        public void run() {
            Date ntpServerDate = getNtpServerDate();
            for (int i = 0; i < seconds; i++){ // convert to seconds
                Log.d(TAG, "startThread: " + i);
                try {
                    Thread.sleep(4000); // sleep for slowing down thread
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                UIHandler.handleMessage(ntpServerDate); // get ntp time from Handler
            }
        }
    }

    // get real time from NTP server
    public static Date getNtpServerDate() {
        NTPUDPClient ntpudpClient = new NTPUDPClient();
        ntpudpClient.setDefaultTimeout(1000); // begin ntp after 1 sec
        TimeInfo timeInfo;
        try {
            InetAddress inetAddress = InetAddress.getByName(NTP_SERVER);
            timeInfo = ntpudpClient.getTime(inetAddress);
            long RemoteNTPTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            Date date = new Date(RemoteNTPTime);
            //System.out.println("NTP time: " + date); // get ntp time
            return date;
        } catch (Exception e) {
            //System.out.println("System time: " + new Date()); // get system time
            System.out.println(e);
            return new Date();
        }
    }

    private class UIHandler extends Handler{
        public void handleMessage(Date date) {

            // update thread for better connection and send in ntp time
            runOnUiThread (()->{UITxt.setText(date.toString());});
        }
    }
}