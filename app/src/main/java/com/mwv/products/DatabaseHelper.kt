package com.mwv.products
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.database.Cursor

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "ProductDB.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "pricelist"
        private const val COLUMN_ID = "id"
        private const val COLUMN_PRODUCT = "product"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_ADDRESS = "address"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_PRODUCT TEXT, $COLUMN_DESCRIPTION TEXT, $COLUMN_PRICE REAL, $COLUMN_ADDRESS TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(product: String, description: String, price: Float, address: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PRODUCT, product)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_PRICE, price)
            put(COLUMN_ADDRESS, address)
        }
        val rowId = db.insert(TABLE_NAME, null, values)
        db.close()
        return rowId
    }

    fun getAllData(): Cursor {
        val db = this.readableDatabase
        return db.query(TABLE_NAME, null, null, null, null, null, null)
    }

    // Function to delete data by ID
    fun deleteDataById(id: Int): Int {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(id.toString())
        val rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
        return rowsDeleted // Returns the number of rows deleted
    }

    // Function to delete data by Name
    fun deleteDataByProduct(product: String): Int {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_PRODUCT = ?"
        val whereArgs = arrayOf(product)
        val rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
        return rowsDeleted // Returns the number of rows deleted
    }

    fun getDataByProduct(product: String): Cursor {
        val db = this.readableDatabase
        val selection = "$COLUMN_PRODUCT = ?"
        val selectionArgs = arrayOf(product)
        return db.query(
            TABLE_NAME,
            null, // All columns
            selection,
            selectionArgs,
            null, // groupBy
            null, // having
            null  // orderBy
        )
    }

    fun getDataByPortionProduct(product: String): Cursor {
        val db = this.readableDatabase
        val selection = "$COLUMN_PRODUCT LIKE ?"
        val selectionArgs = arrayOf("%$product%") // Add '%' wildcards around the portion
        val orderBy = "$COLUMN_PRICE ASC" // Order by price in ascending order
        return db.query(
            TABLE_NAME,
            null, // All columns
            selection,
            selectionArgs,
            null, // groupBy
            null, // having
            orderBy  // orderBy Specify the order
        )
    }

    fun getDataById(id: Int): Cursor {
        val db = this.readableDatabase
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        return db.query(
            TABLE_NAME,
            null, // All columns
            selection,
            selectionArgs,
            null, // groupBy
            null, // having
            null  // orderBy
        )
    }

    override fun close() {
        super.close()
        writableDatabase.close()
        readableDatabase.close()
    }
}