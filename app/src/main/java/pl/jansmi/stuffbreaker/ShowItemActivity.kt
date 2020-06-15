package pl.jansmi.stuffbreaker

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.paris.extensions.style

import kotlinx.android.synthetic.main.activity_show_item.*
import kotlinx.android.synthetic.main.content_edit_item.*
import kotlinx.android.synthetic.main.content_edit_item.desc
import kotlinx.android.synthetic.main.content_show_item.*
import pl.jansmi.stuffbreaker.database.AppDatabase
import pl.jansmi.stuffbreaker.database.entity.Item
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

class ShowItemActivity : AppCompatActivity() {

    private var item: Item? = null
    private var boxId: Int = -1
    private var itemId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_item)
        setSupportActionBar(toolbar)

        itemId = intent.getIntExtra("item", -1)
        boxId = intent.getIntExtra("box", -1)

        if (boxId == -1 || itemId == -1) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        edit_btn.setOnClickListener {
            val intent = Intent(this, EditItemActivity::class.java)
            intent.putExtra("box", boxId)
            intent.putExtra("item", itemId)
            startActivity(intent)
        }

        delete_btn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder
                .setTitle("Confirm delete")
                .setMessage("Are you sure to delete item: ${item!!.name}?")
                .setPositiveButton("Yes") { _, _ ->
                    val database = AppDatabase.getInstance(applicationContext)
                    database.items().delete(item!!)
                    finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
            builder.create().show()
        }
    }

    override fun onStart() {
        super.onStart()

        val database = AppDatabase.getInstance(applicationContext)
        item = database.items().findItemById(itemId)
        actionBar?.title = item!!.name
        supportActionBar?.title = item!!.name

        if (item!!.image != null)
            image.setImageBitmap(BitmapFactory.decodeByteArray(item!!.image, 0, item!!.image!!.size))

        description.text = item!!.desc
        if (item!!.eanUpc.isNullOrEmpty())
            ean.text = ""
        else
            ean.text = "EAN/UPC: " + item!!.eanUpc

        kwds.text = item!!.keywords

        if (!item!!.qrCode.isNullOrEmpty()) {
            code.setText("QR code attached")
            code.setTextColor(resources.getColor(R.color.colorPrimary, theme))
        }
    }

}