package io.taptalk.TapTalk.ViewModel

import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import io.taptalk.TapTalk.Model.TAPLocationItem

class TapMapPickerViewModel : ViewModel() {
    var longitude: Double = 0.0
    var latitude: Double = 0.0
    var count: Int = 0
    var isFirstTriggered = true
    var isSearch: Boolean = true
    var isSameKeyword: Boolean = false
    var isCurrentLocationInitialized: Boolean = false
    var fetchingCurrentLocationCount = 0
    var currentLongitude: Double = 0.0
    var currentLatitude: Double = 0.0
    var currentAddress = ""
    var postalCode = ""
    var locationManager: LocationManager? = null
    var centerOfMap: LatLng? = null
    var googleMap: GoogleMap? = null
    var geoCoder: Geocoder? = null
    var addresses = mutableListOf<Address>()
    var locationList = mutableListOf<TAPLocationItem>()
    var timer: CountDownTimer? = null
}
