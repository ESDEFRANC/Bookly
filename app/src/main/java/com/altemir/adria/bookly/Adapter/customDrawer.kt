package com.altemir.adria.bookly.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.altemir.adria.bookly.Model.Drawer
import com.altemir.adria.bookly.R
import kotlinx.android.synthetic.main.activity_custom_drawer.view.*
import java.util.zip.Inflater

class customDrawer(
        private val context: Context,
        private val drawers: ArrayList<Drawer>
):BaseAdapter(){
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val mInflater: LayoutInflater = LayoutInflater.from(context)
        val layoutInflater = LayoutInflater.from(context)
        val rowMain = layoutInflater.inflate(R.layout.activity_custom_drawer, parent, false)
        val view: View
        if(convertView == null){
            view = mInflater.inflate(R.layout.activity_custom_drawer, parent, false)

        }else{
            rowMain.Name.text = drawers[position].name
        }

        return rowMain

    }
    override fun getItem(position: Int): Any {
    return drawers[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
    return drawers.size
    }


}
