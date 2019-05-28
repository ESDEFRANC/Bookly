package com.altemir.adria.bookly

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
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
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_find_books.*


class FindBooksActivity : AppCompatActivity() {
    var scannedResult: String = ""
    val books = arrayListOf<Book>()
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
            getBooks(books,isbnFind.text.toString())
        }

        listBooks.setOnItemClickListener { _, _, position, _ ->
            listBooks.adapter
            val intent = Intent(this, FindBookClicked::class.java)
            intent.putExtra("Book", listBooks.getItemAtPosition(position) as Book);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
        }
    }

    private fun getBooks(booksArray:ArrayList<Book>,isbn:String){
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    booksArray.clear()
                    for (e in p0.children){
                        val book = e.getValue(Book::class.java)
                        if (book != null) {
                                if (isbn == book.isbn || book.title.contains(isbn)) {
                                    getDrawers(booksArray,book)
                            }else{
                                    emptyTextView()
                                }
                        }
                    }

                }
                val adapter = customBook(this@FindBooksActivity, books)
                listBooks.adapter = adapter
            }

        });
    }
    private fun getShelfs(booksArray:ArrayList<Book>,book:Book,drawer:Drawer){
        val ref = FirebaseDatabase.getInstance().getReference("Shelf")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for (e in p0.children) {
                        val shelf = e.getValue(Shelf::class.java)
                            if (book.uidShelf == shelf!!.uid) {
                                setTextView()
                                booksArray.add(book)

                            }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        });

    }
    private fun getDrawers(booksArray: ArrayList<Book>,book:Book){
        val ref = FirebaseDatabase.getInstance().getReference("Drawers")
        val user = FirebaseAuth.getInstance().currentUser?.uid
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (e in p0.children) {
                        val drawer = e.getValue(Drawer::class.java)
                        if (drawer!!.uidUser == user) {
                            if (book.uidDrawer == drawer.uid) {
                                getShelfs(booksArray,book,drawer)
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
        noResults.text = getString(R.string.NingunLibro)
    }

    private fun setTextView(){
        ubicacion.text = ""
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
