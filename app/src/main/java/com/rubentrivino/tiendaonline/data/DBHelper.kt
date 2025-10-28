package com.rubentrivino.tiendaonline.data

import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "tienda.db", null, 10) {

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
        db.execSQL("PRAGMA foreign_keys=ON")
    }

    override fun onCreate(db: SQLiteDatabase) {
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

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_products_name ON products(name)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS cart_items(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                product_id INTEGER NOT NULL,
                quantity INTEGER NOT NULL DEFAULT 1,
                FOREIGN KEY(product_id) REFERENCES products(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_cart_unique_product ON cart_items(product_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_cart_product ON cart_items(product_id)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS orders(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                created_at INTEGER NOT NULL,
                total REAL NOT NULL,
                status TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_orders_created ON orders(created_at)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS order_items(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                order_id INTEGER NOT NULL,
                product_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                qty INTEGER NOT NULL,
                FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE,
                FOREIGN KEY(product_id) REFERENCES products(id) ON DELETE RESTRICT
            )
            """.trimIndent()
        )

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_order_items_product ON order_items(product_id)")

        seedBeauty(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try { db.execSQL("ALTER TABLE products ADD COLUMN favorite INTEGER DEFAULT 0") } catch (_: Exception) {}
        try { db.execSQL("ALTER TABLE products ADD COLUMN rating REAL DEFAULT 4.5") } catch (_: Exception) {}

        if (oldVersion < 9) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS orders(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    created_at INTEGER NOT NULL,
                    total REAL NOT NULL,
                    status TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS order_items(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    order_id INTEGER NOT NULL,
                    product_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    price REAL NOT NULL,
                    qty INTEGER NOT NULL,
                    FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE,
                    FOREIGN KEY(product_id) REFERENCES products(id) ON DELETE RESTRICT
                )
                """.trimIndent()
            )
        }

        if (oldVersion < 10) {
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_products_name ON products(name)")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_cart_unique_product ON cart_items(product_id)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_cart_product ON cart_items(product_id)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_orders_created ON orders(created_at)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_order_items_product ON order_items(product_id)")
        }

        seedBeauty(db)
    }

    private fun seedBeauty(db: SQLiteDatabase) {
        val rows = DatabaseUtils.queryNumEntries(db, "products")
        if (rows > 0) return

        db.execSQL(
            """
            INSERT INTO products(name, description, price, stock, image, favorite, rating) VALUES
            ('Esmalte Gel UV','Secado rápido, 15 ml, color nude',12900,50,'img_esmalte',0,4.5),
            ('Labial Mate','Alta duración, tono rosado',19900,40,'img_labial',0,4.2),
            ('Base Líquida','Cobertura media, piel mixta 30 ml',35900,30,'img_base',0,4.1),
            ('Crema Hidratante Facial','Con ácido hialurónico 50 ml',28900,25,'img_crema',1,4.8),
            ('Shampoo Nutritivo','Aceite de argán 400 ml',25900,35,'img_shampoo',0,4.0),
            ('Acondicionador Nutritivo','Aceite de argán 400 ml',25900,35,'img_acond',0,3.9),
            ('Removedor de Esmalte','Sin acetona 120 ml',8900,60,'img_removedor',0,4.3),
            ('Kit Manicure','Cortaúñas, lima, palito naranjo',18900,20,'img_kit',0,4.6),
            ('Brocha Kabuki','Cerdas sintéticas',14900,25,'img_brocha',0,4.4),
            ('Mascarilla Capilar','Reparación profunda 250 ml',32900,18,'img_mascarilla',0,4.7)
            """.trimIndent()
        )
    }
}
