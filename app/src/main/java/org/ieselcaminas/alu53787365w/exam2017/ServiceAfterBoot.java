package org.ieselcaminas.alu53787365w.exam2017;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class ServiceAfterBoot extends Service {

        @Override
        public IBinder onBind(Intent intent) {
                 return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
               Toast.makeText(this, "Service Started afterBoot", Toast.LENGTH_LONG).show();
               return START_STICKY;
        }

}