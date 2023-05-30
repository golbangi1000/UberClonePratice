package com.example.uberclone.ui.ui.home

import android.annotation.SuppressLint
import android.content.res.Resources
import android.location.LocationRequest
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.uberclone.R
import com.example.uberclone.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var mMap: GoogleMap

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallBack : LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return root
    }

    @SuppressLint("MissingPermission")
    private fun init(){
        val locationRequest =  com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
        ).build()
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                val newPos = LatLng(locationResult?.lastLocation?.latitude!!,locationResult.lastLocation?.longitude!!)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos,10f))

            }
        }, Looper.myLooper())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        try {
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.uber_maps_style))
             if(!success){
                 Log.d("Google Map", "error")
             }
        } catch (e : Resources.NotFoundException){
            e.printStackTrace()
        }
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}