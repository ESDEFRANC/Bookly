package com.altemir.adria.bookly

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.altemir.adria.bookly.Adapter.customBook
import com.altemir.adria.bookly.Model.Book
import com.altemir.adria.bookly.Model.Shelf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_create_books.*

class BooksActivity : AppCompatActivity() {

    companion object {
        const  val PRODUCT_KEY = "books"
    }

    private val books = arrayListOf<Book>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_books)
        val shelf = intent.getParcelableExtra<Shelf>("shelfUID");
        val shelfUid = shelf.uid
        this.title = shelf.name

        getBooks(books,shelfUid)


        listBooks.setOnItemClickListener { parent, view, position, id ->
            listBooks.adapter
            val intent = Intent(this, ActivityBookClicked::class.java)
            intent.putExtra("Book", listBooks.getItemAtPosition(position) as Book);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
        }


        listBooks.setOnItemLongClickListener { parent, view, position, id ->
            val inflater = layoutInflater
            listBooks.adapter

            val myBuild = AlertDialog.Builder(this)
            val dialoglayout = inflater.inflate(R.layout.activity_delete_update_biblio, null)
            val biblioName = dialoglayout.findViewById<EditText>(R.id.Info)
            val update = dialoglayout.findViewById<Button>(R.id.btnUpdate)
            val delete = dialoglayout.findViewById<Button>(R.id.btnDelete)

            myBuild.setView(dialoglayout)
            val dialog = myBuild.create()
            dialog.show()

            update.setOnClickListener() {
                dialog.dismiss()
                updateBtn(position)

            }
            delete.setOnClickListener() {
                dialog.dismiss()
                deleteBtn(position)
            }


            true
        }

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
                     override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        books.clear()
                        for (e in p0.children){
                            val book = e.getValue(Book::class.java)
                            if (book != null) {
                                if(shelfUID == book.uidShelf){
                                    books.add(book)
                                }
                            }
                        }
                        val adapter = customBook(this@BooksActivity, books)
                        listBooks.adapter = adapter
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            });
        }
    private fun deleteBtn(position:Int){
        val inflater = layoutInflater
        listBooks.adapter

        val myBuild = AlertDialog.Builder(this@BooksActivity)
        val dialoglayout = inflater.inflate(R.layout.activity_delete_shelf, null)
        val borrar = dialoglayout.findViewById<Button>(R.id.btnBorrar)
        val cancel = dialoglayout.findViewById<Button>(R.id.btnCancelar)

        myBuild.setView(dialoglayout)
        val dialog = myBuild.create()
        dialog.show()

        val list = listBooks.getItemAtPosition(position) as Book

        borrar.setOnClickListener() {
            val refBook = FirebaseDatabase.getInstance().getReference("Books").child(list.uid)
            refBook.removeValue()
            Toast.makeText(this, "Elemento borrado correctamente", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
        cancel.setOnClickListener() {
            dialog.dismiss()
        }

        true
    }
    private fun updateBtn(position:Int){
        val inflater = layoutInflater
        listBooks.adapter

        val grid = listBooks.getItemAtPosition(position) as Book
        val ref = FirebaseDatabase.getInstance().getReference("/Books/${grid.uid}")

        val intent = Intent(this, updateBookActivity::class.java)
        intent.putExtra("book",grid);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)

    }
}

