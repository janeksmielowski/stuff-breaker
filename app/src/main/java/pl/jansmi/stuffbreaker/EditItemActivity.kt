package pl.jansmi.stuffbreaker

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.paris.extensions.style

import kotlinx.android.synthetic.main.activity_edit_item.*
import kotlinx.android.synthetic.main.content_edit_item.*
import pl.jansmi.stuffbreaker.database.AppDatabase
import pl.jansmi.stuffbreaker.database.entity.Item
import java.io.*
import java.lang.Exception
import java.util.*

class EditItemActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_QR_SCAN = 2

    private var item: Item? = null
    private var boxId: Int = -1

    private var imageBitmap: Bitmap? = null
    private var qrCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)
        setSupportActionBar(toolbar)

        val itemId = intent.getIntExtra("item", -1)
        boxId = intent.getIntExtra("box", -1)

        if (boxId == -1) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        Log.i("TAG", boxId.toString());

        val database = AppDatabase.getInstance(applicationContext)
        val box = database.boxes().findBoxById(boxId)
        actionBar?.title = box.name
        supportActionBar?.title = box.name

        if (itemId != -1) {
            item = database.items().findItemById(itemId)
            titleBox.setText("Edit item")
            name.setText(item!!.name)
            desc.setText(item!!.desc)

            if (!item!!.qrCode.isNullOrEmpty()) {
                qrCode = item!!.qrCode
                qr_label.text = "QR code attached"
                qr_btn.text = "Change QR code"
                qr_btn.style(R.style.Widget_AppCompat_Button_Colored)
            }

            if (!item!!.imagePath.isNullOrEmpty()) {
                photo_label.text = "Image attached"
                photo_btn.text = "Change image"
                photo_btn.style(R.style.Widget_AppCompat_Button_Colored)
            }
        }

        photo_btn.setOnClickListener { dispatchTakePictureIntent() }
        qr_btn.setOnClickListener { dispatchScanQrCodeIntent() }
        submit_btn.setOnClickListener { saveItemToDatabase() }

    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun dispatchScanQrCodeIntent() {
        // TODO: check if QR code is already scanned
//        if (qrCode scanned) {
//            // open new activity for scanning
//        } else {
//            // open popup with question "Choose action: change QR code [or] delete QR code"
//        }
        val intent = Intent(applicationContext, ScannerActivity::class.java)
        startActivityForResult(intent, REQUEST_QR_SCAN)
    }

    private fun loadImageFromDatabase(path: String?): Bitmap? {
        if (path == null)
            return null

        val contextWrapper = ContextWrapper(applicationContext)
        val directory = contextWrapper.getDir("images", Context.MODE_PRIVATE)

        // TODO: toasts
        return try {
            val file = File(directory, path)
            val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveImageToDatabase(image: Bitmap?): String? {
        if (image == null)
            return null

        val contextWrapper = ContextWrapper(applicationContext)
        val directory = contextWrapper.getDir("images", Context.MODE_PRIVATE)
        val filePath = "${UUID.randomUUID()}.jpg"

        val file = File(directory, filePath)
        var fos: FileOutputStream?

        // TODO: toasts
        try {
            fos = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return filePath
    }

    private fun saveItemToDatabase() {
        val database = AppDatabase.getInstance(applicationContext)

        if (item == null) { // insert new item
            AsyncTask.execute {
                val imagePath = saveImageToDatabase(imageBitmap!!)
                item = Item(name.text.toString(), desc.text.toString(), boxId, qrCode, imagePath)
                database.items().insert(item!!)
            }
            Toast.makeText(this, "Item created successfully!", Toast.LENGTH_SHORT).show()

        } else { // update item
            AsyncTask.execute {

                item!!.name = name.text.toString()
                item!!.desc = desc.text.toString()
                item!!.qrCode = qrCode;
                item!!.boxId = boxId;

                // if image bitmap is updated (else leave unchanged)
                if (imageBitmap != null)
                    item!!.imagePath = saveImageToDatabase(imageBitmap)

                database.items().update(item!!)
            }
            Toast.makeText(this, "Item updated successfully!", Toast.LENGTH_SHORT).show()
        }

        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                imageBitmap = data?.extras?.get("data") as Bitmap

                // alter layout
                photo_label.text = "Image attached"
                photo_btn.text = "Change image"
                photo_btn.style(R.style.Widget_AppCompat_Button_Colored)

            } else {
                Toast.makeText(applicationContext, "Error while capturing photo", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == REQUEST_QR_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                qrCode = data?.getStringExtra("content")

                // alter layout
                qr_label.text = "QR code attached"
                qr_btn.text = "Change QR code"
                qr_btn.style(R.style.Widget_AppCompat_Button_Colored)

            } else {
                Toast.makeText(applicationContext, "Error while scanning QR code", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_change -> {
                // TODO: new activity for changing localization/box
                //  and store new localization as boxId variable to further save in db
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
