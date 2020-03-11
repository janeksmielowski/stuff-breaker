package pl.jansmi.stuffbreaker

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.room.RoomDatabase

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import pl.jansmi.stuffbreaker.adapter.ItemsAdapter
import pl.jansmi.stuffbreaker.database.AppDatabase

class MainActivity : AppCompatActivity() {

    val EDIT_ITEM_REQUEST_CODE = 1

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        database = Room
            .databaseBuilder(applicationContext, AppDatabase::class.java, "database")
            .build()

        add_fab.setOnClickListener {
            val newIntent = Intent(this, EditItemActivity::class.java)
            val boxId = 0 // TODO: fetch box id
            newIntent.putExtra("box", boxId)
            startActivityForResult(newIntent, EDIT_ITEM_REQUEST_CODE)
        }

        // TODO: create fragment with recycler view

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
