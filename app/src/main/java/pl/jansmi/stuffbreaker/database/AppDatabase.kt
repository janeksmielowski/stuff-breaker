package pl.jansmi.stuffbreaker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pl.jansmi.stuffbreaker.database.dao.BoxDao
import pl.jansmi.stuffbreaker.database.dao.ItemDao
import pl.jansmi.stuffbreaker.database.entity.Box
import pl.jansmi.stuffbreaker.database.entity.Item

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
            return db!!;
        }

        fun buildDatabaseInstance(context: Context): AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .allowMainThreadQueries()
                .build();
        }
    }



}