package pl.jansmi.stuffbreaker.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity
data class BoxWithItems(
    @Embedded val box: Box,
    @Relation(
        parentColumn = "id",
        entityColumn = "box_id"
    )
    val items: List<Item>
)