package pl.jansmi.stuffbreaker.database.dao

import androidx.room.*
import pl.jansmi.stuffbreaker.database.entity.Box

@Dao
interface BoxDao {

    @Query("SELECT * FROM Box WHERE id = :boxId")
    fun findBoxById(boxId: Int): Box?

    @Query("SELECT * FROM Box WHERE qr_code = :qrCode")
    fun findBoxByQrCode(qrCode: String): Box?

    @Query("SELECT * FROM Box WHERE parent = :parentId")
    fun findAllBoxesByParentId(parentId: Int): List<Box>

    @Insert
    fun insert(box: Box)

    @Update
    fun update(box: Box)

    @Delete
    fun delete(box: Box)

}