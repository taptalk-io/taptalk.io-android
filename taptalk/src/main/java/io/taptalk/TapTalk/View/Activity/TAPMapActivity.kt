package io.taptalk.TapTalk.View.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_LOCATION
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_LOCATION
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Listener.TAPGeneralListener
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager
import io.taptalk.TapTalk.Model.TAPLocationItem
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPSearchLocationAdapter
import io.taptalk.TapTalk.ViewModel.TapMapPickerViewModel
import io.taptalk.TapTalk.databinding.TapActivityMapBinding
import java.util.Arrays
import java.util.Locale

class TAPMapActivity : TAPBaseActivity(),
    OnMapReadyCallback,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraIdleListener,
    View.OnClickListener,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    private lateinit var vb: TapActivityMapBinding
    private lateinit var placesClient: PlacesClient

    private val vm : TapMapPickerViewModel by lazy {
        ViewModelProvider(this)[TapMapPickerViewModel::class.java]
    }

    private var adapter: TAPSearchLocationAdapter? = null

    private val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    companion object {
        private val JAKARTA: LatLng = LatLng(-6.175403, 106.827114)
        private val WORLD: LatLngBounds = LatLngBounds(LatLng(-90.0, 90.0), LatLng(-180.0, 180.0))

        fun start(context: Activity, instanceKey: String) {
            val intent = Intent(context, TAPMapActivity::class.java)
            intent.putExtra(INSTANCE_KEY, instanceKey)
            context.startActivityForResult(intent, PICK_LOCATION)
            context.overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = TapActivityMapBinding.inflate(layoutInflater)
        setContentView(vb.root)

        vm.isCurrentLocationInitialized = false
        vm.latitude = intent.getDoubleExtra(TAPDefaultConstant.Location.LATITUDE, 0.0)
        vm.longitude = intent.getDoubleExtra(TAPDefaultConstant.Location.LONGITUDE, 0.0)
        vm.currentAddress = intent.getStringExtra(TAPDefaultConstant.Location.LOCATION_NAME) ?: ""

        try {
            placesClient = Places.createClient(this)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        vm.geoCoder = Geocoder(this, Locale.getDefault())

        vb.ivButtonBack.setOnClickListener(this)
        vb.ivCurrentLocation.setOnClickListener(this)
        vb.tvClear.setOnClickListener(this)

        if (Places.isInitialized()) {
            vb.etKeyword.removeTextChangedListener(textWatcher)
            vb.etKeyword.addTextChangedListener(textWatcher)
            vb.etKeyword.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    vb.rlSearch.background = ContextCompat.getDrawable(TapTalk.appContext, R.drawable.tap_bg_location_text_field_active)
                    if (!TAPUtils.isListEmpty(vm.locationList) && vb.etKeyword.text.isNotEmpty())
                        vb.cvSearchResult.visibility = View.VISIBLE
                }
                else {
                    vb.rlSearch.background = ContextCompat.getDrawable(TapTalk.appContext, R.drawable.tap_bg_location_text_field_inactive)
                }
            }
            vb.etKeyword.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        TAPUtils.dismissKeyboard(this@TAPMapActivity)
                        return true
                    }
                    return false
                }
            })
        }
        else {
            vb.rlSearch.visibility = View.GONE
        }

        adapter = TAPSearchLocationAdapter(vm.locationList, generalListener)
        vb.recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        vb.recyclerView.adapter = adapter

        vb.ivCurrentLocation.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_recenter_location_button_ripple)
    }

    override fun onResume() {
        super.onResume()

        if (!vm.isCurrentLocationInitialized) {
            val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }

        if (TAPUtils.hasPermissions(this, PERMISSIONS[0])) {
            getLocation()
        }
    }

    override fun onStop() {
        super.onStop()
        vm.timer?.cancel()
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        if (null != vm.locationManager) {
            vm.locationManager?.removeUpdates(this)
            vm.locationManager = null
        }
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return false
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
            when (requestCode) {
                PERMISSION_LOCATION -> {
                    getLocation()
                    try {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        ) {
                            return
                        }
                        vm.googleMap?.isMyLocationEnabled = true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        vm.googleMap = map
        val latLng: LatLng?
        if (0.0 == vm.longitude && 0.0 == vm.latitude && 0.0 == vm.currentLongitude && 0.0 == vm.currentLatitude) {
            // Location of Monumen Nasional Indonesia
            vm.longitude = 106.827114
            vm.latitude = -6.175403
            latLng = LatLng(vm.latitude, vm.longitude)
            vm.googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.toFloat()))
        } else if (0.0 == vm.longitude && 0.0 == vm.latitude) {
            latLng = LatLng(vm.currentLatitude, vm.currentLongitude)
            vm.googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.toFloat()))
        }
        else {
            latLng = LatLng(vm.latitude, vm.longitude)
            vm.googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.toFloat()))
            vm.isCurrentLocationInitialized = true
        }

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            vm.googleMap?.isMyLocationEnabled = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        vm.googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        vm.googleMap?.setOnCameraMoveListener(this)
        vm.googleMap?.setOnCameraIdleListener(this)
    }

    override fun onCameraMove() {
        vm.centerOfMap = vm.googleMap?.cameraPosition?.target
        vm.latitude = vm.centerOfMap?.latitude ?: 0.0
        vm.longitude = vm.centerOfMap?.longitude ?: 0.0
        disableSendButton()
        vb.ivLocation.setImageDrawable(ContextCompat.getDrawable(this@TAPMapActivity, R.drawable.tap_ic_location_orange))
        vb.ivLocation.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconLocationPickerAddressInactive))

        vb.tvLocation.setHint(R.string.tap_searching_for_address)
        vb.tvLocation.text = ""

        vb.cvSearchResult.visibility = View.GONE
        TAPUtils.dismissKeyboard(this)
    }

    override fun onCameraIdle() {
        getGeoCoderAddress()

        vb.ivLocation.setImageDrawable(ContextCompat.getDrawable(this@TAPMapActivity, R.drawable.tap_ic_location_orange))
        vb.ivLocation.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconLocationPickerAddressActive))

        vb.cvSearchResult.visibility = View.GONE
        vm.isSearch = !vm.isSameKeyword
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        if (!isFinishing) {
            TapTalkDialog.Builder(this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(if (TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(this))
                        getString(R.string.tap_error_message_general) else getString(R.string.tap_no_internet_show_error))
                    .setMessage(getString(R.string.tap_error_message_general))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .show()
        }
    }

    override fun onLocationChanged(location: Location) {
        vm.currentLatitude = location.latitude
        vm.currentLongitude = location.longitude
        if (vm.isFirstTriggered) {
            moveToCurrentLocation()
            vm.isFirstTriggered = false;
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_button_back -> {
                onBackPressed()
            }
            R.id.iv_current_location -> {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(this, PERMISSIONS, PERMISSION_LOCATION)
                }
                else {
                    if (vm.fetchingCurrentLocationCount > 1) {
                        vm.fetchingCurrentLocationCount = 1
                    }
                    moveToCurrentLocation()
                }
            }
            R.id.tv_clear -> {
                vb.etKeyword.setText("")
                vb.tvClear.visibility = View.GONE
                vb.cvSearchResult.visibility = View.GONE
                vm.locationList.clear()
            }
        }
    }

    private val generalListener = object : TAPGeneralListener<TAPLocationItem>() {
        override fun onClick(position: Int, item: TAPLocationItem?) {
            TAPUtils.dismissKeyboard(this@TAPMapActivity)
            if (item?.prediction?.getPrimaryText(StyleSpan(Typeface.NORMAL)).toString().equals(vb.etKeyword.text.toString())) {
                vm.isSameKeyword = true
            }
            vb.etKeyword.setText(item?.prediction?.getPrimaryText(StyleSpan(Typeface.NORMAL)).toString())
            val placeID: String = item?.prediction?.placeId ?: "0"
            val placeFields: MutableList<Place.Field> = Arrays.asList(Place.Field.LAT_LNG)
            val request: FetchPlaceRequest = FetchPlaceRequest.builder(placeID, placeFields).build()
            placesClient.fetchPlace(request).addOnSuccessListener(this@TAPMapActivity) { p0 ->
                val place = p0?.place
                vm.latitude = place?.latLng?.latitude ?: 0.0
                vm.longitude = place?.latLng?.longitude ?: 0.0
                vm.centerOfMap = place?.latLng
                val curr: LatLng = LatLng(vm.latitude, vm.longitude)
                vm.googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(curr, 16.toFloat()))
                getGeoCoderAddress()
                vb.ivLocation.setImageDrawable(ContextCompat.getDrawable(this@TAPMapActivity, R.drawable.tap_ic_location_orange))
                vb.ivLocation.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconLocationPickerAddressActive))
                vb.cvSearchResult.visibility = View.GONE
                if (vb.etKeyword.isFocused)
                    vb.etKeyword.clearFocus()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        vm.locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (vm.locationManager?.allProviders?.contains(LocationManager.GPS_PROVIDER) == true) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            vm.locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0.toLong(), 0.toFloat(), this)
        }
        else if (vm.locationManager?.allProviders?.contains(LocationManager.NETWORK_PROVIDER) == true) {
            vm.locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0.toLong(), 0.toFloat(), this)
        }

        val netLocation: Location? = vm.locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (null != netLocation) {
            onLocationChanged(netLocation)
        }

        val mobileLocation: Location? = vm.locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (null != mobileLocation) {
            onLocationChanged(mobileLocation)
        }

        if (netLocation == null && mobileLocation == null && vm.fetchingCurrentLocationCount > 0) {
            moveToCurrentLocation()
        }
    }

    private fun getGeoCoderAddress() {
        try {
            vm.addresses = vm.geoCoder?.getFromLocation(vm.latitude, vm.longitude, 1) ?: mutableListOf()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        if (!TAPUtils.isListEmpty(vm.addresses)) {
            val address: Address = vm.addresses[0]
            try {
                vm.currentAddress = address.getAddressLine(0)
                vm.postalCode = address.postalCode
                vb.tvLocation.text = vm.currentAddress
                enableSendButton()
            }
            catch (e: Exception) {
                e.printStackTrace()
                vb.tvLocation.text = resources.getText(R.string.tap_location_not_found)
                disableSendButton()
            }
        }
        else {
            vb.tvLocation.text = resources.getText(R.string.tap_location_not_found)
            disableSendButton()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            vm.timer = object : CountDownTimer(300, 1000) {
                override fun onFinish() {
                    if (!"".equals(vb.etKeyword.text.toString().trim())) {
                        val token = AutocompleteSessionToken.newInstance()
                        val bounds = RectangularBounds.newInstance(WORLD)
                        val request = FindAutocompletePredictionsRequest.builder()
                                .setLocationBias(bounds)
                                .setCountry("id")
                                .setSessionToken(token)
                                .setQuery(vb.etKeyword.text.toString())
                                .build()
                        try {
                            placesClient.findAutocompletePredictions(request)
                            .addOnSuccessListener { p0 ->
                                if (!TAPUtils.isListEmpty(vm.locationList))
                                    vm.locationList.clear()

                                p0?.autocompletePredictions?.forEach { prediction ->
                                    val item = TAPLocationItem()
                                    item.prediction = prediction
                                    item.myReturnType = TAPLocationItem.MyReturnType.MIDDLE
                                    vm.locationList.add(item)
                                }

                                if (!TAPUtils.isListEmpty(vm.locationList) && 1 == vm.locationList.size) {
                                    vm.locationList[0].myReturnType = TAPLocationItem.MyReturnType.ONLY_ONE
                                    adapter?.items = vm.locationList
                                    vb.cvSearchResult.visibility = if (vm.isSearch) View.VISIBLE else View.GONE
                                }
                                else if (!TAPUtils.isListEmpty(vm.locationList)) {
                                    vm.locationList[0].myReturnType = TAPLocationItem.MyReturnType.FIRST
                                    vm.locationList[vm.locationList.size - 1].myReturnType = TAPLocationItem.MyReturnType.LAST

                                    if (5 < vm.locationList.size) {
                                        vm.locationList.subList(0, 5)
                                    }
                                    adapter?.items = vm.locationList
                                    vb.cvSearchResult.visibility = if (vm.isSearch) View.VISIBLE else View.GONE
                                }
                            }
                            .addOnFailureListener { e ->
                                e.printStackTrace()
                            }
                        }
                        catch (e: UninitializedPropertyAccessException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onTick(millisUntilFinished: Long) {

                }
            }.start()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            vm.timer?.cancel()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            vm.isSameKeyword = false
            vm.isSearch = true
            vb.cvSearchResult.visibility = View.GONE
            if (vb.etKeyword.text.toString().isNotEmpty()) {
                vb.tvClear.visibility = View.VISIBLE
            }
            else {
                vb.tvClear.visibility = View.GONE
            }
        }
    }

    override fun onBackPressed() {
        if (vb.etKeyword.isFocused) {
            TAPUtils.dismissKeyboard(this)
            vb.etKeyword.clearFocus()
        }
        else {
            super.onBackPressed()
        }
    }

    private fun moveToCurrentLocation() {
        if (!vm.locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {
            TapTalkDialog.Builder(this)
                    .setTitle(getString(R.string.tap_location_disabled))
                    .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                    .setMessage(getString(R.string.tap_allow_location_services))
                    .setPrimaryButtonTitle(getString(R.string.tap_go_to_settings))
                    .setPrimaryButtonListener {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                    .setSecondaryButtonListener { }
                    .show()
        }
        else if (vm.currentLatitude != 0.0 || vm.currentLongitude != 0.0) {
            vm.latitude = vm.currentLatitude
            vm.longitude = vm.currentLongitude
            vm.centerOfMap = LatLng(vm.currentLatitude, vm.currentLongitude)
            vm.fetchingCurrentLocationCount = 0
            val locations: CameraUpdate = CameraUpdateFactory.newLatLngZoom(vm.centerOfMap ?: return, 16.toFloat())
            vm.googleMap?.animateCamera(locations)
        }
        else if (vm.fetchingCurrentLocationCount <= 5) {
            if (vm.fetchingCurrentLocationCount == 0) {
                Toast.makeText(this, getString(R.string.tap_fetching_current_location), Toast.LENGTH_SHORT).show()
            }

            vm.fetchingCurrentLocationCount++
            Handler(Looper.getMainLooper()).postDelayed({
                getLocation()
            }, 5000L)
        }
        else {
            vm.fetchingCurrentLocationCount = 0
            Toast.makeText(this, getString(R.string.tap_unable_to_obtain_current_location), Toast.LENGTH_SHORT).show()
        }
    }

    private fun disableSendButton() {
        vb.llButtonSendLocation.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_inactive)
        vb.llButtonSendLocation.setOnClickListener(null)
    }

    private fun enableSendButton() {
        vb.llButtonSendLocation.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_active_ripple)
        vb.llButtonSendLocation.setOnClickListener { sendLocation() }
    }

    private fun sendLocation() {
        val intent = Intent()
        intent.putExtra(TAPDefaultConstant.Location.LATITUDE, vm.latitude)
        intent.putExtra(TAPDefaultConstant.Location.LONGITUDE, vm.longitude)
        intent.putExtra(TAPDefaultConstant.Location.LOCATION_NAME, vm.currentAddress)
        intent.putExtra(TAPDefaultConstant.Location.POSTAL_CODE, vm.postalCode)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
