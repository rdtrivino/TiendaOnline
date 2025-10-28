package com.rubentrivino.tiendaonline

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.rubentrivino.tiendaonline.data.CartDAO
import com.rubentrivino.tiendaonline.data.Product
import com.rubentrivino.tiendaonline.data.ProductDAO
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var dao: ProductDAO
    private lateinit var cartDao: CartDAO
    private var product: Product? = null

    private val money = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        dao = ProductDAO(this)
        cartDao = CartDAO(this)

        val productId = intent.getIntExtra("id", -1)
        if (productId == -1) {
            Toast.makeText(this, "Producto no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val img = findViewById<ImageView>(R.id.imgDetail)
        val name = findViewById<TextView>(R.id.txtDetailName)
        val desc = findViewById<TextView>(R.id.txtDetailDesc)
        val price = findViewById<TextView>(R.id.txtDetailPrice)
        val ratingBar = findViewById<RatingBar>(R.id.ratingDetail)
        val btnFav = findViewById<ImageButton>(R.id.btnFavDetail)
        val btnAdd = findViewById<Button>(R.id.btnAddToCart)

        val p = dao.getById(productId)
        if (p == null) {
            Toast.makeText(this, "No se encontró el producto", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        product = p

        name.text = p.name
        desc.text = p.description.orEmpty()
        price.text = money.format(p.price)
        ratingBar.rating = p.rating.toFloat()

        val imgStr = p.image
        if (!imgStr.isNullOrBlank()) {
            try {
                if (imgStr.startsWith("content://") || imgStr.startsWith("file://")) {
                    img.scaleType = ImageView.ScaleType.CENTER_CROP
                    img.setImageURI(Uri.parse(imgStr))
                } else {
                    val resId = resources.getIdentifier(imgStr, "drawable", packageName)
                    if (resId != 0) {
                        img.scaleType = ImageView.ScaleType.CENTER_CROP
                        img.setImageResource(resId)
                    } else {
                        img.scaleType = ImageView.ScaleType.CENTER_CROP
                        img.setImageURI(Uri.parse(imgStr))
                    }
                }
            } catch (_: Exception) {}
        }

        fun paintFavIcon(fav: Int) {
            btnFav.setImageResource(
                if (fav == 1) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )
        }
        paintFavIcon(p.favorite)

        btnFav.setOnClickListener {
            val cur = product ?: return@setOnClickListener
            val currentFav = cur.favorite
            val newFav = if (currentFav == 1) 0 else 1
            val rows = dao.updateFavorite(cur.id, newFav == 1)
            if (rows > 0) {
                cur.favorite = newFav
                paintFavIcon(newFav)
            } else {
                Toast.makeText(this, "No se pudo actualizar favorito", Toast.LENGTH_SHORT).show()
            }
        }

        ratingBar.setOnRatingBarChangeListener { _, newRating, fromUser ->
            if (!fromUser) return@setOnRatingBarChangeListener
            val cur = product ?: return@setOnRatingBarChangeListener
            val rows = dao.updateRating(cur.id, newRating.toDouble())
            if (rows > 0) {
                cur.rating = newRating.toDouble()
            } else {
                Toast.makeText(this, "No se pudo actualizar calificación", Toast.LENGTH_SHORT).show()
            }
        }

        btnAdd.setOnClickListener {
            val email = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getString("current_user", "demo@tienda.com") ?: "demo@tienda.com"
            val id = product?.id ?: return@setOnClickListener
            cartDao.add(email, id, 1)
            Toast.makeText(this, "Añadido al carrito", Toast.LENGTH_SHORT).show()
        }
    }
}
