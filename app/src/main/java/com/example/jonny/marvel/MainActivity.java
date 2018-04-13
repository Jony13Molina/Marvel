package com.example.jonny.marvel;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import Model.MockLoc;
import Model.User;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, View.OnClickListener {
    //variables for login out
    private FirebaseAuth myAuth;
    private FirebaseAuth.AuthStateListener myAuthListener;
    //variables to get the user name from fire base
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference;
    //variables for the UI
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private TextView userName;
    private View headerView;


    //variables for map
    DatabaseReference database;
    TextView Search;
    RelativeLayout drawerL;
    ImageView icon;
    FloatingActionButton searchButton;

    GoogleMap map1;

    public LatLng [] coordinates;
    List<Address> addressList = null;
    public MockLoc addLoc;
    public LatLng values;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //define our views
        NavigationView myView = findViewById(R.id.nav_view);
        myView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        headerView = myView.getHeaderView(0);
        userName = headerView.findViewById(R.id.nav_header_textView);


        //method to display the name of the user in the nav header
        getUsername();
        //search box
        Search = findViewById(R.id.searchInput);
        drawerL = findViewById(R.id.mapRelativeLayout);
        icon = findViewById(R.id.searchIcon);
        searchButton = findViewById(R.id.searchButton);
        //setting the hamburger icon open close
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                drawerL.setVisibility(View.VISIBLE);

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                drawerL.setVisibility(View.INVISIBLE);

            }
        };


        //makes hamburger icon clickable and add the
        drawerLayout.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //method to log out
        authListner();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapsFragment);
        mapFragment.getMapAsync(this);

        searchButton.setOnClickListener(this);
    }


    public void onClick(View v){
        onMapSearch(v);

    }
    //search method for looking up places/ addresses
    public void onMapSearch(View view) {

        String location = Search.getText().toString();


        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            addNewMarker(latLng);

        }
    }


    //add markers
    public void addNewMarker( LatLng latLng){
        coordinates = new LatLng[addressList.size()];
        //LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());


        List values = new ArrayList<LatLng>(Arrays.asList(coordinates));
        values.add(latLng);
        addLoc = new MockLoc(values, latLng);
        database = FirebaseDatabase.getInstance().getReference();
        String loc = Search.getText().toString();
        database.child("mockloc").child(loc).setValue(addLoc);
        map1.addMarker(new MarkerOptions().position(latLng).title("Marker"));
        map1.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map1 = googleMap;
        // here add marker on a mock location, Germany and then move camera for adding more markers
        LatLng Munich = new LatLng(48.1351, 11.5820);
        map1.addMarker(new MarkerOptions().position(Munich).title("Munich, Germany"));
        map1.moveCamera(CameraUpdateFactory.newLatLng(Munich));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //map1.setMyLocationEnabled(true);
    }

/*------------------------Code manages the navigation drawer--------*/
    //navigation drawer
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newCon){
        super.onConfigurationChanged(newCon);
        toggle.onConfigurationChanged(newCon);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //on back button pressed do nothing

    //setting up the click listner
    @Override
    public void onBackPressed() {
        //do nothing

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.LogOut:
                Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
                myAuth.signOut();
                this.startActivity(new Intent(this, Login.class));

        }
        //closes drawer when item is choosen
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    //method to get the user name from firebase and then to set it on to the textview
    public void getUsername(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName.setText(dataSnapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //listen to authentication changes once the activity
    //starts in the case of sign out
    @Override
    protected void onStart() {
        super.onStart();

        myAuth.addAuthStateListener(myAuthListener);
    }
    public void authListner(){
        myAuth=FirebaseAuth.getInstance();


        myAuthListener =new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null){
                    startActivity(new Intent(MainActivity.this,Login.class));

                }
            }
        };
    }



}
