package pl.jansmi.stuffbreaker

import android.app.Activity
import android.content.Intent
import android.media.MediaActionSound
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pl.jansmi.stuffbreaker.database.AppDatabase

class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var scannerView: ZXingScannerView
    private var shouldValidate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)
        shouldValidate = intent.getBooleanExtra("shouldValidate", false)
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun handleResult(result: Result?) {
        if (shouldValidate) {
            val database = AppDatabase.getInstance(applicationContext)
            val sound = MediaActionSound()
            if (database.boxes().findBoxByQrCode(result!!.text) != null ||
                database.items().findItemByQrCode(result!!.text) != null
            ) {
                sound.play(MediaActionSound.START_VIDEO_RECORDING)
            } else {
                sound.play(MediaActionSound.STOP_VIDEO_RECORDING)
            }
        }

        val resultIntent = Intent()
        resultIntent.putExtra("content", result!!.text)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

}