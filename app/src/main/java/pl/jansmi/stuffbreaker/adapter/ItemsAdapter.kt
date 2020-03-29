package pl.jansmi.stuffbreaker.adapter

import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.jansmi.stuffbreaker.EditBoxActivity
import pl.jansmi.stuffbreaker.EditItemActivity
import pl.jansmi.stuffbreaker.MainActivity
import pl.jansmi.stuffbreaker.R
import pl.jansmi.stuffbreaker.database.entity.Box
import pl.jansmi.stuffbreaker.database.entity.Item


class ItemsAdapter(val box: Box): RecyclerView.Adapter<ItemsAdapter.ItemHolder>() {

    private var boxes: List<Box>
    private var items: List<Item>

    // TODO: desc , tags & image
    class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        private var title: TextView
        private var desc: TextView
        private var image: ImageView

        init {
            title = view.findViewById(R.id.title)
            desc = view.findViewById(R.id.desc)
            image = view.findViewById(R.id.image)
        }

        fun bindBox(box: Box) {
            title.text = box.name
            desc.text = box.desc

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, EditBoxActivity::class.java)
                intent.putExtra("box", box.id)
                intent.putExtra("parent", box.parentId)
                itemView.context.startActivity(intent)
            }
        }

        fun bindItem(item: Item) {
            title.text = item.name
            desc.text = item.desc
            // TODO: image.setImageResource()
            // custom icon if no image provided
            image.setImageResource(R.drawable.ic_baseline_crop_square_64)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, EditItemActivity::class.java)
                intent.putExtra("box", item.boxId)
                intent.putExtra("item", item.id)
                itemView.context.startActivity(intent)
            }
        }

    }

    init {
        boxes = MainActivity.database.boxes().findAllBoxesByParentId(box.id)
        items = MainActivity.database.items().findAllItemsByBoxId(box.id)
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
            val item: Item = items[position - boxes.size]
            holder.bindItem(item)
        }
    }

}