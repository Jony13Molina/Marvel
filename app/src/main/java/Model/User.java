package Model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by jonny on 4/10/2018.
 */

public class User {
    //global variables for helper class
    public String userID;
    public String name;
    public Date currLogin;
    public String userEmail;

    //arraylist that will contain the mock locations
    public ArrayList<LatLng>mockLocations;

    //constructors
    public User(String myId, String name, Date currLogin, String email) {
        this.userID = myId;
        this.name = name;
        this.currLogin = currLogin;
        this.userEmail = email;

    }

    public User(String userid) {
        this.userID = userid;

    }

    public User() {

    }

    //setters
    public void setUserID(String id) {
        this.userID = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;

    }


    //getters
    public String getUserID() {
        return this.userID;
    }

    public String getName() {
        return this.name;
    }

    public String getUserEmail() {
        return this.userEmail;
    }


}
