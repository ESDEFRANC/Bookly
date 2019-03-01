package com.altemir.adria.bookly

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.altemir.adria.bookly.Adapter.customBook
import com.altemir.adria.bookly.Model.Book
import com.altemir.adria.bookly.Model.Shelf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_create_books.*

class BooksActivity : AppCompatActivity() {

    val books = arrayListOf<Book>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_books)

        val shelf = intent.getParcelableExtra<Shelf>("shelfUID");
        val drawerUID = shelf.uid

        getBooks(books,drawerUID)


        ButtonCreateBook.setOnClickListener(){
            val intent = Intent(this, AddBook::class.java)
            intent.putExtra("shelfUid",shelf);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
        }
    }


        private fun getBooks(books:ArrayList<Book>,shelfUID:String){
            val ref = FirebaseDatabase.getInstance().getReference("Books")
            ref.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        books.clear()
                        for (e in p0.children){
                            val book = e.getValue(Book::class.java)
                            if (book != null) {
                                if(shelfUID.equals(book.uidShelf)){
                                    books.add(book)
                                }
                            }
                        }
                        val adapter = customBook(this@BooksActivity, books)
                        listBooks.adapter = adapter
                    }
                }

            });
        }
}

