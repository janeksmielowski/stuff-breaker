package pl.jansmi.stuffbreaker.adapter

import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.jansmi.stuffbreaker.MainActivity
import pl.jansmi.stuffbreaker.R
import pl.jansmi.stuffbreaker.database.entity.Box
import pl.jansmi.stuffbreaker.database.entity.Item

class ItemsAdapter(val boxId: Int): RecyclerView.Adapter<ItemsAdapter.ItemHolder>() {

    private lateinit var boxes: List<Box>
    private lateinit var items: List<Item>

    // TODO: desc , tags & image
    class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        private var title: TextView

        init {
            title = view.findViewById(R.id.title)
        }

        fun bindBox(box: Box) {
            title.text = box.name
        }

        fun bindItem(item: Item) {
            title.text = "\t${item.name}"
        }

    }

    init {
        AsyncTask.execute {
            boxes = MainActivity.database.boxes().findAllBoxesByParentId(boxId)
            items = MainActivity.database.items().findAllItemsByBoxId(boxId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.listitem, parent, false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return boxes.size + items.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if (position < boxes.size) {
            val box: Box = boxes[position]
            holder.bindBox(box)
        } else {
            val item: Item = items[position]
            holder.bindItem(item)
        }
    }

}