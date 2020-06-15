package pl.jansmi.stuffbreaker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaActionSound
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
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
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            if (database.boxes().findBoxByQrCode(result!!.text) != null ||
                database.items().findItemByQrCode(result!!.text) != null
            ) {
                sound.play(MediaActionSound.STOP_VIDEO_RECORDING)
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                sound.play(MediaActionSound.FOCUS_COMPLETE)
                v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 100, 100), VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }

        val resultIntent = Intent()
        resultIntent.putExtra("content", result!!.text)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

}