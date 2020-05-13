package pl.jansmi.stuffbreaker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_change_path.*
import pl.jansmi.stuffbreaker.database.AppDatabase
import pl.jansmi.stuffbreaker.database.entity.Box

class ChangePathActivity : AppCompatActivity() {

    var currentBox: Box? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_path)
        setSupportActionBar(toolbar)

        val boxId = intent.getIntExtra("box", -1)

        if (boxId == -1) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        val database = AppDatabase.getInstance(applicationContext)
        currentBox = database.boxes().findBoxById(boxId)

        val currentPath = getCurrentPath(currentBox!!)
        currentPath.forEach {
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_from_right, R.anim.slide_out_to_right,
                    R.anim.slide_in_from_right, R.anim.slide_out_to_right)
                .add(R.id.path_fragment, LocalizationFragment(it, this::switchContent, false))
                .addToBackStack(it.name)
                .commit()
        }

        actionBar?.title = currentBox!!.name
        supportActionBar?.title = currentBox!!.name
    }

    private fun getCurrentPath(box: Box): ArrayList<Box> {
        var list = ArrayList<Box>()
        if (box.parentId != null) {
            val database = AppDatabase.getInstance(this)
            val parent = database.boxes().findBoxById(box.parentId!!)
            list = getCurrentPath(parent!!)
        }
        list.add(box)
        return list
    }

    private fun switchContent(box: Box) {
        currentBox = box;
        actionBar?.title = box.name
        supportActionBar?.title = box.name

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_from_right, R.anim.slide_out_to_right,
                R.anim.slide_in_from_right, R.anim.slide_out_to_right)
            .add(R.id.path_fragment, LocalizationFragment(currentBox!!, this::switchContent, false))
            .addToBackStack(currentBox!!.name)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_path, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                val resultIntent = Intent()
                resultIntent.putExtra("box", currentBox!!.id)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (currentBox!!.parentId != null) {
            val database = AppDatabase.getInstance(applicationContext)
            currentBox = database.boxes().findBoxById(currentBox!!.parentId!!)
            actionBar?.title = currentBox!!.name
            supportActionBar?.title = currentBox!!.name
            // TODO: reload fragment content
        }
        super.onBackPressed()
    }
}