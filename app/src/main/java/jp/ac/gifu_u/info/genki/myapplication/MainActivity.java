package jp.ac.gifu_u.info.genki.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int REQ_LOCATION = 100;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationIfPermitted();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(this);    // 省電力のため停止
        }
    }

    /* ---------- 位置情報取得 ---------- */

    private void startLocationIfPermitted() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_LOCATION);
            return;
        }
        requestLocationUpdates();
    }

    @SuppressLint("MissingPermission")   // ここは上で許可確認済み
    private void requestLocationUpdates() {
        // 1000 ms または 10 m ごとに GPS 更新
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                10,
                this);
    }

    /* ---------- パーミッション結果 ---------- */

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] perms, @NonNull int[] results) {

        super.onRequestPermissionsResult(requestCode, perms, results);
        if (requestCode == REQ_LOCATION &&
                results.length > 0 &&
                results[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates();
        }
    }

    /* ---------- LocationListener 実装 ---------- */

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        Toast.makeText(this,
                String.format(Locale.US, "%.3f  %.3f", lat, lng),
                Toast.LENGTH_SHORT).show();
    }

    // 以下３メソッドは API 33 以前の互換用に空実装
    @Override public void onProviderEnabled(@NonNull String provider) {}
    @Override public void onProviderDisabled(@NonNull String provider) {}
    @Override public void onStatusChanged(String p, int s, Bundle b) {}
}
