package com.altemir.adria.bookly

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.altemir.adria.bookly.Model.Book
import com.altemir.adria.bookly.Model.Shelf
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_book.*
import java.util.*

class AddBook : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        val shelf = intent.getParcelableExtra<Shelf>("shelfUid");

        createBook(shelf)
    }

    private fun createBook(shelf:Shelf) {
        val bookID = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Books/$bookID")
        val inflater = layoutInflater
        Add.setOnClickListener() {
            if (!TitolUpdate.text.toString().isEmpty()&&!DescripcioUpdate.text.toString().isEmpty() && !EditorialUpdate.text.toString().isEmpty()) {
                val book = Book(DescripcioUpdate.text.toString(),EditorialUpdate.text.toString(),TitolUpdate.text.toString(),bookID,shelf.uid,shelf.uidDrawer)
                ref.setValue(book)
                finish()
            } else {
                Toast.makeText(this, "Porfavor introduzca los campos", Toast.LENGTH_LONG).show()
            }

        }

    }
}
