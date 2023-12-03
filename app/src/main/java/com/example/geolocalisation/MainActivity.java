package com.example.geolocalisation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap myMap; //instance d'un objet de type googlemap (myMap) est un variable de type google map
    private SearchView mapSearchView, mapSearchView2;
    private final int FINE_PERMISSION_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Button B1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        B1 = findViewById(R.id.btnGetCurrentLocation);
        mapSearchView = findViewById(R.id.mapSearch);
        mapSearchView2 = findViewById(R.id.mapSearch2);
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(MainActivity.this);
        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync((OnMapReadyCallback) this);

        requestLocationPermission();

        B1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastlocation();

            }
        });

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String nomdepays) {

                Geocoder geocoder=new Geocoder(MainActivity.this);
                List <Address> list=null;
                try {
                    list=geocoder.getFromLocationName(nomdepays,1);
                    Address address=list.get(0);
                    LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
                    myMap.addMarker(new MarkerOptions().position(latLng).title(nomdepays));
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        mapSearchView2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String lattitudelongitude) {

                int longeur=lattitudelongitude.length();
                int x=lattitudelongitude.indexOf(",");
                String lat=lattitudelongitude.substring(0,x);
                String lon=lattitudelongitude.substring(x+1,longeur);
                double latitude=Double.parseDouble(lat);
                double longitude=Double.parseDouble(lon);
                Geocoder geocoder = new Geocoder(MainActivity.this);

                try {
                    List <Address>  list= geocoder.getFromLocation(latitude,longitude,1);
                    Address adresse=list.get(0);
                    LatLng latLng = new LatLng(adresse.getLatitude(), adresse.getLongitude());
                    myMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(adresse)));
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }




    public void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        //Cette condition vérifie si la permission d'accéder à la localisation  n'a pas encore été accordée
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);

        }
        else
        {
            getLastlocation();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastlocation();
            } else {
                Toast.makeText(this, "la permission de position n'est pas autorisé", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getLastlocation()
    {
        @SuppressLint("MissingPermission") Task <Location> location=fusedLocationProviderClient.getLastLocation();
        location.addOnSuccessListener(new OnSuccessListener <Location> () {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng latlng=new LatLng(location.getLatitude(),location.getLongitude());
                    myMap.addMarker(new MarkerOptions().position(latlng).title("Home"));
                    myMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                }

            }
        });
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        LatLng latLng=new LatLng(33.892166,9.561555);
        myMap.addMarker(new MarkerOptions().position(latLng).title("Tunisie"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }
}