package com.altemir.adria.bookly

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.altemir.adria.bookly.Model.Book
import com.altemir.adria.bookly.Model.Shelf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_book.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class AddBook : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        var booksISBN = arrayListOf<String>()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        val shelf = intent.getParcelableExtra<Shelf>("shelfUid");
        getBooks(booksISBN,shelf.uid)
        createBook(shelf,booksISBN)
    }

    private fun createBook(shelf:Shelf, booksISBN:ArrayList<String>) {
        val bookID = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Books/$bookID")
        val inflater = layoutInflater
        Add.setOnClickListener() {
            if(checkFields()){
                if(checkISBN(ISBNAdd.text.toString())){
                    if (!booksISBN.contains(ISBNAdd.text.toString())) {
                        val book = Book(ISBNAdd.text.toString(),AutorAdd.text.toString(),EditorialAdd.text.toString(),TitolAdd.text.toString(),ratingBar.rating.toDouble(),bookID,shelf.uid,shelf.uidDrawer)
                        ref.setValue(book)
                        finish()
                    } else {
                        Toast.makeText(this, "ISBN repetido", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this, "ISBN mal introducido", Toast.LENGTH_LONG).show()
                }

            }

        }

    }
    private fun getBooks(booksISBN: ArrayList<String>, shelfUID:String){
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    booksISBN.clear()
                    for (e in p0.children){
                        val book = e.getValue(Book::class.java)
                        if (book != null) {
                            if(shelfUID == book.uidShelf){
                                booksISBN.add(book.isbn)
                            }
                        }
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        });
    }

    private fun checkFields():Boolean{
        return if (!ISBNAdd.text.isEmpty() && !AutorAdd.text.toString().isEmpty()&&!EditorialAdd.text.toString().isEmpty() && !TitolAdd.text.toString().isEmpty()) {
            true
        } else {
            Toast.makeText(this, "Porfavor introduzca los campos", Toast.LENGTH_LONG).show()
            false
        }
    }
    private fun checkISBN(bookISBN: String): Boolean {
        val regex = "^[a-zA-Z0-9]+$"
        val p = Pattern.compile(regex)
        val drawertrimed = bookISBN.trim()
        val m = p.matcher(drawertrimed)
        val b = m.matches()
        return b
    }

}
