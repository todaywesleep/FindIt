package com.papayainc.findit.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.android.material.button.MaterialButton
import com.papayainc.findit.R
import com.papayainc.findit.model.DrawerItem

class DrawerAdapter(var context: Context, items: ArrayList<DrawerItem>) : BaseAdapter() {
    interface Callback {
        fun onItemSelceted(item: DrawerItem)
    }

    private var items: ArrayList<DrawerItem> = arrayListOf()
    private var mCallback: Callback? = null

    init {
        this.items.addAll(items)
    }

    fun setCallback(callback: Callback){
        mCallback = callback
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var changedView = convertView
        val modelItem = items[position]

        if (convertView == null) {
            val mInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            changedView = mInflater.inflate(R.layout.drawer_item, parent, false)
        }

        lateinit var button: MaterialButton
        if (changedView != null){
            button = changedView.findViewById(R.id.drawer_item_button)
            button.text = modelItem.itemName

            if (modelItem.imageRecourse != null){
                button.setIconResource(modelItem.imageRecourse!!)
            }
        }

        button.setOnClickListener(getOnItemClickListener(modelItem))
        return changedView!!
    }

    private fun getOnItemClickListener(forItem: DrawerItem): View.OnClickListener{
        return View.OnClickListener {
            if (mCallback != null){
                mCallback!!.onItemSelceted(forItem)
            }
        }
    }
}