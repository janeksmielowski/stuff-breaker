package pl.jansmi.stuffbreaker

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_edit_item.*
import kotlinx.android.synthetic.main.content_edit_item.*
import kotlinx.coroutines.runBlocking
import pl.jansmi.stuffbreaker.database.entity.Item
import java.util.*

class EditItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)
        setSupportActionBar(toolbar)

        submit_btn.setOnClickListener { saveItemToDatabase() }

    }

    fun saveItemToDatabase() {
        val itemId: Int = intent.getIntExtra("item", -1)
        val boxId: Int = intent.getIntExtra("box", -1)
        var item: Item

        if (boxId == -1) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        if (itemId == -1) { // insert new item
            AsyncTask.execute {
                item = Item(name.text.toString(), desc.text.toString(), boxId, null)
                MainActivity.database.items().insert(item)
            }
            Toast.makeText(this, "Item created successfully!", Toast.LENGTH_SHORT).show()

        } else { // update item
            AsyncTask.execute {
                item = runBlocking { MainActivity.database.items().findItemById(itemId) }
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
