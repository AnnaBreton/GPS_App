package edu.stlawu.locationgps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Observable;

public class LocationHandler extends Observable implements LocationListener {

    private LocationManager lm;
    private MainActivity act;

    public LocationHandler(MainActivity act){
        this.act = act;
        this.lm = (LocationManager) this.act.getSystemService(Context.LOCATION_SERVICE);
        if(this.act.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 5000, 0, this);

            // check for initial GPS location
            // TODO: check for accuracy
            Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(l != null){
                setChanged();
                notifyObservers(l);
                return;
            }

            l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(l != null){
                setChanged();
                notifyObservers(l);
                return;
            }

            l = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if(l != null){
                setChanged();
                notifyObservers(l);
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // tell the observers that the location has changed
        setChanged();
        notifyObservers(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
