package com.iscoding.mapsxml

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.codebyashish.googledirectionapi.AbstractRouting
import com.codebyashish.googledirectionapi.RouteDrawing
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.PolyUtil
import com.iscoding.mapsxml.data.CustomRouteListener
import com.iscoding.mapsxml.data.RetrofitInstance
import com.iscoding.mapsxml.databinding.FragmentMapsBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var marker: Marker
    private lateinit var snippet: String
    private  var styleBoolean: Boolean = false
    private  var first: Boolean = true
    private lateinit var endDistination: LatLng
    private lateinit var startDistination: LatLng
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var autocompleteAdapter: PlaceAutocompleteAdapter
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMapClickListener {latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))

            val rout = RouteDrawing.Builder().context(requireContext())
                .travelMode(AbstractRouting.TravelMode.DRIVING).withListener(CustomRouteListener(map))
                .alternativeRoutes(true).waypoints(startDistination, latLng).build()
            rout.execute()

//            fetchDirections(startDistination,latLng)

        }
        Toast.makeText(requireContext(), "MAP READY", Toast.LENGTH_LONG).show()
        googleMap.setInfoWindowAdapter(CustomInfoWindowAdapter(layoutInflater))
        getDeviceLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMapsBinding.inflate(inflater, container, false)
//        binding.inputSearch.setOnEditorActionListener { v, actionId, event ->
//            Log.d("ISLAM", "Editor action triggered: actionId=$actionId, event=$event")
//            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
//                actionId == EditorInfo.IME_ACTION_DONE ||
//                (event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) ||
//                (event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
//            ) {
//                Log.d("ISLAM", "Triggering geoLocate function")
//                geoLocate(binding.inputSearch)
//                return@setOnEditorActionListener true // Indicate that the event was handled
//            }
//            false
//        }
//
//        binding.inputSearch.setOnKeyListener { v, keyCode, event ->
//            Log.d("ISLAM", "Key action triggered: keyCode=$keyCode, event=$event")
//            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BREAK) {
//                Log.d("ISLAM", "Triggering geoLocate function")
//                geoLocate(binding.inputSearch)
//                return@setOnKeyListener true // Indicate that the event was handled
//            }
//            false
//        }
        Places.initialize(requireContext(), BuildConfig.API_KEY)
        placesClient = Places.createClient(requireContext())

        // Set up the autocomplete adapter
        val locationBias = RectangularBounds.newInstance(
            LatLng(29.948885, 31.206344), // Southwest corner
            LatLng(30.116667, 31.375778)  // Northeast corner
        )

        autocompleteAdapter = PlaceAutocompleteAdapter(requireContext(), locationBias)

        // Find the AutoCompleteTextView in the layout
        val autoCompleteTextView = binding.inputSearch
        autoCompleteTextView.setAdapter(autocompleteAdapter)
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val item = autocompleteAdapter.getItem(position)
                item?.let {
                    val placeId = it.placeId
                    getPlaceAndMoveCamera(placeId, binding.inputSearch)
                }
            }


        binding.icGps.setOnClickListener {
            getDeviceLocation()
        }
        binding.deleteMarkers.setOnClickListener {
            map.clear()
        }

        binding.icMagnify.setOnClickListener {
            geoLocate(binding.inputSearch)

        }
        binding.placePicker.setOnClickListener {
            styleBoolean = !styleBoolean
            if (styleBoolean){
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.map_style_night))

            }else{
                map.setMapStyle(null)

            }


        }
        binding.placeInfo.setOnClickListener {
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }
        }

        return binding.root
    }

    private fun geoLocate(editText: EditText) {
        Log.d("ISLAM", "geoLocate: geolocating")

        val searchString = editText.text.toString()

        val geocoder = Geocoder(requireContext())
        var list: List<Address> = emptyList()
        try {
            list = geocoder.getFromLocationName(searchString, 1)!!
        } catch (e: IOException) {
            Log.e("ISLAM", "geoLocate: IOException: ${e.message}")
        }

        if (list.isNotEmpty()) {
            val address = list[0]
            updateMapWithLocation(address)
            hideKeyboard(editText)
            Log.d("ISLAM", "geoLocate: found a location: $address")
            // Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMapWithLocation(address: Address) {
        val latLng = LatLng(address.latitude, address.longitude)
        map.addMarker(MarkerOptions().position(latLng).title("Found Location"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
// the blue dot for my location
        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {

                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    startDistination = LatLng(location.latitude, location.longitude)
                    map.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Unable to get current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("MapFragment", "Error getting location", e)
            }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun hideKeyboard(editText: EditText) {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getDeviceLocation()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPlaceAndMoveCamera(placeId: String, editText: EditText) {
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS,
            Place.Field.PHONE_NUMBER,
            Place.Field.WEBSITE_URI,
            Place.Field.RATING
        )
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            val latLng = place.latLng
            latLng?.let {
                map.clear()  // Clear previous markers if any

                val snippet =  """
                Address: ${place.address ?: "N/A"}
                Phone Number: ${place.phoneNumber ?: "N/A"}
                Website: ${place.websiteUri ?: "N/A"}
                Price Rating: ${place.rating ?: "N/A"}
            """.trimIndent()
                val options = MarkerOptions().position(it).title(place.name).snippet(snippet)

                marker = map.addMarker(options)!!
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
            }

            hideKeyboard(editText)
        }.addOnFailureListener { exception ->
            Log.e("MapsFragment", "Place not found: ${exception.message}")
        }
    }
    private fun fetchDirections( origin: LatLng, destination: LatLng) {
        val originStr = "${origin.latitude},${origin.longitude}"
        val destinationStr = "${destination.latitude},${destination.longitude}"
        val apiKey = BuildConfig.API// Replace with your actual API key

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getDirections(originStr, destinationStr, apiKey)
                Log.d("DirectionsFragment", "API response: $response")
                Log.d("DirectionsFragment", "API response: $response")
                Log.d("DirectionsFragment", "start ${originStr}  end ${destinationStr}")

                if (response.routes.isNotEmpty()) {

                    val points = response.routes[0].overviewPolyline.points
                    val polyline = PolyUtil.decode(points)
                    map.addPolyline(
                        PolylineOptions()
                            .addAll(polyline)
                            .width(10f)
                            .color(Color.BLUE)
                    )
                } else {
                    Log.e("DirectionsFragment", "No routes found")
                }
            } catch (e: HttpException) {
                Log.e("DirectionsFragment", "HTTP error: ${e.message()}")
            } catch (e: Exception) {
                Log.e("DirectionsFragment", "Error: ${e.message}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getPlaceIdForLocation(latitude: Double, longitude: Double) {
        val latLng = LatLng(latitude, longitude)
        val placesClient = Places.createClient(requireContext())

        val request = FindCurrentPlaceRequest.newInstance(listOf(Place.Field.ID))
        placesClient.findCurrentPlace(request)
            .addOnSuccessListener { response: FindCurrentPlaceResponse ->
                val placeLikelihood = response.placeLikelihoods.firstOrNull()
                val placeId = placeLikelihood?.place?.id
                if (placeId != null) {
                    Log.d("PlaceId", "Place ID for current location: $placeId")
                    // Use the place ID as needed
                } else {
                    Log.e("PlaceId", "Place ID not found for current location")
                }
            }
            .addOnFailureListener { exception: Exception ->
                Log.e("PlaceId", "Failed to get place ID for current location: $exception")
            }
    }
}