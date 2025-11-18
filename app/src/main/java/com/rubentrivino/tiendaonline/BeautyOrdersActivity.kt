package com.rubentrivino.tiendaonline

import android.database.Cursor
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.rubentrivino.tiendaonline.data.DBHelper

data class BeautyOrder(
    val id: Long,
    val titulo: String,
    val fecha: String,
    val hora: String,
    val descripcion: String?,
    val latitud: Double?,
    val longitud: Double?,
    val fotoUri: String?
)

class BeautyOrdersActivity : AppCompatActivity() {

    private val orders = ArrayList<BeautyOrder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beauty_orders)

        val listView = findViewById<ListView>(R.id.listAgendamientos)

        val dbHelper = DBHelper(this)
        val db = dbHelper.readableDatabase

        // Traemos todos los agendamientos
        val cursor: Cursor = db.rawQuery(
            """
            SELECT id, titulo, fecha, hora, descripcion, latitud, longitud, foto_uri
            FROM agendamientos
            ORDER BY created_at DESC
            """.trimIndent(),
            null
        )

        val items = ArrayList<Map<String, String>>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
                val hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"))

                val descIndex = cursor.getColumnIndex("descripcion")
                val latIndex = cursor.getColumnIndex("latitud")
                val lonIndex = cursor.getColumnIndex("longitud")
                val fotoIndex = cursor.getColumnIndex("foto_uri")

                val descripcion =
                    if (descIndex != -1 && !cursor.isNull(descIndex)) cursor.getString(descIndex) else null
                val latitud =
                    if (latIndex != -1 && !cursor.isNull(latIndex)) cursor.getDouble(latIndex) else null
                val longitud =
                    if (lonIndex != -1 && !cursor.isNull(lonIndex)) cursor.getDouble(lonIndex) else null
                val fotoUri =
                    if (fotoIndex != -1 && !cursor.isNull(fotoIndex)) cursor.getString(fotoIndex) else null

                // Guardamos el objeto completo para usarlo al hacer clic
                val order = BeautyOrder(
                    id = id,
                    titulo = titulo,
                    fecha = fecha,
                    hora = hora,
                    descripcion = descripcion,
                    latitud = latitud,
                    longitud = longitud,
                    fotoUri = fotoUri
                )
                orders.add(order)

                // Datos para mostrar en la lista
                val map = HashMap<String, String>()
                map["titulo"] = titulo
                map["detalle"] = "Fecha: $fecha  •  Hora: $hora"
                items.add(map)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        val adapter = SimpleAdapter(
            this,
            items,
            android.R.layout.simple_list_item_2,
            arrayOf("titulo", "detalle"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )

        listView.adapter = adapter

        // Al hacer clic en un agendamiento, abrimos el detalle
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val order = orders[position]
                val intent = android.content.Intent(this, BeautyOrderDetailActivity::class.java).apply {
                    putExtra("id", order.id)
                    putExtra("titulo", order.titulo)
                    putExtra("fecha", order.fecha)
                    putExtra("hora", order.hora)
                    putExtra("Detalles del servicio", order.descripcion)
                    putExtra("latitud", order.latitud)
                    putExtra("longitud", order.longitud)
                    putExtra("fotoUri", order.fotoUri)
                }
                startActivity(intent)
            }
    }
}
