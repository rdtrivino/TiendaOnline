package com.rubentrivino.tiendaonline

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rubentrivino.tiendaonline.adapter.CartAdapter
import com.rubentrivino.tiendaonline.data.CartDAO
import com.rubentrivino.tiendaonline.data.CartItem
import com.rubentrivino.tiendaonline.data.CartRow
import com.rubentrivino.tiendaonline.data.Product
import com.rubentrivino.tiendaonline.data.ProductDAO
import java.text.NumberFormat
import java.util.Locale

class OrdersActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var txtTotal: TextView
    private lateinit var btnClear: Button
    private lateinit var btnCheckout: Button

    private lateinit var cartDao: CartDAO
    private lateinit var productDao: ProductDAO
    private lateinit var adapter: CartAdapter

    private val money = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        recycler = findViewById(R.id.recyclerCart)
        txtTotal = findViewById(R.id.txtTotal)
        btnClear = findViewById(R.id.btnClear)
        btnCheckout = findViewById(R.id.btnCheckout)

        recycler.layoutManager = LinearLayoutManager(this)

        cartDao = CartDAO(this)
        productDao = ProductDAO(this)

        adapter = CartAdapter(
            items = mutableListOf(),
            onInc = { row: CartRow ->
                cartDao.updateQuantity(row.id, row.qty + 1)
                load()
            },
            onDec = { row: CartRow ->
                val newQty = if (row.qty - 1 < 1) 1 else row.qty - 1
                cartDao.updateQuantity(row.id, newQty)
                load()
            },
            onRemove = { row: CartRow ->
                cartDao.remove(row.id)
                load()
            }
        )
        recycler.adapter = adapter

        btnClear.setOnClickListener {
            cartDao.clear()
            load()
        }
        btnCheckout.setOnClickListener {
            Toast.makeText(this, "¡Simulación de pago! (pendiente)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        val productList: List<Product> = productDao.getAll()
        val productById: Map<Int, Product> = productList.associateBy { it.id }
        val cartItems: List<CartItem> = cartDao.getAll()

        val rows = mutableListOf<CartRow>()
        for (ci in cartItems) {
            val prod = productById[ci.productId] ?: continue
            rows.add(
                CartRow(
                    id = ci.id,
                    productId = prod.id,
                    name = prod.name,
                    price = prod.price,
                    qty = ci.quantity,
                    image = prod.image
                )
            )
        }

        adapter.setRows(rows)

        var total = 0.0
        for (r in rows) total += r.subtotal
        txtTotal.text = "Total: ${money.format(total)}"
    }
}
