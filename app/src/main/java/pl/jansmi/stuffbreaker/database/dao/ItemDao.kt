package pl.jansmi.stuffbreaker.database.dao

import androidx.room.*
import pl.jansmi.stuffbreaker.database.entity.Item

@Dao
interface ItemDao {

    @Query("SELECT * FROM Item WHERE id = :itemId")
    fun findItemById(itemId: Int): Item?

    @Query("SELECT * FROM Item WHERE qr_code = :qrCode")
    fun findItemByQrCode(qrCode: String): Item?

    @Query("SELECT * FROM Item WHERE box_id = :boxId")
    fun findAllItemsByBoxId(boxId: Int): List<Item>

    @Insert
    fun insert(item: Item)

    @Update
    fun update(item: Item)

    @Delete
    fun delete(item: Item)

}