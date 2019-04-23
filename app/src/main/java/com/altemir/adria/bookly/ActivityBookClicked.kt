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
        this.title = book.title

        BookISBN.text = book.isbn
        BookAutor.text = book.autor
        BookEditorial.text = book.editorial
        BookTitol.text = book.title
        ratingBar3.rating = book.stars.toFloat()
    }
}
