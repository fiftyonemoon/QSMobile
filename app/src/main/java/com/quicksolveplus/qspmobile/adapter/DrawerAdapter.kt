package com.quicksolveplus.qspmobile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.quicksolveplus.qspmobile.R

class DrawerAdapter(private val context: Context, private val actionsList: ArrayList<String>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return actionsList.size
    }

    override fun getItem(position: Int): Any {
        return actionsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context)
                .inflate(R.layout.layout_item_drawer, parent, false)
        }
        val currentItem = actionsList[position]
        val tvDrawer = view!!.findViewById(R.id.tvDrawer) as TextView

        tvDrawer.text = currentItem
        return view
    }

}