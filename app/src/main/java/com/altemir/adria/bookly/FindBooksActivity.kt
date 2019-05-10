package com.altemir.adria.bookly

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.altemir.adria.bookly.Model.Book
import com.altemir.adria.bookly.Model.Drawer
import com.altemir.adria.bookly.Model.Shelf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_find_books.*


class FindBooksActivity : AppCompatActivity() {
    var scannedResult: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_books)

        buttonCameraFind.setOnClickListener(){
            run {
                IntentIntegrator(this).initiateScan()
            }
        }
        Buscar.setOnClickListener(){
            internetConnected()
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
                                if (isbn == book.isbn || isbn == book.title) {
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
    private fun getShelfs(book:Book,drawer:Drawer){
        val ref = FirebaseDatabase.getInstance().getReference("Shelf")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for (e in p0.children) {
                        val shelf = e.getValue(Shelf::class.java)
                            if (book.uidShelf == shelf!!.uid) {
                                drawerSelected.text=(drawer.name)
                                shelfSelected.text=(shelf.name)
                                drawerSelected.text = drawer.name
                                shelfSelected.text = shelf.name
                                titleSelected.text = book.title
                                autorSelected.text = book.autor
                                editorialSelected.text = book.editorial
                                setTextView()

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
                                getShelfs(book,drawer)
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
    private fun emptyTextView(){
        drawerSelected.text = ""
        shelfSelected.text = ""
        titleSelected.text = ""
        autorSelected.text = ""
        textViewAutorSelected.text = ""
        textViewBiblioSelected.text = ""
        textViewShelfSelected.text = ""
        textViewTitolSelected.text = ""
        noResults.text = getString(R.string.NingunLibro)
        textViewEditorialSelected.text = ""
        editorialSelected.text = ""
        ubicacion.text = ""
    }

    private fun setTextView(){
        textViewAutorSelected.text = getString(R.string.autor)
        textViewEditorialSelected.text = getString(R.string.editorial)
        textViewBiblioSelected.text = getString(R.string.BibliotecaRuta)
        textViewShelfSelected.text = getString(R.string.CajonRuta)
        textViewTitolSelected.text = getString(R.string.titol)
        ubicacion.text = getString(R.string.location)
        noResults.text = ""
    }
    private fun internetConnected(){
        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = cm.activeNetworkInfo

        if(networkInfo == null){
            Toast.makeText(baseContext,getString(R.string.Nointernet), Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){

            if(result.contents != null){
                scannedResult = result.contents
                isbnFind.setText(scannedResult, TextView.BufferType.EDITABLE)
            } else {

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("scannedResult", scannedResult)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState?.let {
            it.getString("scannedResult")?.let { result ->
                scannedResult = result
                isbnFind.setText(scannedResult, TextView.BufferType.EDITABLE)

            }
        }
    }
}
