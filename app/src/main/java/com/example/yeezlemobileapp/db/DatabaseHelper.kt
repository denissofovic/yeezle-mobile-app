package com.example.yeezlemobileapp.db

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.content.Context
import com.example.yeezlemobileapp.BuildConfig

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = BuildConfig.DATABASE_NAME
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "acces_token"
        const val COLUMN_TOKEN = "token"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableStatement = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_TOKEN VARCHAR(255) PRIMARY KEY
            )
        """
        db.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertAccessToken(token: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TOKEN, token)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAccessToken(): String {
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, arrayOf(COLUMN_TOKEN), null, null, null, null, null)
        var token = ""
        with(cursor) {
            while (moveToNext()) {
                token = getString(getColumnIndexOrThrow(COLUMN_TOKEN))
            }
        }
        cursor.close()
        return token
    }
}
