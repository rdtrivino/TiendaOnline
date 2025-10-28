package com.rubentrivino.tiendaonline.data

data class CartRow(
    val id: Int,
    val productId: Int,
    val name: String,
    val price: Double,
    var qty: Int,
    val image: String? = null
) {
    val subtotal: Double get() = price * qty
}
