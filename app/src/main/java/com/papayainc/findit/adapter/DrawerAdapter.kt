package com.papayainc.findit.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.papayainc.findit.R

class DrawerAdapter: BaseAdapter {
    var items: ArrayList<String> = arrayListOf()
    var context: Context
    private lateinit var title: TextView

    constructor (context: Context, items: ArrayList<String>) : super() {
        this.context = context
        this.items.addAll(items)
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

        if (convertView == null) {
            val mInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            changedView = mInflater.inflate(R.layout.drawer_item, null)
        }

        title = changedView!!.findViewById(R.id.title)
        title.text = items[position]

        return changedView
    }
}