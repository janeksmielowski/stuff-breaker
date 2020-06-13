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
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.airbnb.paris.extensions.style
import kotlinx.android.synthetic.main.activity_edit_item.*
import kotlinx.android.synthetic.main.content_edit_item.*
import pl.jansmi.stuffbreaker.database.AppDatabase
import pl.jansmi.stuffbreaker.database.entity.Box
import pl.jansmi.stuffbreaker.database.entity.Item
import pl.jansmi.stuffbreaker.dialogs.ImageDialogFragment
import pl.jansmi.stuffbreaker.dialogs.QRCodeDialogFragment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*

class EditBoxActivity : AppCompatActivity(),
    ImageDialogFragment.ImageDialogListener,
    QRCodeDialogFragment.QRCodeDialogListener
{

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_QR_SCAN = 2
    val REQUEST_CHANGE_PATH = 3

    private var box: Box? = null
    private var parentId: Int = -1

    private var shouldRemoveImage: Boolean = false
    private var imageBitmap: Bitmap? = null
    private var qrCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_box)
        setSupportActionBar(toolbar)

        val boxId = intent.getIntExtra("box", -1)
        parentId = intent.getIntExtra("parent", -1)

        val database = AppDatabase.getInstance(applicationContext)

        // adding new box
        if (boxId != -1) {
            box = database.boxes().findBoxById(boxId)
            titleBox.setText("Edit box")
            name.setText(box!!.name)
            desc.setText(box!!.desc)

            if (!box!!.qrCode.isNullOrEmpty()) {
                qrCode = box!!.qrCode
                // alter layout
                qr_label.text = "QR code attached"
                qr_btn.text = "Change QR code"
                qr_btn.style(R.style.Widget_AppCompat_Button_Colored)
            }

            if (!box!!.imagePath.isNullOrEmpty()) {
                // alter layout
                photo_label.text = "Image attached"
                photo_btn.text = "Change image"
                photo_btn.style(R.style.Widget_AppCompat_Button_Colored)
            }

        }

        // note: we don't expect situation of adding new box not having parent
        if (parentId == -1) {
            actionBar?.title = box!!.name
            supportActionBar?.title = box!!.name

        } else {
            val parent = database.boxes().findBoxById(parentId)!!
            actionBar?.title = parent.name
            supportActionBar?.title = parent.name
        }

        photo_btn.setOnClickListener { dispatchTakePictureIntent() }
        qr_btn.setOnClickListener { dispatchScanQrCodeIntent() }
        submit_btn.setOnClickListener { saveBoxToDatabase() }

    }

    override fun onImageDialogChangeClick(dialog: DialogFragment) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onImageDialogDeleteClick(dialog: DialogFragment) {
        shouldRemoveImage = true
        imageBitmap = null

        // alter layout
        photo_label.text = "No picture attached"
        photo_btn.text = "Attach image"
        photo_btn.style(R.style.Widget_AppCompat_Button)

        Toast.makeText(applicationContext, "Image removed", Toast.LENGTH_SHORT).show()
    }

    override fun onQRCodeDialogChangeClick(dialog: DialogFragment) {
        val intent = Intent(applicationContext, ScannerActivity::class.java)
        startActivityForResult(intent, REQUEST_QR_SCAN)
    }

    override fun onQRCodeDialogDeleteClick(dialog: DialogFragment) {
        qrCode = null

        // alter layout
        qr_label.text = "No QR code attached"
        qr_btn.text = "Attach QR code"
        qr_btn.style(R.style.Widget_AppCompat_Button)

        Toast.makeText(applicationContext, "QR code removed", Toast.LENGTH_SHORT).show()
    }

    private fun dispatchTakePictureIntent() {
        if (imageBitmap == null && (box == null || box!!.imagePath.isNullOrEmpty())) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
                intent.resolveActivity(packageManager)?.also {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }
        } else {
            var image = imageBitmap
            if (imageBitmap == null && box != null && !box!!.imagePath.isNullOrEmpty())
                image = loadImageFromDatabase(box!!.imagePath)

            val dialog = ImageDialogFragment(image!!)
            dialog.show(supportFragmentManager, "ImageDialogFragment")
        }
    }

    private fun dispatchScanQrCodeIntent() {
        if (qrCode == null && (box == null || box!!.qrCode.isNullOrEmpty())) {
            val intent = Intent(applicationContext, ScannerActivity::class.java)
            startActivityForResult(intent, REQUEST_QR_SCAN)
        } else {
            val dialog = QRCodeDialogFragment()
            dialog.show(supportFragmentManager, "QRCodeDialogFragment")
        }
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
        val fos: FileOutputStream?

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

    private fun deleteImageFromDatabase(path: String?): Boolean {
        if (path == null)
            return false

        val contextWrapper = ContextWrapper(applicationContext)
        val directory = contextWrapper.getDir("images", Context.MODE_PRIVATE)

        // TODO: toasts
        return try {
            val file = File(directory, path)
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun saveBoxToDatabase() {
        val database = AppDatabase.getInstance(applicationContext)

        if (box == null) { // insert new box
            AsyncTask.execute {
                var imagePath: String? = null
                if (imageBitmap != null)
                    imagePath = saveImageToDatabase(imageBitmap!!)

                box = Box(name.text.toString(), desc.text.toString(), qrCode, parentId, imagePath)
                database.boxes().insert(box!!)
            }
            Toast.makeText(this, "Box created successfully!", Toast.LENGTH_SHORT).show()

        } else { // update item
            AsyncTask.execute {
                box!!.name = name.text.toString()
                box!!.desc = desc.text.toString()
                box!!.parentId = parentId
                box!!.qrCode = qrCode

                if (shouldRemoveImage) {
                    deleteImageFromDatabase(box!!.imagePath)
                    box!!.imagePath = null
                }

                // if image bitmap is updated (else leave unchanged)
                if (imageBitmap != null)
                    box!!.imagePath = saveImageToDatabase(imageBitmap)

                database.boxes().update(box!!)
            }
            Toast.makeText(this, "Box updated successfully!", Toast.LENGTH_SHORT).show()
        }

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun qrCodeDuplicate(qr: String): Boolean {
        val database = AppDatabase.getInstance(this)
        if (database.items().findItemByQrCode(qr) != null ||
            database.boxes().findBoxByQrCode(qr) != null) {
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                imageBitmap = data?.extras?.get("data") as Bitmap
                shouldRemoveImage = true

                // alter layout
                photo_label.text = "Image attached"
                photo_btn.text = "Change image"
                photo_btn.style(R.style.Widget_AppCompat_Button_Colored)
                Toast.makeText(applicationContext, "Image attached successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Image capture canceled", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == REQUEST_QR_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                val qr = data?.getStringExtra("content")
                if (!qrCodeDuplicate(qr!!)) {
                    qrCode = qr

                    // alter layout
                    qr_label.text = "QR code attached"
                    qr_btn.text = "Change QR code"
                    qr_btn.style(R.style.Widget_AppCompat_Button_Colored)
                    Toast.makeText(applicationContext, "QR code attached successfully!", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(applicationContext, "This QR code is already attached to some item or box", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "QR code scan canceled", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == REQUEST_CHANGE_PATH) {
            if (resultCode == Activity.RESULT_OK) {
                val tmpBoxId = data?.getIntExtra("box", -1)
                if (tmpBoxId != -1) {
                    parentId = tmpBoxId!!;

                    val database = AppDatabase.getInstance(applicationContext)
                    val box = database.boxes().findBoxById(parentId)
                    actionBar?.title = box!!.name
                    supportActionBar?.title = box.name

                    Toast.makeText(applicationContext, "Path changed successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Unexpected error while changing path", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Path change canceled", Toast.LENGTH_SHORT).show()
            }
        }

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
                val intent = Intent(this, ChangePathActivity::class.java)
                intent.putExtra("box", parentId)
                startActivityForResult(intent, REQUEST_CHANGE_PATH)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}