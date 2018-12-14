package com.papayainc.findit.processors

import android.media.Image
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions
import com.google.firebase.ml.vision.FirebaseVision


class ImageProcessor {
    companion object {
        fun newInstance(): ImageProcessor{
            return ImageProcessor()
        }
    }

    interface Callback{
        fun getImageLabels()
    }

    private val options = FirebaseVisionLabelDetectorOptions.Builder()
        .setConfidenceThreshold(0.8f)
        .build()!!
    private var detector = FirebaseVision.getInstance().getVisionLabelDetector(options)
    private var isDetectorBusy = false

    fun lookForLabels(image: Image, rotation: Int){
        if (!isDetectorBusy){
            isDetectorBusy = true
            val firebaseImage = FirebaseVisionImage.fromMediaImage(image, rotation)
            detector.detectInImage(firebaseImage).addOnSuccessListener { it ->
                it.forEach {
                    Log.d("dbg", it.label)
                }
                isDetectorBusy = false
            }.addOnFailureListener {
                Log.e("dbg", it.message)
                isDetectorBusy = false
            }
        }
    }
}