package com.example.med_it_yourself;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAmbulanceMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private Button mLogout, mRequest, mSettings, mHistory;

    private LatLng pickupLocation;

    private Marker mDriverMarker;

    private int radius = 1;
    private Boolean AmbulanceFound = false;
    private String AmbulanceFoundID;

    private String destination, requestService;
    private LatLng destinationLatLng;
    private LinearLayout mDriverInfo;
    private ImageView mDriverProfileImage;
    private TextView mDriverName, mDriverPhone, mDriverCar;
    private RadioGroup mRadioGroup;

    private Boolean requestBol = false;

    private Marker pickupMarker;
    GeoQuery geoQuery;
    private SupportMapFragment mapFragment;
    final int LOCATION_REQUEST_CODE = 1;

    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;

    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ambulance_map);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(UserAmbulanceMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }else{
            mapFragment.getMapAsync(this);
        }

        Places.initialize(getApplicationContext(), "AIzaSyCrLg8mc7mNAlBoBChK8J9_taPN_5Gt1Lw");
        PlacesClient placesClient = Places.createClient(this);

        destinationLatLng = new LatLng(0.0,0.0);
        mDriverInfo = (LinearLayout) findViewById(R.id.AmbulanceDriverInfo);
        mDriverProfileImage = (ImageView) findViewById(R.id.AmbulanceDriverProfileImage);
        mDriverName = (TextView) findViewById(R.id.AmbulanceDriverName);
        mDriverPhone = (TextView) findViewById(R.id.AmbulanceDriverPhone);
        mDriverCar = (TextView) findViewById(R.id.AmbulanceDriverCar);


        mRadioGroup = (RadioGroup) findViewById(R.id.UserRadioGroup);
        mRadioGroup.check(R.id.ambulance_r);
        mSettings = (Button) findViewById(R.id.user_settings);
        mHistory = (Button) findViewById(R.id.user_history);


        mLogout = (Button) findViewById(R.id.user_logout);
        mRequest = (Button) findViewById(R.id.request);

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserAmbulanceMapActivity.this, AuthenticationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (requestBol)
                {
                    endRide();
                }
                else{
                    int selectId = mRadioGroup.getCheckedRadioButtonId();
                    final RadioButton radioButton = (RadioButton) findViewById(selectId);
                    if (radioButton.getText() == null){
                        return;
                    }
                    requestService = radioButton.getText().toString();
                    requestBol = true;
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserRequest");

                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                    pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here"));

                    mRequest.setText("Getting Your Ambulance Driver....");

                    getClosestDriver();
                }
            }
        });

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserAmbulanceMapActivity.this, UserSettingsActivity.class);
                startActivity(intent);
                return;
            }
        });

        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserAmbulanceMapActivity.this, HistoryActivity.class);
                intent.putExtra("UserOrDriver", "Customers");
                startActivity(intent);
                return;
            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCrLg8mc7mNAlBoBChK8J9_taPN_5Gt1Lw");
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                destination = place.getName().toString();
                destinationLatLng = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Toast.makeText(UserAmbulanceMapActivity.this,"Inside User onError()",Toast.LENGTH_SHORT);
            }
        });

    }

    private void getClosestDriver(){
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("AmbulanceAvailable");

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!AmbulanceFound && requestBol){
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Providers").child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (AmbulanceFound){
                                    return;
                                }

                                if(driverMap.get("Service").equals(requestService)){
                                    AmbulanceFound = true;
                                    AmbulanceFoundID = dataSnapshot.getKey();

                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Providers").child(AmbulanceFoundID).child("UserRequest");
                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("UserRideId", customerId);
                                    map.put("Destination", destination);
                                    map.put("DestinationLat", destinationLatLng.latitude);
                                    map.put("DestinationLng", destinationLatLng.longitude);
                                    driverRef.updateChildren(map);
                                    getDriverLocation();
                                    getDriverInfo();
                                    getHasRideEnded();
                                    mRequest.setText("Looking for Ambulance Driver Location....");
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!AmbulanceFound)
                {
                    radius++;
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void getDriverLocation(){
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("AmbulanceWorking").child(AmbulanceFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat,locationLng);
                    if(mDriverMarker != null){
                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (distance<100){
                        mRequest.setText("Driver's Here");
                    }else{
                        mRequest.setText("Driver Found: " + String.valueOf(distance));
                    }
                    
                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Your Ambulance Driver"));
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void getDriverInfo(){
        mDriverInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Providers").child(AmbulanceFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Name")!=null){
                        mDriverName.setText(map.get("Name").toString());
                    }
                    if(map.get("Phone")!=null){
                        mDriverPhone.setText(map.get("Phone").toString());
                    }
                    if(map.get("Car")!=null){
                        mDriverCar.setText(map.get("Car").toString());
                    }
                    if(map.get("ProfileImageUrl")!=null){
                        Glide.with(getApplication()).load(map.get("ProfileImageUrl").toString()).into(mDriverProfileImage);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getHasRideEnded(){
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Providers").child(AmbulanceFoundID).child("UserRequest").child("UserRideId");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                }else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void endRide(){
        requestBol = false;
        geoQuery.removeAllListeners();
        driverLocationRef.removeEventListener(driverLocationRefListener);
        driveHasEndedRef.removeEventListener(driveHasEndedRefListener);
        if (AmbulanceFoundID != null)
        {
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Providers").child(AmbulanceFoundID).child("UserRequest");
            driverRef.removeValue();
            AmbulanceFoundID = null;
        }
        AmbulanceFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (mDriverMarker != null){
            mDriverMarker.remove();
        }
        mRequest.setText("Request Ambulance");
        mDriverInfo.setVisibility(View.GONE);
        mDriverName.setText("");
        mDriverPhone.setText("");
        mDriverCar.setText("Destination: --");
        mDriverProfileImage.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(getApplicationContext()!=null){
            mLastLocation = location;

            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mapFragment.getMapAsync(this);


                } else {
                    Toast.makeText(getApplicationContext(), "Please Provide The Permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
}
