package com.rubentrivino.tiendaonline.data

data class Product(
    var id: Int = 0,
    var name: String = "",
    var description: String? = null,
    var price: Double = 0.0,
    var stock: Int = 0,
    var image: String? = null,
    var favorite: Int = 0,
    var rating: Double = 4.5
) {
    override fun toString(): String {
        return "$name — $${"%.0f".format(price)}"
    }
}
