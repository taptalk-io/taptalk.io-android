package io.taptalk.TapTalk.View.Activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.*
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_LOCATION
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Listener.TAPGeneralListener
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager
import io.taptalk.TapTalk.Model.TAPLocationItem
import io.taptalk.TapTalk.View.Adapter.TAPSearchLocationAdapter
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_map.*
import java.util.*

class TAPMapActivity : TAPBaseActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener, View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
        var latLng: LatLng?
        if (0.0 == longitude && 0.0 == latitude && 0.0 == currentLongitude && 0.0 == currentLatitude) {
            //Location of Monumen Nasional,` Indonesia
            longitude = 106.827114
            latitude = -6.175403
            latLng = LatLng(latitude, longitude)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.toFloat()))
        } else if (0.0 == longitude && 0.0 == latitude) {
            latLng = LatLng(currentLatitude, currentLongitude)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.toFloat()))
        } else {
            latLng = LatLng(latitude, longitude)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.toFloat()))
        }

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            googleMap?.isMyLocationEnabled = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        googleMap?.setOnCameraMoveListener(this)
        googleMap?.setOnCameraIdleListener(this)
    }

    override fun onCameraMove() {
        centerOfMap = googleMap?.cameraPosition?.target
        latitude = centerOfMap?.latitude ?: 0.0
        longitude = centerOfMap?.longitude ?: 0.0

        ll_set_location.visibility = View.GONE
        iv_location.setImageResource(R.drawable.tap_ic_location_pumpkin_orange)
        iv_location.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconLocationPickerAddressInactive))

        tv_location.setHint(R.string.tap_searching_for_address)
        tv_location.text = ""

        recycler_view.visibility = View.GONE
        TAPUtils.getInstance().dismissKeyboard(this)
    }

    override fun onCameraIdle() {
        getGeoCoderAddress()

        iv_location.setImageResource(R.drawable.tap_ic_location_pumpkin_orange)
        iv_location.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconLocationPickerAddressActive))

        recycler_view.visibility = View.GONE
        isSearch = !isSameKeyword
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_button_back -> {
                onBackPressed()
            }
            R.id.iv_current_location -> {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(this, PERMISSIONS, PERMISSION_LOCATION)
                } else {
                    latitude = currentLatitude
                    longitude = currentLongitude
                    centerOfMap = LatLng(currentLatitude, currentLongitude)
                    val locations: CameraUpdate = CameraUpdateFactory.newLatLngZoom(centerOfMap, 16.toFloat())
                    googleMap?.animateCamera(locations)
                }
            }
            R.id.ll_set_location -> {
                val intent = Intent()
                intent.putExtra(TAPDefaultConstant.Location.LATITUDE, latitude)
                intent.putExtra(TAPDefaultConstant.Location.LONGITUDE, longitude)
                intent.putExtra(TAPDefaultConstant.Location.LOCATION_NAME, currentAddress)
                intent.putExtra(TAPDefaultConstant.Location.POSTAL_CODE, postalCode)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            R.id.tv_clear -> {
                et_keyword.setText("")
                tv_clear.visibility = View.GONE
                recycler_view.visibility = View.GONE
                locationList.clear()
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        if (!isFinishing) {
            TapTalkDialog.Builder(this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(if (TAPNetworkStateManager.getInstance().hasNetworkConnection(this))
                        getString(R.string.tap_error_message_general) else getString(R.string.tap_no_internet_show_error))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .show()
        }
    }

    override fun onLocationChanged(location: Location?) {
        count = 0
        if (3 >= count) {
            currentLatitude = location?.latitude ?: currentLatitude
            currentLongitude = location?.longitude ?: currentLongitude
            count++
            if (count == 3) {
                locationManager?.removeUpdates(this)
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    private val generalListener = object : TAPGeneralListener<TAPLocationItem>() {
        override fun onClick(position: Int, item: TAPLocationItem?) {
            TAPUtils.getInstance().dismissKeyboard(this@TAPMapActivity)
            if (item?.prediction?.getPrimaryText(StyleSpan(Typeface.NORMAL)).toString().equals(et_keyword.text.toString()))
                isSameKeyword = true
            et_keyword.setText(item?.prediction?.getPrimaryText(StyleSpan(Typeface.NORMAL)).toString())
            val placeID: String = item?.prediction?.placeId ?: "0"
            val placeFields: MutableList<Place.Field> = Arrays.asList(Place.Field.LAT_LNG)
            val request: FetchPlaceRequest = FetchPlaceRequest.builder(placeID, placeFields).build()
            placesClient.fetchPlace(request).addOnSuccessListener(this@TAPMapActivity) { p0 ->
                val place = p0?.place
                latitude = place?.latLng?.latitude ?: 0.0
                longitude = place?.latLng?.longitude ?: 0.0
                centerOfMap = place?.latLng
                val curr: LatLng = LatLng(latitude, longitude)
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(curr, 16.toFloat()))
                getGeoCoderAddress()
                iv_location.setImageResource(R.drawable.tap_ic_location_pumpkin_orange)
                iv_location.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconLocationPickerAddressActive))
                recycler_view.visibility = View.GONE
                if (et_keyword.isFocused)
                    et_keyword.clearFocus()
            }
        }
    }

    private lateinit var placesClient: PlacesClient
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var count: Int = 0
    private var isSearch: Boolean = true
    private var isSameKeyword: Boolean = false
    private var currentLongitude: Double = 0.0
    private var currentLatitude: Double = 0.0
    private var currentAddress = ""
    private var postalCode = ""
    private var locationManager: LocationManager? = null
    private var centerOfMap: LatLng? = null
    private var googleMap: GoogleMap? = null
    private var geoCoder: Geocoder? = null
    private var addresses = mutableListOf<Address>()
    private var locationList = mutableListOf<TAPLocationItem>()
    private var adapter: TAPSearchLocationAdapter? = null

    private var PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    companion object {
        private val JAKARTA: LatLng = LatLng(-6.175403, 106.827114)
        private val WORLD: LatLngBounds = LatLngBounds(LatLng(-90.0, 90.0), LatLng(-180.0, 180.0))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_map)

        latitude = intent.getDoubleExtra(TAPDefaultConstant.Location.LATITUDE, 0.0)
        longitude = intent.getDoubleExtra(TAPDefaultConstant.Location.LONGITUDE, 0.0)
        currentAddress = intent.getStringExtra(TAPDefaultConstant.Location.LOCATION_NAME) ?: ""

        placesClient = Places.createClient(this)
        geoCoder = Geocoder(this, Locale.getDefault())

        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        iv_button_back.setOnClickListener(this)
        iv_current_location.setOnClickListener(this)
        ll_set_location.setOnClickListener(this)
        tv_clear.setOnClickListener(this)

        et_keyword.addTextChangedListener(textWatcher)
        et_keyword.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                rl_search.background = ContextCompat.getDrawable(TapTalk.appContext, R.drawable.tap_bg_location_text_field_active)
                if (!TAPUtils.getInstance().isListEmpty(locationList) && et_keyword.text.isNotEmpty())
                    recycler_view.visibility = View.VISIBLE
            } else {
                rl_search.background = ContextCompat.getDrawable(TapTalk.appContext, R.drawable.tap_bg_location_text_field_inactive)
            }
        }
        et_keyword.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    TAPUtils.getInstance().dismissKeyboard(this@TAPMapActivity)
                    return true
                }
                return false
            }
        })

        adapter = TAPSearchLocationAdapter(locationList, generalListener)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter
        if (TAPUtils.getInstance().hasPermissions(this, PERMISSIONS[0])) {
            getLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != locationManager) {
            locationManager?.removeUpdates(this)
            locationManager = null
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
            when (requestCode) {
                PERMISSION_LOCATION -> {
                    getLocation()
                    try {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return
                        }
                        googleMap?.isMyLocationEnabled = true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager?.allProviders?.contains(LocationManager.GPS_PROVIDER) == true) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0.toLong(), 0.toFloat(), this)
        }

        if (locationManager?.allProviders?.contains(LocationManager.NETWORK_PROVIDER) == true) {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0.toLong(), 0.toFloat(), this)
        }

        val netLocation: Location? = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (null != netLocation) {
            onLocationChanged(netLocation)
        }

        val mobileLocation: Location? = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (null != mobileLocation) {
            onLocationChanged(mobileLocation)
        }
    }

    private fun getGeoCoderAddress() {
        try {
            addresses = geoCoder?.getFromLocation(latitude, longitude, 1) ?: mutableListOf()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (!TAPUtils.getInstance().isListEmpty(addresses)) {
            val address: Address = addresses[0]
            try {
                currentAddress = address.getAddressLine(0)
                postalCode = address.postalCode
                tv_location.text = currentAddress
                ll_set_location.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
                tv_location.text = resources.getText(R.string.tap_location_not_found)
                ll_set_location.visibility = View.GONE
            }
        } else {
            tv_location.text = resources.getText(R.string.tap_location_not_found)
            ll_set_location.visibility = View.GONE
        }
    }

    private val textWatcher = object : TextWatcher {
        var timer: CountDownTimer? = null

        override fun afterTextChanged(s: Editable?) {
            timer = object : CountDownTimer(300, 1000) {
                override fun onFinish() {
                    if (!"".equals(et_keyword.text.toString().trim())) {
                        val token = AutocompleteSessionToken.newInstance()
                        val bounds = RectangularBounds.newInstance(WORLD)
                        val request = FindAutocompletePredictionsRequest.builder()
                                .setLocationBias(bounds)
                                .setCountry("id")
                                .setSessionToken(token)
                                .setQuery(et_keyword.text.toString())
                                .build()
                        placesClient.findAutocompletePredictions(request).addOnSuccessListener { p0 ->
                            if (!TAPUtils.getInstance().isListEmpty(locationList))
                                locationList.clear()

                            p0?.autocompletePredictions?.forEach { prediction ->
                                var item = TAPLocationItem()
                                item.prediction = prediction
                                item.myReturnType = TAPLocationItem.MyReturnType.MIDDLE
                                locationList.add(item)
                            }

                            if (!TAPUtils.getInstance().isListEmpty(locationList) && 1 == locationList.size) {
                                locationList[0].myReturnType = TAPLocationItem.MyReturnType.ONLY_ONE
                                adapter?.items = locationList
                                recycler_view.visibility = if (isSearch) View.VISIBLE else View.GONE
                            } else if (!TAPUtils.getInstance().isListEmpty(locationList)) {
                                locationList[0].myReturnType = TAPLocationItem.MyReturnType.FIRST
                                locationList[locationList.size - 1].myReturnType = TAPLocationItem.MyReturnType.LAST

                                if (5 < locationList.size) {
                                    locationList.subList(0, 5)
                                }
                                adapter?.items = locationList
                                recycler_view.visibility = if (isSearch) View.VISIBLE else View.GONE
                            }
                        }
                    }
                }

                override fun onTick(millisUntilFinished: Long) {

                }

            }.start()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            timer?.cancel()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            isSameKeyword = false
            isSearch = true
            recycler_view.visibility = View.GONE
            if (0 < et_keyword.text.toString().length) {
                tv_clear.visibility = View.VISIBLE
            } else {
                tv_clear.visibility = View.GONE
            }
        }
    }

    override fun onBackPressed() {
        if (et_keyword.isFocused) {
            TAPUtils.getInstance().dismissKeyboard(this)
            et_keyword.clearFocus()
        } else {
            super.onBackPressed()
        }
    }
}
