package pl.jansmi.stuffbreaker

import android.app.Activity
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

        fab.setOnClickListener { view ->
            val intent = Intent(this, EditItemActivity::class.java)
            intent.putExtra("box", boxId)
            intent.putExtra("item", itemId)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val database = AppDatabase.getInstance(applicationContext)
        val item = database.items().findItemById(itemId)
        actionBar?.title = item.name
        supportActionBar?.title = item.name

        if (!item.imagePath.isNullOrEmpty())
            image.setImageBitmap(loadImageFromDatabase(item.imagePath))

        description.text = item.desc

        if (!item.qrCode.isNullOrEmpty()) {
            code.setText("QR code attached")
            code.setTextColor(resources.getColor(R.color.colorPrimary, theme))
        }
    }

    // TODO: unify this loader with other loaders in seperate class
    private fun loadImageFromDatabase(path: String?): Bitmap? {
        if (path == null)
            return null

        val contextWrapper = ContextWrapper(applicationContext)
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
}