package com.iscoding.mapsxml

import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.iscoding.mapsxml.databinding.FragmentMapsBinding
import java.io.IOException

class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        Toast.makeText(requireContext(), "MAP READY", Toast.LENGTH_LONG).show()

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
        binding.icGps.setOnClickListener {
            getDeviceLocation()
        }

        binding.icMagnify.setOnClickListener {
            geoLocate(binding.inputSearch)

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

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
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
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(  editText.windowToken, 0)
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
}