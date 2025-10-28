package com.rubentrivino.tiendaonline

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rubentrivino.tiendaonline.adapter.ProductAdapter
import com.rubentrivino.tiendaonline.data.CartDAO
import com.rubentrivino.tiendaonline.data.Product
import com.rubentrivino.tiendaonline.data.ProductDAO
import java.text.NumberFormat
import java.util.Locale

class ShopActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var fabAdd: FloatingActionButton

    private lateinit var etSearch: EditText
    private lateinit var btnClearSearch: ImageButton
    private var searchRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var cartBar: View
    private lateinit var txtCartTotal: TextView
    private lateinit var btnGoCart: Button
    private val money = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    private lateinit var dao: ProductDAO
    private lateinit var cartDao: CartDAO

    private var isAdmin = false
    private var currentUser = "demo@tienda.com"

    private var adapter: ProductAdapter? = null
    private val allItems = mutableListOf<Product>()
    private val shownItems = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        val userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        currentUser = userPrefs.getString("current_user", null)
            ?: intent.getStringExtra("email") ?: "demo@tienda.com"
        userPrefs.edit().putString("current_user", currentUser).apply()
        isAdmin = intent.getBooleanExtra(
            "isAdmin",
            userPrefs.getBoolean(
                "isAdmin",
                getSharedPreferences("app", MODE_PRIVATE).getBoolean("isAdmin", false)
            )
        )

        recycler = findViewById(R.id.recyclerProducts)
        fabAdd = findViewById(R.id.fabAdd)

        etSearch = findViewById(R.id.etSearch)
        btnClearSearch = findViewById(R.id.btnClearSearch)

        cartBar = findViewById(R.id.cartBar)
        txtCartTotal = findViewById(R.id.txtCartTotal)
        btnGoCart = findViewById(R.id.btnGoCart)

        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.setHasFixedSize(true)

        fabAdd.visibility = if (isAdmin) View.VISIBLE else View.GONE
        fabAdd.setOnClickListener {
            startActivity(Intent(this, ProductFormActivity::class.java))
        }

        btnGoCart.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }

        dao = ProductDAO(this)
        cartDao = CartDAO(this)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { handler.removeCallbacks(it) }
                searchRunnable = Runnable { applyFilter(s?.toString().orEmpty()) }
                handler.postDelayed(searchRunnable!!, 200)
            }
        })
        btnClearSearch.setOnClickListener { etSearch.setText("") }
    }

    override fun onResume() {
        super.onResume()
        load()
        refreshCartBar()
    }

    private fun load() {
        try {
            val products: List<Product> = dao.getAll()
            allItems.clear()
            allItems.addAll(products)

            if (adapter == null) {
                shownItems.clear()
                shownItems.addAll(allItems)

                adapter = ProductAdapter(
                    items = shownItems,
                    isAdmin = isAdmin,
                    onEdit = { p ->
                        startActivity(
                            Intent(this, ProductFormActivity::class.java).putExtra("id", p.id)
                        )
                    },
                    onDelete = { p ->
                        dao.delete(p.id)
                        Toast.makeText(this, "Eliminado: ${p.name}", Toast.LENGTH_SHORT).show()
                        load()
                        refreshCartBar()
                    },
                    onAddCart = { p ->
                        cartDao.add(p.id, 1)
                        Toast.makeText(this, "Añadido: ${p.name}", Toast.LENGTH_SHORT).show()
                        refreshCartBar()
                    }
                )
                recycler.adapter = adapter
            } else {
                applyFilter(etSearch.text?.toString().orEmpty())
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cargar: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun applyFilter(queryRaw: String) {
        val q = queryRaw.trim().lowercase()
        val filtered = if (q.isEmpty()) {
            allItems
        } else {
            allItems.filter { p ->
                p.name.lowercase().contains(q) ||
                        (p.description ?: "").lowercase().contains(q)
            }
        }
        shownItems.clear()
        shownItems.addAll(filtered)
        adapter?.notifyDataSetChanged()
    }

    private fun refreshCartBar() {
        val items = cartDao.getAll()
        val count = items.sumOf { it.quantity }
        val total = cartDao.total()

        if (count > 0) {
            cartBar.visibility = View.VISIBLE
            txtCartTotal.text = "Total: ${money.format(total)}"
            btnGoCart.text = "Ver carrito ($count)"
        } else {
            cartBar.visibility = View.GONE
        }
    }
}
