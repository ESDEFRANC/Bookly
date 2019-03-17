package com.altemir.adria.bookly.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.altemir.adria.bookly.Model.Book
import com.altemir.adria.bookly.R
import kotlinx.android.synthetic.main.activity_custom_book.view.*

class customBook(
        private val context: Context,
        private val books: ArrayList<Book>
):BaseAdapter(){
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val mInflater: LayoutInflater = LayoutInflater.from(context)
        val layoutInflater = LayoutInflater.from(context)
        val rowMain:View
        rowMain = convertView ?: mInflater.inflate(R.layout.activity_custom_book, parent, false)

            rowMain.NameBook.text = books[position].title
            rowMain.EditorialEdit.text = books[position].editorial

        return rowMain

    }
    override fun getItem(position: Int): Any {
    return books[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
    return books.size
    }


}
