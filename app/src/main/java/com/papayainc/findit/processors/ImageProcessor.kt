package com.papayainc.findit.processors

import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions


class ImageProcessor {
    companion object {
        fun newInstance(): ImageProcessor{
            return ImageProcessor()
        }
    }

    interface Callback{
        fun getImageLabels()
    }

    private val options = FirebaseVisionCloudDetectorOptions.Builder()
        .setModelType(FirebaseVisionCloudDetectorOptions.STABLE_MODEL)
        .setMaxResults(10)
        .build()

    private var detector = FirebaseVision.getInstance().getVisionCloudLabelDetector(options)
    private var isDetectorBusy = false

    fun lookForLabels(bitmap: Bitmap){
        if (!isDetectorBusy){
            isDetectorBusy = true
            val firebaseImage = FirebaseVisionImage.fromBitmap(bitmap)
            detector.detectInImage(firebaseImage).addOnSuccessListener { it ->
                it.forEach {
                    Log.d("dbg", it.label)
                    Log.d("dbg", it.confidence.toString())
                }
                isDetectorBusy = false
            }.addOnFailureListener {
                Log.e("dbg", it.message)
                isDetectorBusy = false
            }
        }
    }
}