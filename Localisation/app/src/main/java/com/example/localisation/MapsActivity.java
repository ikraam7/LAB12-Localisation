package com.example.localisation;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapsActivity extends AppCompatActivity {

    private MapView osmMap;
    private RequestQueue networkQueue;

    // ⚠️ Remplacer par ton IP locale
    private static final String FETCH_URL = "http://10.0.2.2/localisation/api/fetch.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );

        setContentView(R.layout.activity_maps);

        osmMap = findViewById(R.id.osmMap);
        osmMap.setTileSource(TileSourceFactory.MAPNIK);
        osmMap.setMultiTouchControls(true);

        // Position initiale Marrakech pendant le chargement
        osmMap.getController().setZoom(15.0);
        osmMap.getController().setCenter(new GeoPoint(33.5731, -7.5898));

        networkQueue = Volley.newRequestQueue(getApplicationContext());
        loadMarkers();
    }

    private void loadMarkers() {
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                FETCH_URL,
                null,
                response -> {
                    try {
                        Log.d("MAP", "Réponse reçue : " + response.toString());

                        JSONArray points = response.getJSONArray("points");
                        Log.d("MAP", "Nombre de points : " + points.length());

                        if (points.length() == 0) {
                            Log.d("MAP", "Aucun point en base !");
                            return;
                        }

                        double firstLat = 0, firstLng = 0;

                        for (int i = 0; i < points.length(); i++) {
                            JSONObject p = points.getJSONObject(i);

                            double lat    = p.getDouble("tp_lat");
                            double lng    = p.getDouble("tp_lng");
                            String time   = p.getString("tp_time");
                            String device = p.getString("tp_device");

                            Log.d("MAP", "Point " + i + " → lat=" + lat + " lng=" + lng);

                            // Sauvegarde le premier point pour centrer
                            if (i == 0) { firstLat = lat; firstLng = lng; }

                            GeoPoint gp = new GeoPoint(lat, lng);

                            Marker marker = new Marker(osmMap);
                            marker.setPosition(gp);
                            marker.setTitle("🕐 " + time);
                            marker.setSubDescription("📱 " + device);
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            osmMap.getOverlays().add(marker);
                        }

                        // ✅ setCenter + setZoom au lieu de animateTo
                        final double lat = firstLat;
                        final double lng = firstLng;
                        osmMap.post(() -> {
                            osmMap.getController().setZoom(17.0);
                            osmMap.getController().setCenter(new GeoPoint(lat, lng));
                            osmMap.invalidate();
                        });

                    } catch (Exception ex) {
                        Log.e("MAP", "Erreur JSON : " + ex.getMessage());
                        ex.printStackTrace();
                    }
                },
                error -> Log.e("MAP", "Erreur réseau : " + error.toString())
        );

        networkQueue.add(req);
    }

    @Override
    protected void onResume() { super.onResume(); osmMap.onResume(); }

    @Override
    protected void onPause() { super.onPause(); osmMap.onPause(); }
}