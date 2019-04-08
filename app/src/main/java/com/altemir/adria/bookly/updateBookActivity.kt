package com.altemir.adria.bookly

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.altemir.adria.bookly.Model.Book
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_update_book.*

class updateBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_book)

        val book = intent.getParcelableExtra<Book>("book");
        ISBNUpdate.setText(book.isbn)
        TitolUpdate.setText(book.title)
        BookEditorial.setText(book.editorial)
        AutorUpdate.setText(book.autor)
        ratingBar2.rating = book.stars.toFloat()

        Update.setOnClickListener(){
            updateBook(book)
        }



    }

    private fun updateBook(book:Book){


        val ref = FirebaseDatabase.getInstance().getReference("/Books/${book.uid}")
        if (!ISBNUpdate.text.toString().isEmpty()&&!TitolUpdate.text.toString().isEmpty()&&!BookEditorial.text.toString().isEmpty() && !AutorUpdate.text.toString().isEmpty()) {
            val book = Book(ISBNUpdate.text.toString(),AutorUpdate.text.toString(),BookEditorial.text.toString(),TitolUpdate.text.toString(),ratingBar2.rating.toDouble(),book.uid,book.uidShelf,book.uidDrawer)
            ref.setValue(book)
            finish()
        } else {
            Toast.makeText(this, "Porfavor introduzca los campos", Toast.LENGTH_LONG).show()
        }

    }
}
