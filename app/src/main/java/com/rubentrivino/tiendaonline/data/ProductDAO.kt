package com.rubentrivino.tiendaonline.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException

class ProductDAO(context: Context) {
    private val dbHelper = DBHelper(context)

    private fun ensureSchema() {
        dbHelper.writableDatabase.use { db ->
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS products(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    price REAL NOT NULL,
                    stock INTEGER DEFAULT 0,
                    image TEXT,
                    favorite INTEGER DEFAULT 0,
                    rating REAL DEFAULT 4.5
                )
                """.trimIndent()
            )
            try { db.execSQL("ALTER TABLE products ADD COLUMN favorite INTEGER DEFAULT 0") } catch (_: Throwable) {}
            try { db.execSQL("ALTER TABLE products ADD COLUMN rating REAL DEFAULT 4.5") } catch (_: Throwable) {}
        }
    }

    fun insert(p: Product): Long {
        ensureSchema()
        return dbHelper.writableDatabase.use { db ->
            val cv = ContentValues().apply {
                put("name", p.name)
                put("description", p.description)
                put("price", p.price)
                put("stock", p.stock)
                put("image", p.image)
                put("favorite", p.favorite)
                put("rating", p.rating)
            }
            db.insert("products", null, cv)
        }
    }

    fun update(p: Product): Int {
        ensureSchema()
        return dbHelper.writableDatabase.use { db ->
            val cv = ContentValues().apply {
                put("name", p.name)
                put("description", p.description)
                put("price", p.price)
                put("stock", p.stock)
                put("image", p.image)
                put("favorite", p.favorite)
                put("rating", p.rating)
            }
            db.update("products", cv, "id=?", arrayOf(p.id.toString()))
        }
    }

    fun delete(id: Int): Int {
        ensureSchema()
        return dbHelper.writableDatabase.use { db ->
            db.delete("products", "id=?", arrayOf(id.toString()))
        }
    }

    fun getAll(): MutableList<Product> {
        ensureSchema()
        val list = mutableListOf<Product>()
        try {
            dbHelper.readableDatabase.use { db ->
                db.rawQuery(
                    "SELECT id,name,description,price,stock,image,favorite,rating FROM products ORDER BY id DESC",
                    null
                ).use { c ->
                    while (c.moveToNext()) list.add(c.toProduct())
                }
            }
        } catch (_: SQLiteException) {
            ensureSchema()
        }
        return list
    }

    fun getById(id: Int): Product? {
        ensureSchema()
        dbHelper.readableDatabase.use { db ->
            db.rawQuery(
                "SELECT id,name,description,price,stock,image,favorite,rating FROM products WHERE id=?",
                arrayOf(id.toString())
            ).use { c ->
                return if (c.moveToFirst()) c.toProduct() else null
            }
        }
    }

    fun getFavorites(): MutableList<Product> {
        ensureSchema()
        val list = mutableListOf<Product>()
        dbHelper.readableDatabase.use { db ->
            db.rawQuery(
                "SELECT id,name,description,price,stock,image,favorite,rating FROM products WHERE favorite=1 ORDER BY id DESC",
                null
            ).use { c ->
                while (c.moveToNext()) list.add(c.toProduct())
            }
        }
        return list
    }

    fun updateFavorite(id: Int, fav: Boolean): Int {
        return updateFavoriteInt(id, if (fav) 1 else 0)
    }

    fun updateFavoriteInt(id: Int, fav: Int): Int {
        ensureSchema()
        return dbHelper.writableDatabase.use { db ->
            val cv = ContentValues().apply { put("favorite", if (fav != 0) 1 else 0) }
            db.update("products", cv, "id=?", arrayOf(id.toString()))
        }
    }

    fun updateRating(id: Int, rating: Double): Int {
        ensureSchema()
        return dbHelper.writableDatabase.use { db ->
            val cv = ContentValues().apply { put("rating", rating) }
            db.update("products", cv, "id=?", arrayOf(id.toString()))
        }
    }

    fun updateImage(id: Int, image: String?): Int {
        ensureSchema()
        return dbHelper.writableDatabase.use { db ->
            val cv = ContentValues().apply { put("image", image) }
            db.update("products", cv, "id=?", arrayOf(id.toString()))
        }
    }

    private fun Cursor.toProduct(): Product =
        Product(
            id = getInt(0),
            name = getString(1),
            description = getString(2),
            price = getDouble(3),
            stock = getInt(4),
            image = getString(5),
            favorite = getInt(6),
            rating = getDouble(7)
        )
}
