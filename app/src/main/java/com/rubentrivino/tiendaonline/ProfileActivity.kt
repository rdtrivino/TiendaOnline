package com.rubentrivino.tiendaonline

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class ProfileActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // --- Preferencias para obtener el usuario actual ---
        preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val email = preferences.getString("current_user", "Usuario")

        // --- Referencias UI ---
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val cardShop = findViewById<CardView>(R.id.cardShop)
        val cardOrders = findViewById<CardView>(R.id.cardOrders)
        val cardSettings = findViewById<CardView>(R.id.cardSettings)

        // --- Texto de bienvenida ---
        tvWelcome.text = "¡Bienvenido, $email!"

        // --- Menú de navegación ---
        cardShop.setOnClickListener {
            Toast.makeText(this, "🛒 Ir de compras (en construcción)", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, ShopActivity::class.java))
        }

        cardOrders.setOnClickListener {
            Toast.makeText(this, "📦 Mis pedidos (en construcción)", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, OrdersActivity::class.java))
        }

        cardSettings.setOnClickListener {
            Toast.makeText(this, "⚙️ Configuración (en construcción)", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, SettingsActivity::class.java))
        }

        // --- Botón para cerrar sesión ---
        btnLogout.setOnClickListener {
            val editor = preferences.edit()
            editor.remove("current_user")
            editor.apply()

            Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
