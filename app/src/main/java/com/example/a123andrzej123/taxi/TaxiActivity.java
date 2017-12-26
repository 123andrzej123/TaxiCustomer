package com.example.a123andrzej123.taxi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class TaxiActivity extends Activity {
    private int taxiNumber = 512186134;

    private final int REQUEST_PERMISSION_CALL_PHONE = 1;
    private final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 2;

    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL_PHONE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_ACCESS_COARSE_LOCATION);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
        Toast.makeText(this, getString(R.string.call_taxi_internet_na), Toast.LENGTH_LONG).show();
    }

    public void callTaxiSms(View view) {
        final Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", String.valueOf(taxiNumber), null));
        smsIntent.putExtra("sms_body", getString(R.string.call_taxi_sms_simple_body));
        startActivity(smsIntent);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        smsIntent.putExtra("sms_body", getString(R.string.call_taxi_sms_simple_body) + " http://www.google.com/maps/place/" +
                                String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
                        startActivity(smsIntent);
                    } else {
                        smsIntent.putExtra("sms_body", getString(R.string.call_taxi_sms_simple_body));
                        startActivity(smsIntent);
                    }
                }
            });
        } else {
            smsIntent.putExtra("sms_body", getString(R.string.call_taxi_sms_simple_body));
            startActivity(smsIntent);
        }
    }
}
