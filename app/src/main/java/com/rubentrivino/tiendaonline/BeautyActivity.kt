package com.rubentrivino.tiendaonline

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rubentrivino.tiendaonline.data.DBHelper
import java.util.Calendar
import java.util.Locale

class BeautyActivity : AppCompatActivity() {

    // UI
    private lateinit var spServicio: Spinner
    private lateinit var etFecha: EditText
    private lateinit var etHora: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var tvUbicacion: TextView
    private lateinit var imgFoto: ImageView

    // Geolocalización
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapLauncher: ActivityResultLauncher<Intent>

    // Cámara
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private var tempUri: Uri? = null
    private var fotoUri: Uri? = null

    // Coordenadas
    private var latitud: Double? = null
    private var longitud: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beauty)

        // Referencias UI
        spServicio = findViewById(R.id.spServicio)
        etFecha = findViewById(R.id.etFecha)
        etHora = findViewById(R.id.etHora)
        etDescripcion = findViewById(R.id.etDescripcion)
        tvUbicacion = findViewById(R.id.tvUbicacion)
        imgFoto = findViewById(R.id.imgFoto)

        val btnUbicacion = findViewById<Button>(R.id.btnUbicacion)
        val btnFoto = findViewById<Button>(R.id.btnFoto)
        val btnReservar = findViewById<Button>(R.id.btnReservar)

        // Inicializar geolocalización
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Llenar Spinner de servicios
        configurarSpinnerServicios()

        // Configurar fecha y hora con diálogos
        etFecha.setOnClickListener { mostrarDatePicker() }
        etHora.setOnClickListener { mostrarTimePicker() }

        // 🔹 Registrar launcher para el mapa (MapPickerActivity)
        mapLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    if (data != null) {
                        latitud = data.getDoubleExtra("lat", 0.0)
                        longitud = data.getDoubleExtra("lng", 0.0)
                        tvUbicacion.text = "Lat: $latitud  |  Lon: $longitud"
                    }
                }
            }

        // Registrar launcher de cámara
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success && tempUri != null) {
                    fotoUri = tempUri
                    imgFoto.setImageURI(fotoUri)
                } else {
                    Toast.makeText(this, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show()
                }
            }

        // Listeners
        btnUbicacion.setOnClickListener {
            // Abrir el mapa para seleccionar la ubicación
            val intent = Intent(this, MapPickerActivity::class.java)
            mapLauncher.launch(intent)
        }

        btnFoto.setOnClickListener { solicitarFoto() }
        btnReservar.setOnClickListener { guardarAgendamiento() }
    }

    // ================================
    // SPINNER SERVICIOS
    // ================================
    private fun configurarSpinnerServicios() {
        val servicios = listOf(
            "Selecciona un servicio",
            "Manicure spa",
            "Pedicure clásico",
            "Maquillaje social",
            "Combo Manicure + Pedicure",
            "Maquillaje para evento",
            "Servicio personalizado"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            servicios
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spServicio.adapter = adapter
    }

    // ================================
    // DATE PICKER
    // ================================
    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, y, m, d ->
                val fecha = String.format(
                    Locale.getDefault(),
                    "%02d/%02d/%04d",
                    d,
                    m + 1,
                    y
                )
                etFecha.setText(fecha)
            },
            year,
            month,
            day
        )
        datePicker.show()
    }

    // ================================
    // TIME PICKER
    // ================================
    private fun mostrarTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            this,
            { _, h, m ->
                val hora = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    h,
                    m
                )
                etHora.setText(hora)
            },
            hour,
            minute,
            true // formato 24h
        )
        timePicker.show()
    }

    // ================================
    // GELOCALIZACIÓN (quedan como helpers, MapPicker usa su propio permiso)
    // ================================
    private fun solicitarUbicacion() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        } else {
            obtenerUbicacion()
        }
    }

    private fun obtenerUbicacion() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitud = location.latitude
                    longitud = location.longitude
                    tvUbicacion.text = "Lat: $latitud  |  Lon: $longitud"
                } else {
                    Toast.makeText(this, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error obteniendo ubicación", Toast.LENGTH_SHORT).show()
            }
    }

    // ================================
    // CÁMARA
    // ================================
    private fun solicitarFoto() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 200)
        } else {
            abrirCamara()
        }
    }

    private fun abrirCamara() {
        val values = ContentValues().apply {
            put(
                MediaStore.Images.Media.TITLE,
                "foto_agenda_${System.currentTimeMillis()}"
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        tempUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )

        tempUri?.let {
            takePictureLauncher.launch(it)
        } ?: run {
            Toast.makeText(this, "No se pudo crear URI para la foto", Toast.LENGTH_SHORT).show()
        }
    }

    // ================================
    // GUARDAR AGENDAMIENTO EN SQLite
    // ================================
    private fun guardarAgendamiento() {
        val servicioSeleccionado = spServicio.selectedItem?.toString() ?: ""
        val fecha = etFecha.text.toString().trim()
        val hora = etHora.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()

        if (servicioSeleccionado == "Selecciona un servicio" || servicioSeleccionado.isBlank()) {
            Toast.makeText(this, "Seleccione un servicio", Toast.LENGTH_SHORT).show()
            return
        }

        if (fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Complete fecha y hora", Toast.LENGTH_SHORT).show()
            return
        }

        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("titulo", servicioSeleccionado)   // usamos el servicio como título
            put("fecha", fecha)
            put("hora", hora)
            put("descripcion", descripcion)
            put("latitud", latitud)
            put("longitud", longitud)
            put("foto_uri", fotoUri?.toString())
            put("created_at", System.currentTimeMillis())
        }

        val id = db.insert("agendamientos", null, values)
        db.close()

        if (id > 0) {
            Toast.makeText(this, "Agendamiento guardado ✔", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "Error guardando agendamiento", Toast.LENGTH_SHORT).show()
        }
    }

    // ================================
    // ABRIR CONFIGURACIÓN DE LA APP
    // ================================
    private fun abrirConfiguracionApp() {
        Toast.makeText(
            this,
            "Debes otorgar los permisos en Configuración de la aplicación.",
            Toast.LENGTH_LONG
        ).show()

        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    // ================================
    // PERMISOS
    // ================================
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {           // Ubicación
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion()
            } else {
                abrirConfiguracionApp()
            }
        }

        if (requestCode == 200) {           // Cámara
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara()
            } else {
                abrirConfiguracionApp()
            }
        }
    }
}
