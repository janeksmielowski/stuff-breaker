package pl.jansmi.stuffbreaker.database.dao

import androidx.room.*
import pl.jansmi.stuffbreaker.database.entity.Box

@Dao
interface BoxDao {

    @Query("SELECT * FROM Box WHERE id = :boxId")
    suspend fun findBoxById(boxId: Int): Box

    @Query("SELECT * FROM Box WHERE parent = :parentId")
    suspend fun findAllBoxesByParentId(parentId: Int): List<Box>

    @Insert
    fun insert(box: Box)

    @Update
    fun update(box: Box)

    @Delete
    fun delete(box: Box)

}