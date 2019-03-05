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
        createBook(shelf)
    }

    private fun createBook(shelf:Shelf) {
        val bookID = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Books/$bookID")
        val inflater = layoutInflater
        Add.setOnClickListener() {
            if (!Titol.text.toString().isEmpty()&&!Descripcio.text.toString().isEmpty() && !Editorial.text.toString().isEmpty()) {
                val book = Book(Descripcio.text.toString(),Editorial.text.toString(),Titol.text.toString(),bookID,shelf.uid,shelf.uidDrawer)
                ref.setValue(book)
            } else {
                Toast.makeText(this, "Porfavor introduzca los campos", Toast.LENGTH_LONG).show()
            }
        }

    }
}
