package com.example.localisation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PERM_GPS = 42;

    private TextView tvLatitude, tvLongitude, tvStatus, tvLastUpdate;
    private RequestQueue networkQueue;
    private LocationManager gpsManager;

    // ⚠️ Émulateur → 10.0.2.2 | Vrai téléphone → IP Wi-Fi du PC
    private static final String SAVE_URL = "http://10.0.2.2/localisation/api/save.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLatitude   = findViewById(R.id.tvLatitude);
        tvLongitude  = findViewById(R.id.tvLongitude);
        tvStatus     = findViewById(R.id.tvStatus);
        tvLastUpdate = findViewById(R.id.tvLastUpdate);
        Button btnOpenMap = findViewById(R.id.btnOpenMap);

        networkQueue = Volley.newRequestQueue(getApplicationContext());
        gpsManager   = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btnOpenMap.setOnClickListener(v ->
                startActivity(new Intent(this, MapsActivity.class))
        );

        requestGpsPermission();
    }

    private void requestGpsPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, PERM_GPS);
        } else {
            beginTracking();
        }
    }

    @SuppressLint("MissingPermission")
    private void beginTracking() {
        tvStatus.setText("Acquisition du signal...");
        gpsManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 60000, 150, gpsListener
        );
    }

    private final LocationListener gpsListener = new LocationListener() {

        @Override
        public void onLocationChanged(@NonNull Location loc) {
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();

            // Mise à jour interface
            tvLatitude.setText(String.format(Locale.getDefault(), "%.6f", lat));
            tvLongitude.setText(String.format(Locale.getDefault(), "%.6f", lng));
            tvStatus.setText("Signal actif ");

            // Heure de la dernière mise à jour
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss", Locale.getDefault());
            tvLastUpdate.setText(fmt.format(new Date()));

            uploadTrackPoint(lat, lng);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            tvStatus.setText("GPS activé");
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            tvStatus.setText("GPS désactivé !");
        }
    };

    private void uploadTrackPoint(final double lat, final double lng) {
        StringRequest req = new StringRequest(Request.Method.POST, SAVE_URL,
                response -> { /* succès silencieux */ },
                error -> Toast.makeText(getApplicationContext(),
                        "Envoi échoué : " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Map<String, String> body = new HashMap<>();
                body.put("lat",    String.valueOf(lat));
                body.put("lng",    String.valueOf(lng));
                body.put("ts",     fmt.format(new Date()));
                body.put("device", resolveDeviceId());
                return body;
            }
        };
        networkQueue.add(req);
    }

    private String resolveDeviceId() {
        String aid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (aid != null && !aid.isEmpty()) return aid;
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (tel != null && tel.getDeviceId() != null) return tel.getDeviceId();
            }
        } catch (Exception ignored) {}
        return "DEVICE_UNKNOWN";
    }

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] perms, @NonNull int[] results) {
        super.onRequestPermissionsResult(code, perms, results);
        if (code == PERM_GPS && results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
            beginTracking();
        } else {
            Toast.makeText(this, "Permission GPS refusée", Toast.LENGTH_LONG).show();
        }
    }
}