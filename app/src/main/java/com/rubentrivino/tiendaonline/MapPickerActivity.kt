package com.rubentrivino.tiendaonline

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var selectedLatLng: LatLng? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_picker)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val btnConfirmar = findViewById<Button>(R.id.btnConfirmarUbicacion)
        btnConfirmar.setOnClickListener {
            if (selectedLatLng != null) {
                val data = intent
                data.putExtra("lat", selectedLatLng!!.latitude)
                data.putExtra("lng", selectedLatLng!!.longitude)
                setResult(RESULT_OK, data)
                finish()
            } else {
                Toast.makeText(this, "Selecciona una ubicación en el mapa", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Habilitar clics en el mapa para mover marcador
        mMap.setOnMapClickListener { latLng ->
            selectedLatLng = latLng
            marker?.remove()
            marker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Ubicación seleccionada")
            )
        }

        obtenerUbicacionActualYMostrar()
    }

    private fun obtenerUbicacionActualYMostrar() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                300
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val actual = LatLng(location.latitude, location.longitude)
                selectedLatLng = actual
                marker?.remove()
                marker = mMap.addMarker(
                    MarkerOptions()
                        .position(actual)
                        .title("Tu ubicación")
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actual, 16f))
            } else {
                // Centro por defecto (ej: Bogotá)
                val bogota = LatLng(4.6486, -74.0875)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bogota, 12f))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 300) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionActualYMostrar()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
