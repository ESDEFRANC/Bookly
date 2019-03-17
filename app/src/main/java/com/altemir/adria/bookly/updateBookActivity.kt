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
        TitolUpdate.setText(book.editorial)
        EditorialUpdate.setText(book.uid)
        DescripcioUpdate.setText(book.title)
        Update.setOnClickListener(){
            updateBook(book)
        }



    }

    private fun updateBook(book:Book){


        val ref = FirebaseDatabase.getInstance().getReference("/Books/${book.description}")
        if (!TitolUpdate.text.toString().isEmpty()&&!DescripcioUpdate.text.toString().isEmpty() && !EditorialUpdate.text.toString().isEmpty()) {
            val book = Book(DescripcioUpdate.text.toString(),EditorialUpdate.text.toString(),TitolUpdate.text.toString(),book.description,book.uidShelf,book.uidDrawer)
            ref.setValue(book)
            finish()
        } else {
            Toast.makeText(this, "Porfavor introduzca los campos", Toast.LENGTH_LONG).show()
        }

    }
}
