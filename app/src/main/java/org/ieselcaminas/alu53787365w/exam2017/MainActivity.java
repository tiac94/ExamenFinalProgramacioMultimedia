package org.ieselcaminas.alu53787365w.exam2017;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private Button button;
    private SharedPreferences pref;

    private String setup, config;
    Boolean displayLocation, sendNotification;
    private double latitude, longitude;
    private String latitudeStr, longitudeStr;
    private TextView textView;

    public class MyReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "net.victoralonso.intent.action.PROCESS_RESPONSE";
        @Override
        public void onReceive(Context context, Intent intent) {
            //Arrepleguem el valor del codi postal i el posem en un textView
            String postalCode = intent.getStringExtra("postalCode");
            textView.setText(postalCode);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyPreferencesActivity.class);
                startActivity(intent);
            }
        });
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);

        //textView.setVisibility(View.VISIBLE);
        //Enviem un intentFilter amb un nom de paquet
        IntentFilter filter = new IntentFilter(MyReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        MyReceiver receiver = new MyReceiver();
        registerReceiver(receiver, filter); //Registrem el objecte broadcast i el intentFilter

    }


    @Override
    public void onResume() {
        super.onResume();
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setup = pref.getString("option3", "setup");
        config = pref.getString("option4", "config");
        displayLocation = pref.getBoolean("option1", false);
        sendNotification = pref.getBoolean("option2", false);


        AlternaParaules alter = new AlternaParaules();

        //alter.execute(setup, config);
        alter.execute("", "");
    }

    @Override
    public void onLocationChanged(Location location) {
        //Obtenim la latitud i la longitud actual
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        latitudeStr = "" + latitude;
        longitudeStr = "" + longitude;
        String URLStr = "http://maps.googleapis.com/maps/api/geocode/json?" +
                "latlng=" + latitudeStr + "," + longitudeStr + "&sensor=false";

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //Anem al servei per aconseguir al codi postal
            Intent intent = new Intent(this, GetPostalCodeService.class);
            intent.putExtra("URL",URLStr); //Li passem la clau amb l'enllaç
            startService(intent);
        } else {
            Toast.makeText(this,"No internet connection available.",Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }


    public class AlternaParaules extends AsyncTask<String, String, String> {
        int i = 0;
        boolean bool = true;

        @Override
        protected void onProgressUpdate(String... values) {
            //Aci s'actualitza el text del button
            button.setText(values[0]);
        }
        @Override
        protected String doInBackground(String... values) {
            //Forcem el bucle infinit
            while(i == 0) {
                if(bool){
                    values[0] = setup; //Asignem el valor del primer parametre a la variable setup perque s'actualitze el valor
                    publishProgress(values[0]); //Li passem el primer parametre al mètode publishProgress
                    bool = false;
                }
                else{
                    values[1] = config; //Asignem el valor del segon parametre a la variable config perque s'actualitze el valor
                    publishProgress(values[1]); //Li passem el segon parametre al mètode publishProgress
                    bool = true;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        protected void onPostExecute(String result) {
            button.setText(result);
        }

    }


}