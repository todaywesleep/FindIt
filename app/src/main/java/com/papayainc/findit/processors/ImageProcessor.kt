package com.papayainc.findit.processors

import android.graphics.Bitmap
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.papayainc.findit.model.ScanResult
import com.papayainc.findit.utils.FireBaseDataBaseWorker

class ImageProcessor {
    companion object {
        fun newInstance(): ImageProcessor {
            return ImageProcessor()
        }
    }

    interface Callback {
        fun getImageLabels(image: Bitmap, result: ArrayList<ScanResult>)
    }

    private var mCallback: Callback? = null
    private val options = FirebaseVisionCloudDetectorOptions.Builder()
        .setModelType(FirebaseVisionCloudDetectorOptions.STABLE_MODEL)
        .setMaxResults(10)
        .build()

    private var detector = FirebaseVision.getInstance().getVisionCloudLabelDetector(options)
    private var isDetectorBusy = false

    fun setCallback(callback: Callback) {
        mCallback = callback
    }

    fun lookForLabels(bitmap: Bitmap) {
        if (!isDetectorBusy) {
            isDetectorBusy = true
            val firebaseImage = FirebaseVisionImage.fromBitmap(bitmap)

            detector.detectInImage(firebaseImage).addOnSuccessListener { cloudLabel ->
                val scanResult = arrayListOf<ScanResult>()
                val labelsCollection = hashMapOf<String, String>()

                cloudLabel.forEach {
                    scanResult.add(ScanResult(it.label.toLowerCase(), it.confidence))
                    labelsCollection[it.label.toLowerCase()] = it.label.toLowerCase()
                }

                FireBaseDataBaseWorker.writeLabels(labelsCollection)
                if (mCallback != null)
                    mCallback!!.getImageLabels(bitmap, scanResult)

                isDetectorBusy = false
            }.addOnFailureListener {
                isDetectorBusy = false
            }
        }
    }
}