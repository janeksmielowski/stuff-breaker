package pl.jansmi.stuffbreaker.database.entity

import android.media.Image
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["qr_code"], unique = true)])
data class Item(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "desc") val desc: String?,
    @ColumnInfo(name = "box_id") val boxId: Int,
    @ColumnInfo(name = "qr_code") val qrCode: String?
    //@ColumnInfo(name = "image") val image: Image
)