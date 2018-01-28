package pl.rabkataxi.taxi.taxicustomer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a123andrzej123.taxicustomer.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class TaxiCustomerActivity extends Activity {
    private int taxiNumber = 512186134;

    private final int REQUEST_PERMISSION_CALL_PHONE = 1;
    private final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 2;
    private final int REQUEST_PERMISSION_INTERNET = 4;


    private FusedLocationProviderClient fusedLocationClient;

    private TextView taxiLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL_PHONE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, REQUEST_PERMISSION_INTERNET);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        taxiLocation = findViewById(R.id.taxiLocation);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CALL_PHONE) {
            if ((grantResults.length <= 0) || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                View viewButtonCallTaxiPhone = findViewById(R.id.buttonCallTaxiPhone);
                viewButtonCallTaxiPhone.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGrey));
            }
        }
    }

    public void callTaxiPhone(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.call_taxi_phone_na), Toast.LENGTH_LONG).show();
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + taxiNumber));
            startActivity(callIntent);
        }
    }

    public void callTaxiInternet(View view) {
        initServerConnection();
//        Toast.makeText(this, getString(R.string.call_taxi_internet_na), Toast.LENGTH_LONG).show();
    }

    public void callTaxiSms(View view) {
        final Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", String.valueOf(taxiNumber), null));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.call_taxi_location_na_alert_title));
                builder.setMessage(getString(R.string.call_taxi_location_na_alert_text));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
                Dialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                //ToDo jeżeli użytownik włączy lokalizację to niech SMS się stworzy
                return;
            }

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            location = (location != null) ? location : locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                smsIntent.putExtra("sms_body", getString(R.string.call_taxi_sms_simple_body) + " http://www.google.com/maps/place/" +
                        String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
                startActivity(smsIntent);
            } else {
                smsIntent.putExtra("sms_body", getString(R.string.call_taxi_sms_simple_body));
                startActivity(smsIntent);
            }
        } else {
            smsIntent.putExtra("sms_body", getString(R.string.call_taxi_sms_simple_body));
            startActivity(smsIntent);
        }
    }

    public void setTaxiLocation(String location) {
        this.taxiLocation.setText(location);
    }

    private void initServerConnection() {
        Log.d("kbu", "initServerConnection");
        //ToDo wczytywanie konfiguracji serwera z pliku? z witryny? skąd?
        ServerConnectionHandler serverConnectionHandler = new ServerConnectionHandler(this, "192.168.0.103", 5000);
        serverConnectionHandler.execute("http://damianchodorek.com/wsexample/");
    }


}
