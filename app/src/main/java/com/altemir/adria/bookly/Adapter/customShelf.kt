package com.altemir.adria.bookly.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.altemir.adria.bookly.Model.Book
import com.altemir.adria.bookly.Model.Shelf
import com.altemir.adria.bookly.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_custom_shelf.view.*

class customShelf(
        private val context: Context,
        private val shelf: ArrayList<Shelf>,
        private val books: Int
):BaseAdapter(){
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val mInflater: LayoutInflater = LayoutInflater.from(context)

        val rowMain: View
        rowMain = convertView ?: mInflater.inflate(R.layout.activity_custom_shelf, parent, false)
        if(books>3){
            rowMain.Shelf.setImageResource(R.drawable.shelf_image)
        }else{
            rowMain.Shelf.setImageResource(R.drawable.estante)
        }

        rowMain.NameShelf.text = shelf[position].name

        return rowMain

    }
    override fun getItem(position: Int): Any {
    return shelf[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
    return shelf.size
    }


}
