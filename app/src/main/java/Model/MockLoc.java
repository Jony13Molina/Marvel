package Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by jonny on 4/11/2018.
 */

public class MockLoc {
    public LatLng mockLocs;
    public DatabaseReference database;
    public List<LatLng> loc;

    public MockLoc( List<LatLng> locations, LatLng location){
        this.loc = locations;
        this.mockLocs = location;
    }
    public MockLoc(){

    }
    public LatLng getMockLocs(){
        return this.mockLocs;
    }
    public List<LatLng> getVal() {

        return this.loc;
    }




}
