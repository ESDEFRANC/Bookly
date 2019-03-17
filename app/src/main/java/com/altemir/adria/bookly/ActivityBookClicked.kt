package com.altemir.adria.bookly

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.altemir.adria.bookly.Model.Book
import kotlinx.android.synthetic.main.activity_book_clicked.*

class ActivityBookClicked : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_clicked)

        val book = intent.getParcelableExtra<Book>("Book");

        BookTitle.text = book.editorial.toString()
        EditorialUpdate.text = book.uid.toString()
        Description.text = book.title.toString()




        //Toast.makeText(this,"Book"+book.title,Toast.LENGTH_LONG).show()

    }
}
