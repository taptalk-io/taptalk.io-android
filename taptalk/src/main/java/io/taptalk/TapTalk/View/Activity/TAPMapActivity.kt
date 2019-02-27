package io.taptalk.TapTalk.View.Activity

import android.Manifest
import android.location.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.taptalk.TapTalk.Model.TAPLocationItem
import io.taptalk.Taptalk.R

class TAPMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener, View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    override fun onMapReady(p0: GoogleMap?) {

    }

    override fun onCameraMove() {

    }

    override fun onCameraIdle() {

    }

    override fun onClick(v: View?) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(location: Location?) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    private var longitude : Double = 0.0
    private var latitude : Double = 0.0
    private var count : Int = 0
    private var isSearch : Boolean = true
    private var isSameKeyword : Boolean = false
    private var currentLongitude : Double = 0.0
    private var currentLatitude : Double = 0.0
    private var currentAddress = ""
    private var postalCode = ""
    private var locationManager : LocationManager? = null
    private var centerOfMap : LatLng? = null
    private var googleMap : GoogleMap? = null
    private var geoCoder : Geocoder? = null
    private var filter : AutocompleteFilter? = null
    protected var googleApiClient : GoogleApiClient? = null
    private var addresses = listOf<Address>()
    private var locationList = listOf<TAPLocationItem>()

    private var PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    companion object {
        private val JAKARTA : LatLng = LatLng(-6.175403, 106.827114)
        private val WORLD : LatLngBounds = LatLngBounds(LatLng(-90.0, 90.0), LatLng(-180.0, 180.0))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_map)
    }
}
