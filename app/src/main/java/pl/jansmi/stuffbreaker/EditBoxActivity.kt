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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_box)
        setSupportActionBar(toolbar)

        boxId = intent.getIntExtra("box", -1)
        parentId = intent.getIntExtra("parent", -1)

        if (parentId == -1) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        val database = AppDatabase.getInstance(applicationContext)
        val parent = database.boxes().findBoxById(parentId)
        actionBar?.title = parent.name
        supportActionBar?.title = parent.name

        if (boxId != -1) {
            val box = database.boxes().findBoxById(boxId)
            titleBox.setText("Edit box")
            name.setText(box.name)
            desc.setText(box.desc)
        }

        submit_btn.setOnClickListener { saveBoxToDatabase() }

    }

    private fun saveBoxToDatabase() {
        var box: Box
        val database = AppDatabase.getInstance(applicationContext)

        if (boxId == -1) { // insert new box
            AsyncTask.execute {
                box = Box(name.text.toString(), desc.text.toString(), null, parentId)
                database.boxes().insert(box)
            }
            Toast.makeText(this, "Box created successfully!", Toast.LENGTH_SHORT).show()

        } else { // update item
            AsyncTask.execute {
                box = database.boxes().findBoxById(boxId)
                box.name = name.text.toString()
                box.desc = desc.text.toString()
                // TODO box.parent and box.qrCode
                database.boxes().update(box)
            }
            Toast.makeText(this, "Box updated successfully!", Toast.LENGTH_SHORT).show()
        }

        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
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