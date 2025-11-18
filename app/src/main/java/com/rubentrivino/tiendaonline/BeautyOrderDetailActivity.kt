package com.rubentrivino.tiendaonline

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BeautyOrderDetailActivity : AppCompatActivity() {

    private var latitud: Double? = null
    private var longitud: Double? = null
    private var fotoUriString: String? = null
    private var titulo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beauty_order_detail)

        val tvTitulo = findViewById<TextView>(R.id.tvTitulo)
        val tvFechaHora = findViewById<TextView>(R.id.tvFechaHora)
        val tvDescripcion = findViewById<TextView>(R.id.tvDescripcion)
        val tvUbicacion = findViewById<TextView>(R.id.tvUbicacionDetalle)
        val imgFoto = findViewById<ImageView>(R.id.imgFotoDetalle)
        val btnAbrirMapa = findViewById<Button>(R.id.btnAbrirMapa)
        val btnAbrirWaze = findViewById<Button>(R.id.btnAbrirWaze)

        // Recibimos datos del intent
        titulo = intent.getStringExtra("titulo") ?: "Servicio"
        val fecha = intent.getStringExtra("fecha") ?: ""
        val hora = intent.getStringExtra("hora") ?: ""
        val descripcion = intent.getStringExtra("Detalles del servicio") ?: "Sin Detalles del servicio"
        latitud = intent.getDoubleExtra("latitud", Double.NaN)
        longitud = intent.getDoubleExtra("longitud", Double.NaN)
        fotoUriString = intent.getStringExtra("fotoUri")

        tvTitulo.text = titulo
        tvFechaHora.text = "Fecha: $fecha  •  Hora: $hora"
        tvDescripcion.text = descripcion

        // Ubicación
        if (latitud != null && !latitud!!.isNaN() && longitud != null && !longitud!!.isNaN()) {
            tvUbicacion.text = "Lat: $latitud\nLon: $longitud"
        } else {
            tvUbicacion.text = "Ubicación no disponible"
        }

        // Foto
        if (!fotoUriString.isNullOrEmpty()) {
            val uri = Uri.parse(fotoUriString)
            imgFoto.setImageURI(uri)
        }

        // Abrir en Google Maps
        btnAbrirMapa.setOnClickListener {
            if (latitud != null && longitud != null && !latitud!!.isNaN() && !longitud!!.isNaN()) {
                val uri = Uri.parse("geo:${latitud},${longitud}?q=${latitud},${longitud}(${Uri.encode(titulo)})")
                val mapIntent = Intent(Intent.ACTION_VIEW, uri)

                // Intentamos abrir con Google Maps si está instalado
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    // Si no, abrimos cualquier app de mapas disponible
                    val genericIntent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(genericIntent)
                }
            }
        }

        // Abrir en Waze
        btnAbrirWaze.setOnClickListener {
            if (latitud != null && longitud != null && !latitud!!.isNaN() && !longitud!!.isNaN()) {
                val wazeUri = Uri.parse("https://waze.com/ul?ll=${latitud},${longitud}&navigate=yes")
                val wazeIntent = Intent(Intent.ACTION_VIEW, wazeUri)
                wazeIntent.setPackage("com.waze")

                if (wazeIntent.resolveActivity(packageManager) != null) {
                    startActivity(wazeIntent)
                } else {
                    // Si no hay Waze, probamos abrir el link en navegador
                    val browserIntent = Intent(Intent.ACTION_VIEW, wazeUri)
                    startActivity(browserIntent)
                }
            }
        }
    }
}
