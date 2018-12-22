package com.papayainc.findit.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.papayainc.findit.R
import com.papayainc.findit.constants.DrawerConstants
import com.papayainc.findit.model.DrawerItem

class SettingsDrawerAdapter(var context: Context) : RecyclerView.Adapter<SettingsDrawerAdapter.ViewHolder>() {
    interface Callback {
        fun onItemSelected(item: DrawerItem)
    }

    companion object {
        const val SETTINGS_DRAWER_AUTO_FLASH = 0
    }

    private var data: ArrayList<DrawerItem> = arrayListOf()
    private var mCallback: Callback? = null

    init {
        this.data.addAll(getDrawerItems())
    }

    fun setCallback(callback: Callback){
        this.mCallback = callback
    }

    fun clearCallback(){
        if (this.mCallback != null){
            mCallback = null
        }
    }

    fun setItemSelection(position: Int, isSelected: Boolean){
        data[position].isItemSelected = isSelected
        notifyItemChanged(position)
    }

    private fun getDrawerItems(): ArrayList<DrawerItem>{
        return arrayListOf(
            DrawerItem(
                context.getString(R.string.drawer_auto_flash),
                R.drawable.ic_flash_auto,
                DrawerConstants.AUTO_FLASH,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.drawer_item, parent, false)

        return ViewHolder(root)
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemButton = holder.itemButton
        val itemData = data[position]

        itemButton.text = itemData.itemName

        if (itemData.imageRecourse != null) {
            itemButton.setIconResource(itemData.imageRecourse!!)
        }

        if (itemData.isItemSelected != null){
            val newBGColor = if (itemData.isItemSelected!!) R.color.enabledButton else R.color.disabledButton
            itemButton.supportBackgroundTintList = ContextCompat.getColorStateList(context, newBGColor)
        }

        itemButton.setOnClickListener(getOnItemClickListener(itemData, position))
    }

    private fun getOnItemClickListener(item: DrawerItem, position: Int): View.OnClickListener {
        return View.OnClickListener {
            if (item.isItemSelected != null){
                item.isItemSelected = !item.isItemSelected!!
                notifyItemChanged(position)
            }

            if (mCallback != null) {
                mCallback!!.onItemSelected(item)
            }
        }
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var itemButton: MaterialButton

        constructor(root: View): super(root){
            itemButton = root.findViewById(R.id.drawer_item_button)
        }
    }
}