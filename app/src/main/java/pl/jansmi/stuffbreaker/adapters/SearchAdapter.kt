package pl.jansmi.stuffbreaker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.jansmi.stuffbreaker.R
import pl.jansmi.stuffbreaker.database.AppDatabase
import pl.jansmi.stuffbreaker.database.entity.Box
import pl.jansmi.stuffbreaker.database.entity.Item

class SearchAdapter(val context: Context): RecyclerView.Adapter<ItemHolder>() {
    private var boxes: List<Box> = ArrayList()
    private var items: List<Item> = ArrayList()
    val database = AppDatabase.getInstance(context)

    init {
        boxes = database.boxes().findAll()
        items = database.items().findAll()
    }

    fun updateQuery(query: String?) {
        if (query != null) {
            boxes = database.boxes().findAllBoxesMatchingQuery(query)
            items = database.items().findAllItemsMatchingQuery(query)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.listitem, parent, false)
        return ItemHolder(view, null, false)
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