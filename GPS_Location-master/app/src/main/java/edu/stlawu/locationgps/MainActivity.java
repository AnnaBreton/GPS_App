package edu.stlawu.locationgps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

// TODO: clear old values on a new start?

public class MainActivity  extends AppCompatActivity implements Observer {

    // TODO: use fragments to retain info on the flip?
    private int totalDistance;
    private double speed;
    Location currentLocation;
    Location prevLocation;

    Button updateButton;
    Button startButton;
    Button stopButton;

    TextView total;
    TextView speedText;


    // private Observable location;
    private LocationHandler handler = null;
    private final static int PERMISSION_REQUEST_CODE = 987;

    private boolean permissions_granted;
    private final static String LOGTAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(handler == null) {
            handler = new LocationHandler(this);
            this.handler.addObserver(this);
        }

        // check permissions
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        updateButton =  findViewById(R.id.new_position);
        startButton =  findViewById(R.id.start);
        stopButton =  findViewById(R.id.stop);
        total = findViewById(R.id.total);
        speedText = findViewById(R.id.speed);


        // can't update without starting
        updateButton.setEnabled(false);
        stopButton.setEnabled(false);


        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // check if update position button is checked
                updatePosition();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateButton.setEnabled(true);
                stopButton.setEnabled(true);
                startButton.setEnabled(false);
                prevLocation = currentLocation;
                totalDistance = 0;

                // TODO: start a timer that tracks overall velocity here
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateButton.setEnabled(false);
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
                prevLocation = currentLocation;
            }
        });
    }

    public boolean isPermissions_granted() {
        return permissions_granted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST_CODE){
            // only asked for fine location
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                this.permissions_granted = true;
                Log.i(LOGTAG, "Fine location permission granted.");
            }else{
                this.permissions_granted = false;
                Log.i(LOGTAG, "Fine location permission not granted.");
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof LocationHandler){
            currentLocation = (Location) arg;

            // update the current speed here in meters/sec
            speedText.setText(String.format("%d", ((Location) arg).getSpeed()));
        }
    }

    public void updatePosition() {
        // TODO: cuts the decimal with the cast (fix)
        double distance = findDistance(prevLocation, currentLocation);
        int dist = (int) distance;
        addDistanceView(dist);
        updateTotal(dist);

        // save location as the prev
        prevLocation = currentLocation;

        // TODO: speed between points here
    }

    // returns distance in meters
    public double findDistance(Location location1, Location location2){
        return location1.distanceTo(location2);
    }

    // https://developer.android.com/reference/android/location/Location
    // https://stackoverflow.com/questions/3204852/android-add-a-textview-to-linear-layout-programmatically
    // adds a textview with the latest distance to the scrollview
    private void addDistanceView(int distance){
        View linearLayout =  findViewById(R.id.scroll_layout);

        TextView newDistance = new TextView(this);
        newDistance.setText(String.format("%d", distance));
        newDistance.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        ((LinearLayout) linearLayout).addView(newDistance);
    }

    // TODO: change the textview
    // fix this so the rounding isn't so off?
    private void updateTotal(int distance){
        totalDistance += distance;

        total.setText(String.format("%d", totalDistance));
    }
}
