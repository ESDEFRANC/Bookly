package com.altemir.adria.bookly

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.EditText
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
        val shelfUid = shelf.uid
        createBook(shelfUid)
    }

    private fun createBook(shelfUid:String) {
        val book = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Book/$book")
        val inflater = layoutInflater
        Add.setOnClickListener() {
            if (!Titol.text.toString().isEmpty()&&!Descripcio.text.toString().isEmpty() && !Editorial.text.toString().isEmpty()) {
                val shelf1 = Book(book, shelfUid, Titol.text.toString(),Editorial.text.toString(),Descripcio.text.toString())
                ref.setValue(shelf1)
            } else {
                Toast.makeText(this, "Porfavor introduzca los campos", Toast.LENGTH_LONG).show()
            }
        }

    }
}
