package pl.jansmi.stuffbreaker.adapter

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.jansmi.stuffbreaker.*
import pl.jansmi.stuffbreaker.database.AppDatabase
import pl.jansmi.stuffbreaker.database.entity.Box
import pl.jansmi.stuffbreaker.database.entity.Item
import java.io.File
import java.io.FileInputStream
import java.lang.Exception


class ItemsAdapter(
    val context: Context,
    val box: Box,
    val switchContent: (box: Box) -> Unit,
    val renderItems: Boolean
): RecyclerView.Adapter<ItemsAdapter.ItemHolder>() {

    private var boxes: List<Box> = ArrayList()
    private var items: List<Item> = ArrayList()

    class ItemHolder(view: View, val switchContent: (box: Box) -> Unit): RecyclerView.ViewHolder(view) {
        private var title: TextView
        private var desc: TextView
        private var image: ImageView

        init {
            title = view.findViewById(R.id.title)
            desc = view.findViewById(R.id.desc)
            image = view.findViewById(R.id.image)
        }

        private fun loadImageFromDatabase(path: String?): Bitmap? {
            if (path == null)
                return null

            val contextWrapper = ContextWrapper(itemView.context)
            val directory = contextWrapper.getDir("images", Context.MODE_PRIVATE)

            // TODO: toasts
            return try {
                val file = File(directory, path)
                val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun bindBox(box: Box) {
            title.text = box.name
            desc.text = box.desc

            itemView.setOnClickListener {
                switchContent(box)
            }
        }

        fun bindItem(item: Item) {
            title.text = item.name
            desc.text = item.desc

            if (!item.imagePath.isNullOrEmpty())
                image.setImageBitmap(loadImageFromDatabase(item.imagePath))
            else
                image.setImageResource(R.drawable.ic_baseline_crop_square_64)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ShowItemActivity::class.java)
                intent.putExtra("box", item.boxId)
                intent.putExtra("item", item.id)
                itemView.context.startActivity(intent)
            }
        }

    }

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
        return ItemHolder(view, switchContent)
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