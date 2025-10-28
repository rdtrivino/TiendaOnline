package com.rubentrivino.tiendaonline

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BeautyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beauty)

        findViewById<Button>(R.id.btnReservar).setOnClickListener {
            Toast.makeText(this, "Solicitud de servicio enviada ✅", Toast.LENGTH_SHORT).show()
        }
    }
}
