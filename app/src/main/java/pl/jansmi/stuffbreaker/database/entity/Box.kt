package pl.jansmi.stuffbreaker.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["qr_code"], unique = true)])
data class Box(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "desc") var desc: String?,
    @ColumnInfo(name = "qr_code") var qrCode: String?,
    @ColumnInfo(name = "parent") var parentId: Int?,
    @ColumnInfo(name = "image") var imagePath: String?
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}