package pl.jansmi.stuffbreaker.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["qr_code"], unique = true)])
data class Box(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "qr_code") val qrCode: String?,
    @ColumnInfo(name = "parent") val parentId: Int?
)