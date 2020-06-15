package pl.jansmi.stuffbreaker

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import pl.jansmi.stuffbreaker.database.AppDatabase
import pl.jansmi.stuffbreaker.database.entity.Box

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    val EDIT_BOX_REQUEST_CODE = 1
    val EDIT_ITEM_REQUEST_CODE = 2
    val SCANNER_REQUEST_CODE = 3
    val PERMISSION_REQUEST_CODE = 4

    var cameraPermissionGranted: Boolean = false
    var readPermissionGranted: Boolean = false
    var writePermissionGranted: Boolean = false

    var currentBox: Box? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val database = AppDatabase.getInstance(applicationContext)
        val boxId = intent.getIntExtra("box", -1)

        if (boxId == -1) {
            currentBox = database.boxes().findMainBox()

            if (currentBox == null) {
                currentBox = Box("Localizations", "", null, null, null)
                database.boxes().insert(currentBox!!)
                currentBox = database.boxes().findMainBox()

                // Sample locations
                database.boxes().insert(Box("Attic", "", null, currentBox!!.id, null))
                database.boxes().insert(Box("Basement", "", null, currentBox!!.id, null))
                database.boxes().insert(Box("Garage", "", null, currentBox!!.id, null))
                database.boxes().insert(Box("Other", "", null, currentBox!!.id, null))
            }

        } else {
            currentBox = database.boxes().findBoxById(boxId)
        }

        checkPermissions()

        add_fab.setOnClickListener {
            val newIntent = Intent(this, EditItemActivity::class.java)
            newIntent.putExtra("box", currentBox!!.id)
            startActivityForResult(newIntent, EDIT_ITEM_REQUEST_CODE)
        }

        scan_fab.setOnClickListener {
            if (this.cameraPermissionGranted) {
                val newIntent = Intent(this, ScannerActivity::class.java)
                newIntent.putExtra("shouldValidate", true)
                startActivityForResult(newIntent, SCANNER_REQUEST_CODE)
            } else {
                checkPermissions()
            }
        }

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_from_right, R.anim.slide_out_to_right,
                R.anim.slide_in_from_right, R.anim.slide_out_to_right)
            .add(R.id.localization_fragment, LocalizationFragment(currentBox!!, this::switchContent, true, null))
            .commit()

        actionBar?.title = currentBox!!.name
        supportActionBar?.title = currentBox!!.name
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
            .add(R.id.localization_fragment, LocalizationFragment(currentBox!!, this::switchContent, true, null))
            .addToBackStack(currentBox!!.name)
            .commit()
    }

    private fun deleteBoxAndChildren(box: Box) {
        val database = AppDatabase.getInstance(applicationContext)
        database.boxes()
            .findAllBoxesByParentId(box.id)
            .forEach {
                deleteBoxAndChildren(it)
            }
        database.items()
            .findAllItemsByBoxId(box.id)
            .forEach {
                database.items().delete(it)
            }
        database.boxes().delete(box)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_new_box -> {
                val intent = Intent(applicationContext, EditBoxActivity::class.java)
                intent.putExtra("parent", currentBox!!.id)
                startActivityForResult(intent, EDIT_BOX_REQUEST_CODE)
                true
            }
            R.id.action_edit -> {
                val intent = Intent(applicationContext, EditBoxActivity::class.java)
                intent.putExtra("parent", currentBox!!.parentId)
                intent.putExtra("box", currentBox!!.id)
                startActivityForResult(intent, EDIT_BOX_REQUEST_CODE)
                true
            }
            R.id.action_delete -> {
                val builder = AlertDialog.Builder(this)
                builder
                    .setTitle("Confirm delete")
                    .setMessage("Are you sure to delete box: ${currentBox!!.name}?")
                    .setPositiveButton("Yes") { _, _ ->
                        val database = AppDatabase.getInstance(applicationContext)

                        if (currentBox!!.parentId != null) {
                            val parentBox = database.boxes().findBoxById(currentBox!!.parentId!!)
                            deleteBoxAndChildren(currentBox!!)
                            currentBox = parentBox
                        } else {
                            deleteBoxAndChildren(currentBox!!)
                        }

                        actionBar?.title = currentBox!!.name
                        supportActionBar?.title = currentBox!!.name

                        onBackPressed()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.cancel()
                    }
                builder.create().show()
                true
            }
            R.id.action_search -> {
                val intent = Intent(applicationContext, SearchActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_import -> {
                if (this.readPermissionGranted) {
                    val builder = AlertDialog.Builder(this)
                    builder
                        .setTitle("Import database")
                        .setMessage("Are you sure to import database from /Documents?")
                        .setPositiveButton("Yes") { _, _ ->
                            AppDatabase.importDatabase(this)
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.cancel()
                        }
                    builder.create().show()
                } else {
                    checkPermissions()
                }
                true
            }
            R.id.action_export -> {
                if (this.writePermissionGranted) {
                    val builder = AlertDialog.Builder(this)
                    builder
                        .setTitle("Export database")
                        .setMessage("Are you sure to export database to /Documents?")
                        .setPositiveButton("Yes") { _, _ ->
                            AppDatabase.exportDatabase(this)
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.cancel()
                        }
                    builder.create().show()
                } else {
                    checkPermissions()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // reload fragment on activities result
        if (requestCode == EDIT_BOX_REQUEST_CODE || requestCode == EDIT_ITEM_REQUEST_CODE) {
            reloadFragment(currentBox!!.name)
        }

        if (requestCode == SCANNER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val qr = data?.getStringExtra("content")
                val database = AppDatabase.getInstance(this)

                val box = database.boxes().findBoxByQrCode(qr!!);
                val item = database.items().findItemByQrCode(qr)

                if (box != null) {
                    switchContent(box)
                } else if (item != null) {
                    val intent = Intent(this, ShowItemActivity::class.java)
                    intent.putExtra("box", item.boxId)
                    intent.putExtra("item", item.id)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No box/item found with this code", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "QR code scan canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0].equals(PackageManager.PERMISSION_GRANTED))
                this.cameraPermissionGranted = true
            else
                Toast.makeText(this, "You will not be able to scan QR codes, until you grant camera permission.", Toast.LENGTH_SHORT).show()

            if (grantResults[1].equals(PackageManager.PERMISSION_GRANTED))
                this.readPermissionGranted = true
            else
                Toast.makeText(this, "You will not be able to import database, until you grant storage read permission.", Toast.LENGTH_LONG).show()

            if (grantResults[2].equals(PackageManager.PERMISSION_GRANTED))
                this.writePermissionGranted = true
            else
                Toast.makeText(this, "You will not be able to export database, until you grant storage write permission.", Toast.LENGTH_LONG).show()
        }

    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            this.cameraPermissionGranted = true
            this.readPermissionGranted = true
            this.writePermissionGranted = true
        }

    }

    fun reloadFragment(name: String) {
        val fragment = supportFragmentManager.findFragmentByTag(name)
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .detach(fragment)
                .attach(fragment)
                .commit()
        }
    }

    override fun onBackPressed() {
        if (currentBox != null && currentBox!!.parentId != null) {
            val database = AppDatabase.getInstance(applicationContext)
            currentBox = database.boxes().findBoxById(currentBox!!.parentId!!)
            actionBar?.title = currentBox!!.name
            supportActionBar?.title = currentBox!!.name
            reloadFragment(currentBox!!.name)
        }
        super.onBackPressed()
    }

}
