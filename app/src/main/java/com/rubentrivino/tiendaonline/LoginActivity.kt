package com.rubentrivino.tiendaonline

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        seedUsersIfEmpty()

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLoginUser = findViewById<Button>(R.id.btnLoginUser)

        btnLoginUser.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userListJson = preferences.getString("users_list", "[]") ?: "[]"
            val userArray = JSONArray(userListJson)

            var userFound = false
            for (i in 0 until userArray.length()) {
                val userObj = userArray.getJSONObject(i)
                if (userObj.getString("email").equals(email, ignoreCase = true) &&
                    userObj.getString("password") == password) {
                    userFound = true
                    break
                }
            }

            if (userFound) {
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                val isAdmin = email.contains("admin", ignoreCase = true)

                preferences.edit()
                    .putString("current_user", email)
                    .putBoolean("isAdmin", isAdmin)
                    .apply()

                getSharedPreferences("app", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isAdmin", isAdmin)
                    .apply()

                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun seedUsersIfEmpty() {
        val raw = preferences.getString("users_list", "[]") ?: "[]"
        val arr = JSONArray(raw)
        if (arr.length() == 0) {
            val admin = JSONObject().apply {
                put("email", "admin@tienda.com")
                put("password", "123456")
            }
            val demo = JSONObject().apply {
                put("email", "demo@tienda.com")
                put("password", "123456")
            }
            arr.put(admin)
            arr.put(demo)
            preferences.edit().putString("users_list", arr.toString()).apply()
        }
    }
}
