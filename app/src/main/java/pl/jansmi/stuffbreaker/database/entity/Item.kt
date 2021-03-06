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
    @ColumnInfo(name = "keywords") var keywords: String?,
    @ColumnInfo(name = "ean_upc") var eanUpc: String?,
    @ColumnInfo(name = "box_id") var boxId: Int,
    @ColumnInfo(name = "qr_code") var qrCode: String?,
    @ColumnInfo(name = "image", typeAffinity = ColumnInfo.BLOB) var image: ByteArray?
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}