package pl.jansmi.stuffbreaker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.jansmi.stuffbreaker.database.dao.BoxDao
import pl.jansmi.stuffbreaker.database.dao.ItemDao
import pl.jansmi.stuffbreaker.database.entity.Box
import pl.jansmi.stuffbreaker.database.entity.Item

@Database(entities = [Box::class, Item::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun boxes(): BoxDao
    abstract fun items(): ItemDao
}