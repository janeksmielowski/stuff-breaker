package pl.jansmi.stuffbreaker

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.room.Room
import androidx.room.RoomDatabase

import kotlinx.android.synthetic.main.activity_main.*
import pl.jansmi.stuffbreaker.database.AppDatabase

class MainActivity : AppCompatActivity() {

    lateinit var database: AppDatabase;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        database = Room
            .databaseBuilder(applicationContext, AppDatabase::class.java, "database")
            .build()

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
