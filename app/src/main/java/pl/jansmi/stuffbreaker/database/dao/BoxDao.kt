package pl.jansmi.stuffbreaker.database.dao

import androidx.room.*
import pl.jansmi.stuffbreaker.database.entity.Box

@Dao
interface BoxDao {

    @Query("SELECT * FROM Box WHERE id = :boxId")
    fun findBoxById(boxId: Int)

    @Insert
    fun insert(box: Box)

    @Update
    fun update(box: Box)

    @Delete
    fun delete(box: Box)

}