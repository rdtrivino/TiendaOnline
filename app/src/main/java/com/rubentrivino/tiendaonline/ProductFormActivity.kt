package com.rubentrivino.tiendaonline

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.rubentrivino.tiendaonline.data.Product
import com.rubentrivino.tiendaonline.data.ProductDAO
import java.io.File
import java.io.FileOutputStream

class ProductFormActivity : AppCompatActivity() {

    private lateinit var dao: ProductDAO
    private var productId: Int? = null
    private var localImagePath: String? = null

    private lateinit var etName: EditText
    private lateinit var etDesc: EditText
    private lateinit var etPrice: EditText
    private lateinit var etStock: EditText

    private val openImageDoc = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = res.data ?: return@registerForActivityResult
        val uri = data.data ?: return@registerForActivityResult

        val takeFlags = data.flags and
                (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        try { contentResolver.takePersistableUriPermission(uri, takeFlags) } catch (_: Exception) {}

        localImagePath = copyImageToInternalStorage(uri)
        if (localImagePath != null) {
            Toast.makeText(this, "Imagen guardada en la app", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No se pudo copiar la imagen", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_form)

        dao = ProductDAO(this)

        etName = findViewById(R.id.etName)
        etDesc = findViewById(R.id.etDesc)
        etPrice = findViewById(R.id.etPrice)
        etStock = findViewById(R.id.etStock)
        val btnPick: Button = findViewById(R.id.btnPickImage)
        val btnSave: Button = findViewById(R.id.btnSave)

        productId = intent.getIntExtra("id", -1).takeIf { it != -1 }
        productId?.let { id ->
            dao.getById(id)?.let { p ->
                etName.setText(p.name)
                etDesc.setText(p.description ?: "")
                etPrice.setText(p.price.toString())
                etStock.setText(p.stock.toString())
                localImagePath = p.image
            }
        }

        btnPick.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            }
            openImageDoc.launch(intent)
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val price = etPrice.text.toString().toDoubleOrNull()
            val stock = etStock.text.toString().toIntOrNull()

            if (name.isBlank() || price == null || stock == null) {
                Toast.makeText(this, "Completa nombre, precio y stock", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prod = Product(
                id = productId ?: 0,
                name = name,
                description = etDesc.text.toString().ifBlank { null },
                price = price,
                stock = stock,
                image = localImagePath
            )

            try {
                if (productId == null) {
                    dao.insert(prod)
                    Toast.makeText(this, "Producto creado", Toast.LENGTH_SHORT).show()
                } else {
                    dao.update(prod)
                    Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
                }
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Error guardando: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun copyImageToInternalStorage(sourceUri: Uri): String? {
        return try {
            val dir = File(filesDir, "images").apply { if (!exists()) mkdirs() }
            val dest = File(dir, "img_${System.currentTimeMillis()}.jpg")

            contentResolver.openInputStream(sourceUri).use { input ->
                FileOutputStream(dest).use { output ->
                    if (input == null) return null
                    input.copyTo(output)
                }
            }
            dest.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
