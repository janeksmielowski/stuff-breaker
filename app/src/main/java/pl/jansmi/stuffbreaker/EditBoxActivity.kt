package pl.jansmi.stuffbreaker

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit_item.*
import kotlinx.android.synthetic.main.content_edit_item.*
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

        if (boxId != -1) {
            val box = MainActivity.database.boxes().findBoxById(boxId)
            titleBox.setText("Edit box")
            name.setText(box.name)
            desc.setText(box.desc)
        }

        submit_btn.setOnClickListener { saveBoxToDatabase() }

    }

    private fun saveBoxToDatabase() {
        var box: Box

        if (boxId == -1) { // insert new box
            AsyncTask.execute {
                box = Box(name.text.toString(), desc.text.toString(), null, parentId)
                MainActivity.database.boxes().insert(box)
            }
            Toast.makeText(this, "Box created successfully!", Toast.LENGTH_SHORT).show()

        } else { // update item
            AsyncTask.execute {
                box = MainActivity.database.boxes().findBoxById(boxId)
                box.name = name.text.toString()
                box.desc = desc.text.toString()
                // TODO box.parent and box.qrCode
                MainActivity.database.boxes().update(box)
            }
            Toast.makeText(this, "Box updated successfully!", Toast.LENGTH_SHORT).show()
        }

        setResult(Activity.RESULT_OK)
        finish()
    }

}