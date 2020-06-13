package pl.jansmi.stuffbreaker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.jansmi.stuffbreaker.*
import pl.jansmi.stuffbreaker.database.AppDatabase
import pl.jansmi.stuffbreaker.database.entity.Box
import pl.jansmi.stuffbreaker.database.entity.Item


class ItemsAdapter(
    val context: Context,
    val box: Box,
    val switchContent: (box: Box) -> Unit,
    val renderItems: Boolean
): RecyclerView.Adapter<ItemHolder>() {

    private var boxes: List<Box> = ArrayList()
    private var items: List<Item> = ArrayList()

    init {
        val database = AppDatabase.getInstance(context)
        boxes = database.boxes().findAllBoxesByParentId(box.id)
        if (renderItems)
            items = database.items().findAllItemsByBoxId(box.id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.listitem, parent, false)
        return ItemHolder(view, switchContent, renderItems)
    }

    override fun getItemCount(): Int {
        return boxes.size + items.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if (position < boxes.size) {
            val box: Box = boxes[position]
            holder.bindBox(box)
        } else {
            val item: Item = items[position - boxes.size]
            holder.bindItem(item)
        }
    }

}