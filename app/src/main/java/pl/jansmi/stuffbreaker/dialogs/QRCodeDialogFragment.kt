package pl.jansmi.stuffbreaker.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException
import java.lang.IllegalStateException

class QRCodeDialogFragment: DialogFragment() {

    internal lateinit var listener: QRCodeDialogListener

    interface QRCodeDialogListener {
        fun onQRCodeDialogChangeClick(dialog: DialogFragment)
        fun onQRCodeDialogDeleteClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as QRCodeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement QRCodeDialogListener interface")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder
                .setTitle("QR code already supplied")
                .setMessage("What would you like to do?")
                .setPositiveButton("Change") { dialog, id ->
                    listener.onQRCodeDialogChangeClick(this)
                }
                .setNeutralButton("Delete") { dialog, id ->
                    listener.onQRCodeDialogDeleteClick(this)
                }
                .setNegativeButton("Cancel", { dialog, id -> getDialog()!!.cancel() })
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
