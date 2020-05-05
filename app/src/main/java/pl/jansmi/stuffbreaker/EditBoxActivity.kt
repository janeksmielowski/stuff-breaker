package pl.jansmi.stuffbreaker

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit_item.*
import kotlinx.android.synthetic.main.content_edit_item.*
import pl.jansmi.stuffbreaker.database.AppDatabase
import pl.jansmi.stuffbreaker.database.entity.Box
import pl.jansmi.stuffbreaker.database.entity.Item

class EditBoxActivity : AppCompatActivity() {

    private var boxId: Int = -1
    private var parentId: Int = -1
    private var qrCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_box)
        setSupportActionBar(toolbar)

        boxId = intent.getIntExtra("box", -1)
        parentId = intent.getIntExtra("parent", -1)

        val database = AppDatabase.getInstance(applicationContext)
        var box: Box? = null

        // adding new box
        if (boxId != -1) {
            box = database.boxes().findBoxById(boxId)
            titleBox.setText("Edit box")
            name.setText(box.name)
            desc.setText(box.desc)
        }

        // note: we don't expect situation of adding new box not having parent
        if (parentId == -1) {
            actionBar?.title = box!!.name
            supportActionBar?.title = box.name

        } else {
            val parent = database.boxes().findBoxById(parentId)
            actionBar?.title = parent.name
            supportActionBar?.title = parent.name
        }

        submit_btn.setOnClickListener { saveBoxToDatabase() }

    }

    private fun saveBoxToDatabase() {
        var box: Box
        val database = AppDatabase.getInstance(applicationContext)

        if (boxId == -1) { // insert new box
            AsyncTask.execute {
                box = Box(name.text.toString(), desc.text.toString(), qrCode, parentId)
                database.boxes().insert(box)
            }
            Toast.makeText(this, "Box created successfully!", Toast.LENGTH_SHORT).show()

        } else { // update item
            AsyncTask.execute {
                box = database.boxes().findBoxById(boxId)
                box.name = name.text.toString()
                box.desc = desc.text.toString()
                box.parentId = parentId
                box.qrCode = qrCode
                database.boxes().update(box)
            }
            Toast.makeText(this, "Box updated successfully!", Toast.LENGTH_SHORT).show()
        }

        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        if (parentId == -1)
            menu.findItem(R.id.action_change).isEnabled = false

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_change -> {
                // TODO: new activity for changing localization/box
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}