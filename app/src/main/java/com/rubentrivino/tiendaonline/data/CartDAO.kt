package com.rubentrivino.tiendaonline.data

import android.content.ContentValues
import android.content.Context

class CartDAO(context: Context) {

    private val dbHelper: DBHelper = DBHelper(context)

    fun add(productId: Int, qty: Int = 1): Long {
        val db = dbHelper.writableDatabase
        return try {
            val sql = """
                INSERT INTO cart_items(product_id, quantity) VALUES(?, ?)
                ON CONFLICT(product_id) DO UPDATE SET quantity = quantity + excluded.quantity
            """.trimIndent()

            val stmt = db.compileStatement(sql)
            stmt.bindLong(1, productId.toLong())
            stmt.bindLong(2, qty.toLong())
            val res: Long = stmt.executeInsert()

            if (res != -1L) {
                res
            } else {
                db.rawQuery(
                    "SELECT id FROM cart_items WHERE product_id = ?",
                    arrayOf(productId.toString())
                ).use { c -> if (c.moveToFirst()) c.getLong(0) else -1L }
            }
        } finally {
            db.close()
        }
    }

    fun add(@Suppress("UNUSED_PARAMETER") userEmail: String, productId: Int, qty: Int = 1): Long {
        return add(productId, qty)
    }

    fun updateQuantity(id: Int, qty: Int): Int {
        val db = dbHelper.writableDatabase
        return try {
            if (qty <= 0) {
                db.delete("cart_items", "id = ?", arrayOf(id.toString()))
            } else {
                val clamped = if (qty < 1) 1 else qty
                val cv = ContentValues().apply { put("quantity", clamped) }
                db.update("cart_items", cv, "id = ?", arrayOf(id.toString()))
            }
        } finally {
            db.close()
        }
    }

    fun remove(id: Int): Int {
        val db = dbHelper.writableDatabase
        return try {
            db.delete("cart_items", "id = ?", arrayOf(id.toString()))
        } finally {
            db.close()
        }
    }

    fun clear(): Int {
        val db = dbHelper.writableDatabase
        return try {
            db.delete("cart_items", null, null)
        } finally {
            db.close()
        }
    }

    fun getAll(): List<CartItem> {
        val out = mutableListOf<CartItem>()
        dbHelper.readableDatabase.use { db ->
            db.rawQuery(
                "SELECT id, product_id, quantity FROM cart_items ORDER BY id DESC",
                null
            ).use { c ->
                while (c.moveToNext()) {
                    out += CartItem(
                        id = c.getInt(0),
                        productId = c.getInt(1),
                        quantity = c.getInt(2)
                    )
                }
            }
        }
        return out
    }

    fun getView(): List<CartView> {
        val out = mutableListOf<CartView>()
        dbHelper.readableDatabase.use { db ->
            val sql = """
                SELECT ci.id,
                       ci.product_id,
                       p.name,
                       p.price,
                       p.image,
                       ci.quantity,
                       (ci.quantity * p.price) AS subtotal
                FROM cart_items ci
                JOIN products p ON p.id = ci.product_id
                ORDER BY ci.id DESC
            """.trimIndent()
            db.rawQuery(sql, null).use { c ->
                while (c.moveToNext()) {
                    out += CartView(
                        id = c.getInt(0),
                        productId = c.getInt(1),
                        name = c.getString(2),
                        price = c.getDouble(3),
                        image = c.getString(4),
                        quantity = c.getInt(5),
                        subtotal = c.getDouble(6)
                    )
                }
            }
        }
        return out
    }

    fun total(): Double {
        dbHelper.readableDatabase.use { db ->
            db.rawQuery(
                """
                SELECT COALESCE(SUM(ci.quantity * p.price), 0)
                FROM cart_items ci
                JOIN products p ON p.id = ci.product_id
                """.trimIndent(),
                null
            ).use { c ->
                return if (c.moveToFirst()) c.getDouble(0) else 0.0
            }
        }
    }
}
