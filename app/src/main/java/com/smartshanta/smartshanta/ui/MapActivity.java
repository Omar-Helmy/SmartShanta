package com.smartshanta.smartshanta.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smartshanta.smartshanta.R;
import com.smartshanta.smartshanta.util.Constants;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String[] loc = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //AIzaSyCzoqkGBUnci38zSD9ikTPHjiopVem6we8
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        loc = getIntent().getStringExtra(Constants.BL_MSG_KEY).split(",");
        mapFragment.getMapAsync(this);

    }

    // Include the OnCreate() method here too, as described above.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        double lat = Double.parseDouble(loc[0].substring(0, 2)) + Double.parseDouble(loc[0].substring(3, 10)) / 60;
        double lng = Double.parseDouble(loc[1].substring(1, 3)) + Double.parseDouble(loc[1].substring(4, 11)) / 60;
        LatLng pos = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(pos)
                .title("Shanta Location"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15.f));
    }

}
