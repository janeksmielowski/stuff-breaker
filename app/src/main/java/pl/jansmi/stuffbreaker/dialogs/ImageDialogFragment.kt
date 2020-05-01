package pl.jansmi.stuffbreaker.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_image.view.*
import pl.jansmi.stuffbreaker.R
import java.lang.ClassCastException
import java.lang.IllegalStateException

class ImageDialogFragment(val imageBitmap: Bitmap): DialogFragment() {

    internal lateinit var listener: ImageDialogListener

    interface ImageDialogListener {
        fun onImageDialogChangeClick(dialog: DialogFragment)
        fun onImageDialogDeleteClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as ImageDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement ImageDialogListener interface")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            val imageView = inflater.inflate(R.layout.dialog_image, null)
            imageView.image.setImageBitmap(imageBitmap)

            val builder = AlertDialog.Builder(it)
            builder
                .setTitle("Image already supplied")
                .setMessage("What would you like to do?")
                .setView(imageView)
                .setPositiveButton("Change") { dialog, id ->
                    listener.onImageDialogChangeClick(this)
                }
                .setNeutralButton("Delete") { dialog, id ->
                    listener.onImageDialogDeleteClick(this)
                }
                .setNegativeButton("Cancel", { dialog, id -> getDialog()!!.cancel() })
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}