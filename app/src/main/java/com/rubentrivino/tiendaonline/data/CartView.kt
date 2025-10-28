package com.rubentrivino.tiendaonline.data

data class CartView(
    val id: Int,
    val productId: Int,
    val name: String,
    val price: Double,
    val image: String?,
    val quantity: Int,
    val subtotal: Double
)
