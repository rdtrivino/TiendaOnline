package com.rubentrivino.tiendaonline.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rubentrivino.tiendaonline.ProductDetailActivity
import com.rubentrivino.tiendaonline.R
import com.rubentrivino.tiendaonline.data.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val items: MutableList<Product>,
    private val isAdmin: Boolean,
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit,
    private val onAddCart: (Product) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val money = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    private var useFallback = false
    private val TYPE_NORMAL = 1
    private val TYPE_FALLBACK = 2

    init { setHasStableIds(true) }
    override fun getItemId(position: Int) = items[position].id.toLong()
    override fun getItemViewType(position: Int) = if (useFallback) TYPE_FALLBACK else TYPE_NORMAL

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.txtName)
        val desc: TextView = v.findViewById(R.id.txtDesc)
        val price: TextView = v.findViewById(R.id.txtPrice)
        val img: ImageView? = v.findViewById(R.id.imgProduct)
        val btnEdit: ImageButton? = v.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton? = v.findViewById(R.id.btnDelete)
        val btnAddCart: ImageButton? = v.findViewById(R.id.btnAddCart)
        val btnFav: ImageButton? = v.findViewById(R.id.btnFav)
        val rating: android.widget.RatingBar? = v.findViewById(R.id.rating)
    }

    inner class FallbackVH(val tv: TextView) : RecyclerView.ViewHolder(tv)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        try {
            if (viewType == TYPE_NORMAL) {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_product, parent, false)
                VH(v)
            } else {
                val tv = TextView(parent.context).apply {
                    setPadding(24, 24, 24, 24)
                    textSize = 16f
                }
                FallbackVH(tv)
            }
        } catch (_: Throwable) {
            useFallback = true
            val tv = TextView(parent.context).apply {
                setPadding(24, 24, 24, 24)
                textSize = 16f
            }
            FallbackVH(tv)
        }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val p = items[position]
        if (holder is VH) {
            holder.name.text = p.name
            holder.desc.text = p.description.orEmpty()
            holder.price.text = money.format(p.price)

            holder.img?.let { iv ->
                val ctx = iv.context
                val resId = if (!p.image.isNullOrBlank())
                    ctx.resources.getIdentifier(p.image, "drawable", ctx.packageName)
                else 0

                when {
                    resId != 0 -> iv.setImageResource(resId)
                    !p.image.isNullOrBlank() -> iv.setImageURI(Uri.parse(p.image))
                    else -> iv.setImageResource(R.mipmap.ic_launcher_round)
                }
            }

            if (isAdmin) {
                holder.btnEdit?.visibility = View.VISIBLE
                holder.btnDelete?.visibility = View.VISIBLE
            } else {
                holder.btnEdit?.visibility = View.GONE
                holder.btnDelete?.visibility = View.GONE
            }

            holder.btnEdit?.setOnClickListener { onEdit(p) }
            holder.btnDelete?.setOnClickListener { onDelete(p) }
            holder.btnAddCart?.setOnClickListener { onAddCart(p) }

            holder.itemView.setOnClickListener {
                val ctx = holder.itemView.context
                ctx.startActivity(
                    Intent(ctx, ProductDetailActivity::class.java)
                        .putExtra("id", p.id)
                )
            }

            holder.rating?.let { it.rating = p.rating.toFloat() }

            holder.btnFav?.let { btn ->
                try {
                    btn.setImageResource(
                        if (p.favorite == 1) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
                    )
                } catch (_: Exception) { }
            }
        } else if (holder is FallbackVH) {
            holder.tv.text = "${p.name} — ${money.format(p.price)}"
        }
    }

    fun setItems(newItems: List<Product>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(o: Int, n: Int) = items[o].id == newItems[n].id
            override fun areContentsTheSame(o: Int, n: Int) = items[o] == newItems[n]
        })
        items.clear()
        items.addAll(newItems)
        diff.dispatchUpdatesTo(this)
    }
}
