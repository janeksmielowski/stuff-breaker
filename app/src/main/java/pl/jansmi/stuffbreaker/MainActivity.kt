package pl.jansmi.stuffbreaker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.room.RoomDatabase

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.runBlocking
import pl.jansmi.stuffbreaker.adapter.ItemsAdapter
import pl.jansmi.stuffbreaker.database.AppDatabase
import pl.jansmi.stuffbreaker.database.entity.Box

class MainActivity : AppCompatActivity() {

    val EDIT_ITEM_REQUEST_CODE = 1
    val SCANNER_REQUEST_CODE = 2
    val CAMERA_PERMISSION_REQUEST_CODE = 3

    var cameraPermissionGranted: Boolean = false
    lateinit var currentBox: Box

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

        var initBox: Box = runBlocking { database.boxes().findBoxById(0) }
        if (initBox == null)
            currentBox = Box("Localizations", null, null)
        else
            currentBox = initBox

        checkPermissions()

        add_fab.setOnClickListener {
            val newIntent = Intent(this, EditItemActivity::class.java)
            newIntent.putExtra("box", currentBox.id)
            startActivityForResult(newIntent, EDIT_ITEM_REQUEST_CODE)
        }

        scan_fab.setOnClickListener {
            if (this.cameraPermissionGranted) {
                val newIntent = Intent(this, ScannerActivity::class.java)
                startActivityForResult(newIntent, SCANNER_REQUEST_CODE)
            }
        }

        supportFragmentManager
            .beginTransaction()
            .add(R.id.localization_fragment, LocalizationFragment(currentBox))
            .commit()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // TODO: treat scanned code
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && permissions[0].equals(Manifest.permission.CAMERA)) {
            if (grantResults[0].equals(PackageManager.PERMISSION_GRANTED))
                this.cameraPermissionGranted = true
            else
                Toast.makeText(this, "You will not be able to scan QR codes, until you grant camera permission.", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            this.cameraPermissionGranted = true
        }
    }

}
