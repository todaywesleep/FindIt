package com.papayainc.findit.modal

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.papayainc.findit.R
import com.papayainc.findit.model.Quest
import com.papayainc.findit.model.ScanResult
import com.papayainc.findit.utils.FireBaseDataBaseWorker
import kotlin.math.roundToInt

class ScanResultModal(context: Context) : BaseModal(context) {
    private lateinit var takenImage: ImageView
    private lateinit var dismissButton: Button

    private lateinit var contentContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.modal_scan_result)

        takenImage = findViewById(R.id.modal_scan_result_preview)
        dismissButton = findViewById(R.id.modal_scan_result_dismiss)
        contentContainer = findViewById(R.id.modal_scan_result_pic_info)

        bindListeners()
    }

    private fun bindListeners() {
        this.dismissButton.setOnClickListener {
            clearModalData()
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    fun setScanResult(image: Bitmap, scanResult: ArrayList<ScanResult>){
        takenImage.setImageBitmap(image)

        FireBaseDataBaseWorker.getCompletedQuest(scanResult.map {
            it.label
        } as ArrayList<String>, object : FireBaseDataBaseWorker.Companion.IsQuestCompletedCallback {
            override fun isQuestCompleted(isCompleted: Boolean, quests: ArrayList<Quest>?) {
                Log.d("dbg", isCompleted.toString())

                quests?.forEach {
                    Log.d("dbg", it.item_to_search)
                }
            }
        })

        scanResult.forEach { item ->
            val textView = TextView(context)
            textView.text = item.label + " - " + (item.percentage * 100).roundToInt().toString() + "%"
            contentContainer.addView(textView)
        }
    }

    private fun clearModalData(){
        takenImage.setImageResource(0)
        contentContainer.removeAllViews()
    }
}