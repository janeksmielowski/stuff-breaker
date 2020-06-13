package pl.jansmi.stuffbreaker.database.dao

import androidx.room.*
import pl.jansmi.stuffbreaker.database.entity.Box
import pl.jansmi.stuffbreaker.database.entity.Item

@Dao
interface ItemDao {

    @Query("SELECT * FROM Item WHERE id = :itemId")
    fun findItemById(itemId: Int): Item?

    @Query("SELECT * FROM Item WHERE qr_code = :qrCode")
    fun findItemByQrCode(qrCode: String): Item?

    @Query("SELECT * FROM Item WHERE box_id = :boxId")
    fun findAllItemsByBoxId(boxId: Int): List<Item>

    @Query("SELECT * FROM Item WHERE name LIKE '%' || :query || '%'")
    fun findAllItemsMatchingQuery(query: String): List<Item>

    @Query("SELECT * FROM Item")
    fun findAll(): List<Item>

    @Insert
    fun insert(item: Item)

    @Update
    fun update(item: Item)

    @Delete
    fun delete(item: Item)

}