package pl.jansmi.stuffbreaker

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
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
import pl.jansmi.stuffbreaker.dialogs.QRCodeDialogFragment

class EditBoxActivity : AppCompatActivity(),
    QRCodeDialogFragment.QRCodeDialogListener
{

    val REQUEST_QR_SCAN = 2

    private var box: Box? = null
    private var parentId: Int = -1
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

        qr_btn.setOnClickListener { dispatchScanQrCodeIntent() }
        submit_btn.setOnClickListener { saveBoxToDatabase() }

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

    private fun dispatchScanQrCodeIntent() {
        if (qrCode == null && (box == null || box!!.qrCode.isNullOrEmpty())) {
            val intent = Intent(applicationContext, ScannerActivity::class.java)
            startActivityForResult(intent, REQUEST_QR_SCAN)
        } else {
            val dialog = QRCodeDialogFragment()
            dialog.show(supportFragmentManager, "QRCodeDialogFragment")
        }
    }

    private fun saveBoxToDatabase() {
        val database = AppDatabase.getInstance(applicationContext)

        if (box == null) { // insert new box
            AsyncTask.execute {
                box = Box(name.text.toString(), desc.text.toString(), qrCode, parentId)
                database.boxes().insert(box!!)
            }
            Toast.makeText(this, "Box created successfully!", Toast.LENGTH_SHORT).show()

        } else { // update item
            AsyncTask.execute {
                box!!.name = name.text.toString()
                box!!.desc = desc.text.toString()
                box!!.parentId = parentId
                box!!.qrCode = qrCode

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

        if (requestCode == REQUEST_QR_SCAN) {
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
                // TODO: new activity for changing localization/box
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}