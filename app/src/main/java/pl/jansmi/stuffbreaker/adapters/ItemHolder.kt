package pl.jansmi.stuffbreaker.adapters

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.jansmi.stuffbreaker.MainActivity
import pl.jansmi.stuffbreaker.R
import pl.jansmi.stuffbreaker.ShowItemActivity
import pl.jansmi.stuffbreaker.database.entity.Box
import pl.jansmi.stuffbreaker.database.entity.Item
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

class ItemHolder(view: View, val switchContent: ((box: Box) -> Unit)?): RecyclerView.ViewHolder(view) {
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

        if (box.image != null)
            image.setImageBitmap(BitmapFactory.decodeByteArray(box.image, 0, box.image!!.size))
        else
            image.setImageResource(R.drawable.ic_baseline_folder_open_64)

        itemView.setOnClickListener {
            if (switchContent != null) {
                switchContent.invoke(box)
            } else {
                val intent = Intent(itemView.context, MainActivity::class.java)
                intent.putExtra("box", box.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    fun bindItem(item: Item) {
        title.text = item.name
        desc.text = item.desc

        if (item.image != null)
            image.setImageBitmap(BitmapFactory.decodeByteArray(item.image, 0, item.image!!.size))
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