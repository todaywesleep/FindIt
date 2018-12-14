package com.papayainc.findit.processors

import android.media.Image
import android.util.Log
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions
import com.papayainc.findit.tasks.ImageProcessorTask

class ImageProcessor {
    companion object {
        fun newInstance(): ImageProcessor{
            return ImageProcessor()
        }
    }

    private val options = FirebaseVisionLabelDetectorOptions.Builder()
        .setConfidenceThreshold(0.8f)
        .build()!!
    private var processingImage: Image? = null
    private var currentRotation: Int = 0
    private var isProcessorBusy = false
    private val imageProcessorTask: ImageProcessorTask = ImageProcessorTask()

    fun setImage(image: Image, rotation: Int){
        this.processingImage = image
        this.currentRotation = rotation
    }
}