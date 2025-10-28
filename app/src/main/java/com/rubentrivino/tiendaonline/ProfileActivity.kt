package com.rubentrivino.tiendaonline

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class ProfileActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val emailPref = preferences.getString("current_user", null)
        if (emailPref.isNullOrBlank()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        val email = emailPref

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val cardShop = findViewById<CardView>(R.id.cardShop)
        val cardOrders = findViewById<CardView>(R.id.cardOrders)
        val cardPurchases = findViewById<CardView>(R.id.cardPurchases)
        val cardSettings = findViewById<CardView>(R.id.cardSettings)
        val cardBeauty = findViewById<CardView>(R.id.cardBeauty)

        tvWelcome.text = "¡Bienvenido, $email!"

        val isAdmin = email.contains("admin", ignoreCase = true)
        preferences.edit().putBoolean("isAdmin", isAdmin).apply()
        getSharedPreferences("app", MODE_PRIVATE).edit().putBoolean("isAdmin", isAdmin).apply()

        cardShop.setOnClickListener {
            startActivity(
                Intent(this, ShopActivity::class.java)
                    .putExtra("email", email)
                    .putExtra("isAdmin", isAdmin)
            )
        }
        cardSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        cardBeauty.setOnClickListener {
            startActivity(Intent(this, BeautyActivity::class.java))
        }
        cardPurchases.setOnClickListener {
            startActivity(Intent(this, PedidosActivity::class.java))
        }

        cardOrders.visibility = View.GONE
        cardOrders.setOnClickListener(null)

        btnLogout.setOnClickListener {
            preferences.edit().clear().apply()
            getSharedPreferences("app", MODE_PRIVATE).edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
