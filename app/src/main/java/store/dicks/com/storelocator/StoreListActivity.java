package store.dicks.com.storelocator;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import store.dicks.com.storelocator.adapter.StoreListAdapter;
import store.dicks.com.storelocator.model.Location;
import store.dicks.com.storelocator.model.Venue;
import store.dicks.com.storelocator.model.VenueDTO;
import store.dicks.com.storelocator.network.ControllerViewListeners;
import store.dicks.com.storelocator.network.NetworkResponseObject;
import store.dicks.com.storelocator.network.StoreLocatorServiceController;

public class StoreListActivity extends AppCompatActivity implements ControllerViewListeners, LocationListener, StoreListAdapter.onClickStoreVenue {

    private static final int LOCATION_REQUEST_CODE = 100;
    private android.location.Location currentDeviceLocation;
    public static final String STORE_KEY = "serializable_store";

    private TextView locationRequestTextView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        mLayout = findViewById(R.id.main_layout);
        locationRequestTextView = (TextView) findViewById(R.id.location_request_text);
        progressBar = (ProgressBar) findViewById(R.id.loading_view);
        recyclerView = (RecyclerView) findViewById(R.id.store_list);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        locationRequestTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StoreListActivity.this, "Permission for request", Toast.LENGTH_LONG).show();
                checkLocationPermission();
            }
        });

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION
                    , android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        int locationAccessPermissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (locationAccessPermissionResult != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);

        } else {
            locationRequestTextView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            final LocationManager deviceLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<String> providerList = deviceLocationManager.getProviders(true);
            for (String provider : providerList) {
                deviceLocationManager.requestLocationUpdates(provider, 0, 0, this);
                currentDeviceLocation = deviceLocationManager.getLastKnownLocation(provider);
                if (currentDeviceLocation != null) {
                    Log.i("Activity ", currentDeviceLocation.getLatitude() + " " + currentDeviceLocation.getLongitude());
                    new StoreLocatorServiceController(this).retrieveVenue();
                    break;
                }
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
                    getCurrentLocation();
                }
            }
        } else {
            // locationRequestTextView.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onSuccess(NetworkResponseObject networkResponse) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        VenueDTO venueDTO = (VenueDTO) networkResponse.getResponseDataObject();
        List<Venue> venueList = venueDTO.getVenues();
        for (Venue venue : venueList) {
            if (venue.getLocation() != null || venue.getVerified()) {
                venue.setDistance(distance(venue.getLocation().getLatitude(), venue.getLocation().getLongitude(),
                        currentDeviceLocation.getLatitude(), currentDeviceLocation.getLongitude()));
            } else {
                venueList.remove(venue);
            }
        }
        Collections.sort(venueList,new VenueChainedComparator(
                new RatingComparator()
                ,new DistanceComparator()));
        recyclerView.setAdapter(new StoreListAdapter(venueList, this));
    }

    public double distance(double lat1, double lon1, double lat2,
                           double lon2) {
//        final int R = 6371;
//        Double latDistance = convertToRad(lat2 - lat1);
//        Double lonDistance = convertToRad(lon2 - lon1);
//        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
//                Math.cos(convertToRad(lat1)) * Math.cos(convertToRad(lat2)) *
//                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        Double distance = R * c;
//
//        System.out.println("The distance between two lat and long is:" + distance);
//
//        return distance;


        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
//        if (unit == "K") {
//            dist = dist * 1.609344;
//        } else if (unit == "N") {
//            dist = dist * 0.8684;
//        }
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private static Double convertToRad(Double value) {
        return value * Math.PI / 180;
    }

    private void showDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Store Locator");
        dialog.setMessage("Please provide the location access to retrieve nearest locations");
        dialog.setPositiveButton("oK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                ActivityCompat.requestPermissions(StoreListActivity.this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, LOCATION_REQUEST_CODE);
            }
        });

        dialog.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }


    @Override
    public void onError() {

    }

    @Override
    public void onOffline() {

    }

    @Override
    public void onLocationChanged(android.location.Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void venueRowInformation(Venue venue) {
        Toast.makeText(this,venue.getRating()+"",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(StoreListActivity.this,StoreDetailsActivity.class);
        intent.putExtra(STORE_KEY,venue);
        startActivity(intent);
    }

    public class RatingComparator implements Comparator<Venue> {

        @Override
        public int compare(Venue venue1, Venue venue2) {
            return (int) (venue1.getRating() - venue2.getRating());
        }
    }

    public class DistanceComparator implements Comparator<Venue> {

        @Override
        public int compare(Venue venue1, Venue venue2) {
            return (int) (venue1.getDistance() - venue2.getDistance());
        }
    }

    public class VenueChainedComparator implements Comparator<Venue> {

        private List<Comparator<Venue>> listComparators;

        public VenueChainedComparator(Comparator<Venue>... listComparators) {
            this.listComparators = Arrays.asList(listComparators);
        }

        @Override
        public int compare(Venue venue, Venue t1) {

            for (Comparator<Venue> comparator : listComparators) {
                int result = comparator.compare(venue, t1);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }
    }
}
