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

class RegisterActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar SharedPreferences
        preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegisterUser = findViewById<Button>(R.id.btnRegisterUser)

        btnRegisterUser.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Obtener la lista actual de usuarios
            val userListJson = preferences.getString("users_list", "[]")
            val userArray = JSONArray(userListJson)

            // Verificar si el usuario ya existe
            for (i in 0 until userArray.length()) {
                val userObj = userArray.getJSONObject(i)
                if (userObj.getString("email") == email) {
                    Toast.makeText(this, "Este usuario ya está registrado", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Crear un nuevo objeto usuario
            val newUser = JSONObject()
            newUser.put("email", email)
            newUser.put("password", password)

            // Agregarlo a la lista existente
            userArray.put(newUser)

            // Guardar la lista actualizada en SharedPreferences
            val editor = preferences.edit()
            editor.putString("users_list", userArray.toString())
            editor.apply()

            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()

            // Redirigir al login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
