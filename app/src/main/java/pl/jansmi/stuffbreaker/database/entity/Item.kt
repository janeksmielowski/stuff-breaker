package pl.jansmi.stuffbreaker.database.entity

import android.media.Image
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["qr_code"], unique = true)])
data class Item(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "desc") var desc: String?,
    @ColumnInfo(name = "box_id") var boxId: Int,
    @ColumnInfo(name = "qr_code") var qrCode: String?
    //@ColumnInfo(name = "image") var image: Image
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}