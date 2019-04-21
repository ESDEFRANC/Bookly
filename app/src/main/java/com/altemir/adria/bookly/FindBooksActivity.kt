package com.altemir.adria.bookly

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.altemir.adria.bookly.Model.Book
import com.altemir.adria.bookly.Model.Drawer
import com.altemir.adria.bookly.Model.Shelf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_find_books.*
import android.text.style.UnderlineSpan
import android.text.SpannableString





class FindBooksActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_books)

        Buscar.setOnClickListener(){
            getBooks(isbnFind.text.toString())
        }
    }

    private fun getBooks(isbn:String){
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for (e in p0.children){
                        val book = e.getValue(Book::class.java)
                        if (book != null) {
                                if (isbn == book.isbn) {
                                    getDrawers(book)
                            }else{
                                    emptyTextView()
                                }
                        }
                    }

                }
            }

        });
    }
    private fun getShelfs(shelfUID:String){
        val ref = FirebaseDatabase.getInstance().getReference("Shelf")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for (e in p0.children) {
                        val shelf = e.getValue(Shelf::class.java)
                            if (shelfUID == shelf!!.uid) {
                                shelfSelected.text = shelf.name
                            }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        });

    }
    private fun getDrawers(book:Book){
        val ref = FirebaseDatabase.getInstance().getReference("Drawers")
        val user = FirebaseAuth.getInstance().currentUser?.uid
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (e in p0.children) {
                        val drawer = e.getValue(Drawer::class.java)
                        if (drawer!!.uidUser == user) {
                            if (book.uidDrawer == drawer.uid) {
                                drawerSelected.text = drawer.name
                                getShelfs(book.uidShelf)
                                titleSelected.text = book.title
                                autorSelected.text = book.autor
                                editorialSelected.text = book.editorial
                                textViewAutorSelected.text = "Autor:"
                                textViewEditorialSelected.text = "Editorial:"
                                textViewBiblioSelected.text = "Biblioteca:"
                                textViewShelfSelected.text = "Cajon:"
                                textViewTitolSelected.text = "Tiulo:"
                                ubicacion.text = getString(R.string.location)
                                noResults.text = ""
                            }else{
                                emptyTextView()
                            }

                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        });


    }
    private fun user(){
        Toast.makeText(this,"Libro no encontrado",Toast.LENGTH_LONG).show()
    }
    private fun emptyTextView(){
        drawerSelected.text = ""
        shelfSelected.text = ""
        titleSelected.text = ""
        autorSelected.text = ""
        textViewAutorSelected.text = ""
        textViewBiblioSelected.text = ""
        textViewShelfSelected.text = ""
        textViewTitolSelected.text = ""
        noResults.text = "Ningun libro encontrado"
        textViewEditorialSelected.text = ""
        editorialSelected.text = ""
        ubicacion.text = ""
    }
}
