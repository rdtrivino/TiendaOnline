package com.rubentrivino.tiendaonline.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rubentrivino.tiendaonline.R
import com.rubentrivino.tiendaonline.data.CartRow
import com.rubentrivino.tiendaonline.loadAny
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val items: MutableList<CartRow>,
    private val onInc: (CartRow) -> Unit,
    private val onDec: (CartRow) -> Unit,
    private val onRemove: (CartRow) -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    private val money = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgProduct)
        val name: TextView = v.findViewById(R.id.txtName)
        val price: TextView = v.findViewById(R.id.txtPrice)
        val qty: TextView = v.findViewById(R.id.txtQty)
        val btnInc: Button = v.findViewById(R.id.btnInc)
        val btnDec: Button = v.findViewById(R.id.btnDec)
        val btnRemove: Button = v.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val row = items[pos]

        // Carga segura de imagen (soporta: drawable name, content://, file://)
        h.img.loadAny(row.image)

        // Texto del nombre
        h.name.text = row.name

        // Formato de precios
        val priceStr = money.format(row.price)
        val subtotalStr = money.format(row.subtotal)
        h.price.text = "$priceStr  •  Subtotal: $subtotalStr"

        // Cantidad
        h.qty.text = row.qty.toString()

        // Acciones
        h.btnInc.setOnClickListener { onInc(row) }
        h.btnDec.setOnClickListener { onDec(row) }
        h.btnRemove.setOnClickListener { onRemove(row) }
    }

    fun setRows(newRows: List<CartRow>) {
        items.clear()
        items.addAll(newRows)
        notifyDataSetChanged()
    }
}
