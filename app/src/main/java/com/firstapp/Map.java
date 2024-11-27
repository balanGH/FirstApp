package com.firstapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class Map extends AppCompatActivity {

    private MapView mapView;
    private Button btnNavigate;
    private Button btnZoomToCurrentLocation;
    private GeoPoint userLocation; // User location
    private Marker userMarker;
    private Polyline routeLine;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // Define predefined locations with names and coordinates
    private final List<LocationData> predefinedLocations = List.of(
            new LocationData("Go Mechanic, Guindy", new GeoPoint(13.0096, 80.2129)),
            new LocationData("Bala Car Service, Anna Nagar", new GeoPoint(13.0833, 80.2195)),
            new LocationData("ABC Car Care, Saidapet", new GeoPoint(13.0291, 80.2183)),
            new LocationData("Care for car, T.Nagar", new GeoPoint(13.0405, 80.2337)),
            new LocationData("Best service, Tambaram", new GeoPoint(12.9229, 80.1275))
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize the map view
        mapView = findViewById(R.id.mapView);
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Set initial view to Tamil Nadu
        GeoPoint tamilNaduCenter = new GeoPoint(10.8505, 76.2711); // Approximate center of Tamil Nadu
        mapView.getController().setZoom(7); // Adjust zoom level as needed
        mapView.getController().setCenter(tamilNaduCenter);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check and request location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Initialize the navigate button
        btnNavigate = findViewById(R.id.btnNavigate);
        btnNavigate.setOnClickListener(v -> navigateToNearestLocation());

        // Initialize the button to zoom to the current location
        btnZoomToCurrentLocation = findViewById(R.id.btnZoomToCurrentLocation);
        btnZoomToCurrentLocation.setOnClickListener(v -> zoomToCurrentLocation());

        // Initialize the route line
        routeLine = new Polyline(mapView);
        mapView.getOverlays().add(routeLine);

        // Add markers for predefined locations
        addMarkers();
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getCurrentLocation();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission is required to use this app.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Get current location
    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                    addCurrentLocationMarker();
                }
            }
        });
    }

    // Add custom marker for current location
    private void addCurrentLocationMarker() {
        userMarker = new Marker(mapView);
        userMarker.setPosition(userLocation);
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        userMarker.setTitle("Your Location");
        userMarker.setIcon(createCustomMarkerBitmap()); // Set light blue color marker
        mapView.getOverlays().add(userMarker);
        mapView.getController().setCenter(userLocation);
    }

    // Create a custom marker Bitmap with light blue color
    private BitmapDrawable createCustomMarkerBitmap() {
        Drawable drawable = ContextCompat.getDrawable(this, org.osmdroid.library.R.drawable.marker_default);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#3873f2"));
        paint.setAlpha(200);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        return new BitmapDrawable(getResources(), bitmap);
    }

    // Add markers for predefined locations
    private void addMarkers() {
        for (LocationData location : predefinedLocations) {
            Marker marker = new Marker(mapView);
            marker.setPosition(location.position);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(location.name);
            marker.setOnMarkerClickListener((m, mapView) -> {
                if (userLocation == null) {
                    Toast.makeText(this, "User location is not available. Please try again later.", Toast.LENGTH_LONG).show();
                    return true;
                }
                m.showInfoWindow(); // Show the info window with location details
                getRoadRoute(userLocation, location.position);

                // Show the distance to the clicked location
                double distance = userLocation.distanceToAsDouble(location.position); // Use distanceTo method
                marker.setSnippet(location.name + "\n" + "Distance : " + String.format("%.2f km", distance / 1000)); // Convert to km

                // Return the selected location to WelcomePage
                Intent resultIntent = new Intent();
                resultIntent.putExtra("location", location.name);
                setResult(RESULT_OK, resultIntent);
                finish();

                return true;
            });
            mapView.getOverlays().add(marker);
        }
    }

    // Function to navigate to the nearest predefined location based on the user's location
    private void navigateToNearestLocation() {
        if (userLocation == null) {
            Toast.makeText(this, "User location is not available. Please try again later.", Toast.LENGTH_LONG).show();
            return;
        }

        LocationData nearestLocation = null;
        double minDistance = Double.MAX_VALUE;

        // Find the nearest location
        for (LocationData location : predefinedLocations) {
            double distance = userLocation.distanceToAsDouble(location.position); // Use distanceTo method
            if (distance < minDistance) {
                minDistance = distance;
                nearestLocation = location;
            }
        }

        // Navigate to the nearest location
        if (nearestLocation != null) {
            mapView.getController().setZoom(15);  // Zoom level for closer view
            mapView.getController().setCenter(nearestLocation.position);  // Center the map on the nearest location
            getRoadRoute(userLocation, nearestLocation.position); // Get the road route
            Toast.makeText(this, "Nearest Location: " + nearestLocation.name + "\nDistance: " + String.format("%.2f km", minDistance / 1000), Toast.LENGTH_LONG).show(); // Convert to km
        }
    }

    // Function to zoom into the current location
    private void zoomToCurrentLocation() {
        if (userLocation == null) {
            Toast.makeText(this, "User location is not available. Please try again later.", Toast.LENGTH_LONG).show();
            return;
        }

        mapView.getController().setZoom(15);  // Zoom level for closer view
        mapView.getController().setCenter(userLocation);  // Center the map on the current location
    }

    // Get road route between two points using Google Directions API or OSRM
    private void getRoadRoute(GeoPoint start, GeoPoint end) {
        // Make a network request to get the route data (Google Directions API or OSRM)
        // Parse the response and create a list of GeoPoints for the polyline

        // Example for Google Directions API (you need to replace 'YOUR_API_KEY' with your actual API key):
        // String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + start.getLatitude() + "," + start.getLongitude() + "&destination=" + end.getLatitude() + "," + end.getLongitude() + "&key=YOUR_API_KEY";
        // Make the network request and parse the response...

        // For demo purposes, we assume a simple straight line (in reality, you would parse the actual route data)
        List<GeoPoint> points = new ArrayList<>();
        points.add(start);
        points.add(end);
        routeLine.setPoints(points);
        routeLine.setColor(Color.RED);
        routeLine.setWidth(5);
        mapView.invalidate(); // Refresh the map view
    }

    // Inner class to hold location data
    private static class LocationData {
        String name;
        GeoPoint position;

        LocationData(String name, GeoPoint position) {
            this.name = name;
            this.position = position;
        }
    }
}