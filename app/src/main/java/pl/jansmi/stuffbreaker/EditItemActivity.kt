package pl.jansmi.stuffbreaker

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
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
import androidx.fragment.app.DialogFragment
import com.airbnb.paris.extensions.style

import kotlinx.android.synthetic.main.activity_edit_item.*
import kotlinx.android.synthetic.main.content_edit_item.*
import pl.jansmi.stuffbreaker.database.AppDatabase
import pl.jansmi.stuffbreaker.database.entity.Box
import pl.jansmi.stuffbreaker.database.entity.Item
import pl.jansmi.stuffbreaker.dialogs.ImageDialogFragment
import pl.jansmi.stuffbreaker.dialogs.QRCodeDialogFragment
import java.io.*
import java.lang.Exception
import java.util.*

class EditItemActivity : AppCompatActivity(),
    ImageDialogFragment.ImageDialogListener,
    QRCodeDialogFragment.QRCodeDialogListener
{

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_QR_SCAN = 2
    val REQUEST_CHANGE_PATH = 3

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

        val database = AppDatabase.getInstance(applicationContext)
        val box = database.boxes().findBoxById(boxId)
        actionBar?.title = box!!.name
        supportActionBar?.title = box.name

        if (itemId != -1) {
            item = database.items().findItemById(itemId)
            titleBox.setText("Edit item")
            name.setText(item!!.name)
            desc.setText(item!!.desc)
            keywords.setText(item!!.keywords)
            eanUpc.setText(item!!.eanUpc)

            if (!item!!.qrCode.isNullOrEmpty()) {
                qrCode = item!!.qrCode
                // alter layout
                qr_label.text = "QR code attached"
                qr_btn.text = "Change QR code"
                qr_btn.style(R.style.Widget_AppCompat_Button_Colored)
            }

            if (item!!.image != null) {
                imageBitmap = BitmapFactory.decodeByteArray(item!!.image, 0, item!!.image!!.size)
                // alter layout
                photo_label.text = "Image attached"
                photo_btn.text = "Change image"
                photo_btn.style(R.style.Widget_AppCompat_Button_Colored)
            }
        }

        photo_btn.setOnClickListener { dispatchTakePictureIntent() }
        qr_btn.setOnClickListener { dispatchScanQrCodeIntent() }
        submit_btn.setOnClickListener { saveItemToDatabase() }

    }

    override fun onImageDialogChangeClick(dialog: DialogFragment) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onImageDialogDeleteClick(dialog: DialogFragment) {
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
        if (imageBitmap == null) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
                intent.resolveActivity(packageManager)?.also {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }
        } else {
            val dialog = ImageDialogFragment(imageBitmap!!)
            dialog.show(supportFragmentManager, "ImageDialogFragment")
        }
    }

    private fun dispatchScanQrCodeIntent() {
        if (qrCode == null) {
            val intent = Intent(applicationContext, ScannerActivity::class.java)
            startActivityForResult(intent, REQUEST_QR_SCAN)
        } else {
            val dialog = QRCodeDialogFragment()
            dialog.show(supportFragmentManager, "QRCodeDialogFragment")
        }
    }

    private fun saveItemToDatabase() {
        val database = AppDatabase.getInstance(applicationContext)

        if (item == null) { // insert new item
            AsyncTask.execute {
                val stream = ByteArrayOutputStream()
                if (imageBitmap != null) {
                    imageBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    item = Item(name.text.toString(), desc.text.toString(), keywords.text.toString(), eanUpc.text.toString(), boxId, qrCode, stream.toByteArray())
                } else {
                    item = Item(name.text.toString(), desc.text.toString(), keywords.text.toString(), eanUpc.text.toString(), boxId, qrCode, null)
                }

                database.items().insert(item!!)
            }
            Toast.makeText(this, "Item created successfully!", Toast.LENGTH_SHORT).show()

        } else { // update item
            AsyncTask.execute {
                item!!.name = name.text.toString()
                item!!.desc = desc.text.toString()
                item!!.keywords = keywords.text.toString()
                item!!.eanUpc = eanUpc.text.toString()
                item!!.qrCode = qrCode;
                item!!.boxId = boxId;

                val stream = ByteArrayOutputStream()
                if (imageBitmap != null) {
                    imageBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    item!!.image = stream.toByteArray()
                } else {
                    item!!.image = null
                }

                database.items().update(item!!)
            }
            Toast.makeText(this, "Item updated successfully!", Toast.LENGTH_SHORT).show()
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
                    boxId = tmpBoxId!!;

                    val database = AppDatabase.getInstance(applicationContext)
                    val box = database.boxes().findBoxById(boxId)
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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_change -> {
                val intent = Intent(this, ChangePathActivity::class.java)
                intent.putExtra("box", boxId)
                startActivityForResult(intent, REQUEST_CHANGE_PATH)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
