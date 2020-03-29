package pl.jansmi.stuffbreaker

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set

import kotlinx.android.synthetic.main.activity_edit_item.*
import kotlinx.android.synthetic.main.content_edit_item.*
import kotlinx.coroutines.runBlocking
import pl.jansmi.stuffbreaker.database.entity.Item
import java.util.*

class EditItemActivity : AppCompatActivity() {

    private var itemId: Int = -1
    private var boxId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)
        setSupportActionBar(toolbar)

        itemId = intent.getIntExtra("item", -1)
        boxId = intent.getIntExtra("box", -1)

        if (boxId == -1) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        if (itemId != -1) {
            val item = MainActivity.database.items().findItemById(itemId)
            titleBox.setText("Edit item")
            name.setText(item.name)
            desc.setText(item.desc)
        }

        submit_btn.setOnClickListener { saveItemToDatabase() }

    }

    private fun saveItemToDatabase() {
        var item: Item

        if (itemId == -1) { // insert new item
            AsyncTask.execute {
                item = Item(name.text.toString(), desc.text.toString(), boxId, null)
                MainActivity.database.items().insert(item)
            }
            Toast.makeText(this, "Item created successfully!", Toast.LENGTH_SHORT).show()

        } else { // update item
            AsyncTask.execute {
                item = MainActivity.database.items().findItemById(itemId)
                item.name = name.text.toString()
                item.desc = desc.text.toString()
                // TODO item.boxId and item.qrCode
                MainActivity.database.items().update(item)
            }
            Toast.makeText(this, "Item updated successfully!", Toast.LENGTH_SHORT).show()
        }

        setResult(Activity.RESULT_OK)
        finish()
    }

}
