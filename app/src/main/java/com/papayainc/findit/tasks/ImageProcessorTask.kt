package com.papayainc.findit.tasks

import android.media.Image
import android.os.AsyncTask

class ImageProcessorTask : AsyncTask<Image, Int, String>() {
    var isTaskRunning = false

    override fun doInBackground(vararg image: Image): String? {
        return ""
    }
}