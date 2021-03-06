package pl.jansmi.stuffbreaker.database

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pl.jansmi.stuffbreaker.database.dao.BoxDao
import pl.jansmi.stuffbreaker.database.dao.ItemDao
import pl.jansmi.stuffbreaker.database.entity.Box
import pl.jansmi.stuffbreaker.database.entity.Item
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

@Database(entities = [Box::class, Item::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun boxes(): BoxDao
    abstract fun items(): ItemDao

    companion object {
        private val DB_NAME = "database.db";
        private var db: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (db == null)
                db = buildDatabaseInstance(context)
            return db!!
        }

        private fun buildDatabaseInstance(context: Context): AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java, "$DB_NAME")
                .allowMainThreadQueries()
                .build()
        }

        private fun copy(from: String, to: String) {
            val inputStream = File(from).inputStream()
            val outputStream = FileOutputStream(to)
            inputStream.use {input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        }

        fun importDatabase(context: Context) {
            try {
                copy(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path + "/$DB_NAME", context.getDatabasePath("$DB_NAME").path)
                copy(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path + "/$DB_NAME-shm", context.getDatabasePath("$DB_NAME-shm").path)
                copy(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path + "/$DB_NAME-wal", context.getDatabasePath("$DB_NAME-wal").path)
                Toast.makeText(context, "Database import success! Please restart the app to update records.", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Database import failed. Please check if you have put all the necessary files under /Documents path.", Toast.LENGTH_LONG).show()
            }
        }

        fun exportDatabase(context: Context) {
            try {
                copy(context.getDatabasePath("$DB_NAME").path, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path + "/$DB_NAME")
                copy(context.getDatabasePath("$DB_NAME-shm").path, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path + "/$DB_NAME-shm")
                copy(context.getDatabasePath("$DB_NAME-wal").path, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path + "/$DB_NAME-wal")
                Toast.makeText(context, "Database export success! You will find necessary files under /Documents directory.", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Database export failed. Please check if you have /Documents directory in you home path.", Toast.LENGTH_LONG).show()
            }
        }
    }



}