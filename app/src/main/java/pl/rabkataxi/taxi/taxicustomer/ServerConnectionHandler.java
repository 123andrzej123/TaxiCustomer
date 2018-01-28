package pl.rabkataxi.taxi.taxicustomer;

import android.media.midi.MidiInputPort;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by AK_HP on 2018-01-27.
 */

public class ServerConnectionHandler extends AsyncTask<String, String, Void> {
    String address;
    int port;
    TaxiCustomerActivity taxiCustomerActivity;

    Socket socket;

    ServerConnectionHandler(TaxiCustomerActivity taxiCustomerActivity, String address, int port) {
        this.address = address;
        this.port = port;
        this.taxiCustomerActivity = taxiCustomerActivity;
    }

    @Override
    protected Void doInBackground(String... urls) {
        while (true) {
            try {
                Log.d("kbu", "ServerConnectionHandler.doInBackground");

                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();

                InputStream in = new BufferedInputStream(connection.getInputStream());
                publishProgress(streamToString(in));

                Thread.sleep(2000);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        try {
            JSONObject json = new JSONObject(values[0]);
            taxiCustomerActivity.setTaxiLocation(json.optString("id"));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

//    @Override
//    protected void onPostExecute() {

//        try {
//            JSONObject json = new JSONObject(result);
//            taxiCustomerActivity.setTaxiLocation(json.optString("id"));
//
//            this.execute()
//        }
//        catch (JSONException ex){
//            ex.printStackTrace();
//        }
//    }

    public static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            reader.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
