package com.papayainc.findit.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.papayainc.findit.R
import com.papayainc.findit.model.ScanResult
import com.papayainc.findit.processors.ImageProcessor
import com.papayainc.findit.utils.SharedPrefsUtils
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.parameter.Flash
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.autoFlash
import io.fotoapparat.selector.back
import io.fotoapparat.selector.off
import io.fotoapparat.view.CameraView

class CameraFragment : Fragment(), ActivityCompat.OnRequestPermissionsResultCallback, ImageProcessor.Callback,
    View.OnClickListener {
    companion object {
        val TAG = "[" + CameraFragment::class.java.simpleName + "]"

        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
    }

    interface Callback {
        fun onGetImage(image: Bitmap, scanResult: ArrayList<ScanResult>)
        fun onAutoFlashChanged(isAutoFlashEnabled: Boolean)
        fun onOpenDrawer()
    }

    private lateinit var mCameraPreview: CameraView
    private lateinit var mSettingsButton: AppCompatImageButton
    private lateinit var mGetImageButton: Button
    private lateinit var mFotoapparat: Fotoapparat
    private lateinit var mImageProcessor: ImageProcessor

    private var isConfigured = true

    //Camera properties
    private var flashMode: Flash = Flash.Off

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.fragment_camera_get_image -> {
                    mFotoapparat.takePicture()
                        .toBitmap()
                        .whenAvailable { bitmapPhoto ->
                            if (bitmapPhoto != null) {
                                val matrix = Matrix()
                                matrix.postRotate(90f)
                                val scaledBitmap = Bitmap.createScaledBitmap(
                                    bitmapPhoto.bitmap,
                                    bitmapPhoto.bitmap.width,
                                    bitmapPhoto.bitmap.height,
                                    true
                                )
                                val rotatedBitmap = Bitmap.createBitmap(
                                    scaledBitmap,
                                    0,
                                    0,
                                    scaledBitmap.width,
                                    scaledBitmap.height,
                                    matrix,
                                    true
                                )

                                mImageProcessor.lookForLabels(rotatedBitmap)
                            }
                        }
                }

                R.id.fragment_camera_settings -> {
                    if (mCallback != null) {
                        mCallback!!.onOpenDrawer()
                    }
                }
            }
        }
    }

    override fun getImageLabels(image: Bitmap, result: ArrayList<com.papayainc.findit.model.ScanResult>) {
        if (mCallback != null)
            mCallback!!.onGetImage(image, result)
    }

    override fun onStart() {
        super.onStart()

        requestPermissions()
    }

    override fun onResume() {
        super.onResume()
        try {
            if (!isConfigured){
                configureInstances()
            }
            mFotoapparat.start()
        } catch (error: Exception) {
            Log.e(TAG, error.message)
        }
    }

    override fun onPause() {
        super.onPause()
        mFotoapparat.stop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCameraPreview = view.findViewById(R.id.fragment_camera_preview)
        mSettingsButton = view.findViewById(R.id.fragment_camera_settings)
        mGetImageButton = view.findViewById(R.id.fragment_camera_get_image)

        mSettingsButton.setOnClickListener(this)
        mGetImageButton.setOnClickListener(this)

        mImageProcessor = ImageProcessor.newInstance()
        mImageProcessor.setCallback(this)

        configureInstances()
    }

    private var mCallback: Callback? = null

    fun setCallback(callback: Callback) {
        mCallback = callback
    }

    fun clearCallback() {
        if (mCallback != null) {
            mCallback = null
        }
    }

    private fun configureInstances() {
        val userSettings = try {
            SharedPrefsUtils.unpackUserSettings()
        } catch (error: Exception) {
            if (context is Activity) {
                SharedPrefsUtils.initSharedPrefs(context as Activity)
            }

            SharedPrefsUtils.unpackUserSettings()
        }

        flashMode = if (userSettings.isAutoFlashEnabled) Flash.Auto else Flash.Off

        mFotoapparat = Fotoapparat(
            context = context!!,
            view = mCameraPreview,
            scaleType = ScaleType.CenterCrop,
            lensPosition = back(),
            cameraConfiguration = CameraConfiguration(
                if (userSettings.isAutoFlashEnabled) autoFlash() else off()
            ),
            cameraErrorCallback = { error ->
                isConfigured = false
                Log.e(TAG, error.message)
            }
        )

        if (mCallback != null) {
            mCallback!!.onAutoFlashChanged(flashMode == Flash.Auto)
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission()
            return
        }
    }

    private fun requestCameraPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            0
        )
    }

    fun switchAutoFlash() {
        mFotoapparat.updateConfiguration(
            UpdateConfiguration(flashMode = if (flashMode == Flash.Off) autoFlash() else off())
        )

        flashMode = if (flashMode == Flash.Off) Flash.Auto else Flash.Off
        if (mCallback != null) {
            mCallback!!.onAutoFlashChanged(flashMode == Flash.Auto)
        }
    }
}