package com.rubentrivino.tiendaonline

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class LoginActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

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

            val userListJson = preferences.getString("users_list", "[]")
            val userArray = JSONArray(userListJson)

            var userFound = false

            for (i in 0 until userArray.length()) {
                val userObj = userArray.getJSONObject(i)
                if (userObj.getString("email") == email && userObj.getString("password") == password) {
                    userFound = true
                    break
                }
            }

            if (userFound) {
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                // Guardar el usuario actual para futuras sesiones (opcional)
                val editor = preferences.edit()
                editor.putString("current_user", email)
                editor.apply()

                // Ir al perfil o pantalla principal
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
