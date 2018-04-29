package org.ieselcaminas.alu53787365w.exam2017;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class GetPostalCodeService extends IntentService {

    public GetPostalCodeService() {
        super("GetPostalCodeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String URL = intent.getExtras().getString("URL"); //Obtenim el valor de la clau URL
        String allJSON = getAllJSON(URL); //Contingut de totes les dades JSON

        String postalCode = "Postal code: " + getPostalCodeFromJSON(allJSON); //Extraem el valor del codi postal
        Intent broadcastIntent = new Intent();
        //Aquest paquet te que coincidir amb el objecte intentFilter de la clase principal
        broadcastIntent.setAction(MainActivity.MyReceiver.PROCESS_RESPONSE);
        broadcastIntent.putExtra("postalCode", postalCode);
        sendBroadcast(broadcastIntent); //Enviem el broadcast
    }
    private String getAllJSON(String URL){
        InputStream in;
        BufferedReader inReader;
        String s = null;
        String responseMessage = "";
        StringBuilder stBuilder = new StringBuilder();
        try {
            in = openHttpConnection(URL);
            inReader = new BufferedReader(new InputStreamReader(in));
            while ((s = inReader.readLine()) != null) {
                stBuilder.append(s+"\n");
            }
            responseMessage = stBuilder.toString();

            in.close();
        } catch (IOException e1) {
            Log.d("NetworkingActivity", e1.getLocalizedMessage());
        }
        return responseMessage;
    }
    private String getPostalCodeFromJSON(String JSONString) {
        String address = null;
        try {
            boolean found = false;
            address = "No data for that latitude and longitude";
            Log.d("Checking",JSONString);
            JSONObject rootJSON = new JSONObject(JSONString);
            JSONArray arrayJSON = rootJSON.getJSONArray("results");
            JSONArray arrayComponents = arrayJSON.getJSONObject(0).getJSONArray("address_components");
            for (int i=0; i<arrayComponents.length(); i++) {
                JSONObject jsonObject = arrayComponents.getJSONObject(i);
                JSONArray arr = jsonObject.getJSONArray("types");
                for (int j=0; j<arr.length(); j++) {
                    if (arr.getString(j).equalsIgnoreCase("postal_code")) {
                        address = jsonObject.getString("long_name");
                        found = true;
                        break;
                    }
                }
                if (found)
                    break;
            }

        } catch (JSONException ex) {
            Log.d("JSONActivity", ex.getLocalizedMessage());
        }

        return address;
    }
    private InputStream openHttpConnection(String urlString) throws IOException {
        InputStream in = null;
        int response;
        java.net.URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex) {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }

}