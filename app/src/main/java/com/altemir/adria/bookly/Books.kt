package com.altemir.adria.bookly

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.altemir.adria.bookly.Adapter.customBook
import com.altemir.adria.bookly.Model.Book
import com.altemir.adria.bookly.Model.Drawer
import com.altemir.adria.bookly.Model.Shelf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_books.*
import kotlinx.android.synthetic.main.activity_create_shelf.*
import java.util.*


class Books : AppCompatActivity() {

    val books = arrayListOf<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)

        val shelf = intent.getParcelableExtra<Shelf>("shelfUID");
        val shelfUid = shelf.uid

        getBooks(books,shelfUid)

        ButtonCreateBook.setOnClickListener(){
            val intent = Intent(this, AddBook::class.java)
            intent.putExtra("shelfUid", shelf);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        R.id.signout
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

private fun getBooks(books:ArrayList<Book>, shelfuid:String){
    val ref = FirebaseDatabase.getInstance().getReference("Book")
    ref.addValueEventListener(object: ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            if(p0.exists()){
                books.clear()
                for (e in p0.children){
                    val book = e.getValue(Book::class.java)
                    if (book != null) {
                        if(shelfuid.equals(book.uidShelf)){

                            books.add(book)
                        }
                    }

                }
                val adapter = customBook(this@Books, books)
                listBooks.adapter = adapter
            }
        }

        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    });

}
}
