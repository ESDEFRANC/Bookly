package com.altemir.adria.bookly

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.altemir.adria.bookly.Model.Book
import com.altemir.adria.bookly.Model.Drawer
import com.altemir.adria.bookly.Model.Shelf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_find_book_clicked.*


class FindBookClicked : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_book_clicked)
        val book = intent.getParcelableExtra<Book>("Book");
        internetConnected()
        getBook(book.isbn)


    }

    private fun getBook(isbn:String){
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

                            }
                        }
                    }

                }
            }

        });
    }
    private fun getShelfs(book: Book, drawer: Drawer){
        val ref = FirebaseDatabase.getInstance().getReference("Shelf")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for (e in p0.children) {
                        val shelf = e.getValue(Shelf::class.java)
                        if (book.uidShelf == shelf!!.uid) {
                            BiblioNom.text=(drawer.name)
                            CalaixNom.text=(shelf.name)
                            TitolNom.text = book.title
                            AutorNom.text = book.autor
                            EditorialNom.text = book.editorial
                            setTextView()

                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        });

    }
    private fun getDrawers(book: Book){
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
                            }

                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        });


    }

    private fun setTextView(){
        Autor.text = getString(R.string.autor)
        Biblio.text = getString(R.string.BibliotecaRuta)
        Calaix.text = getString(R.string.CajonRuta)
        Titol.text = getString(R.string.titol)
        Editorial.text = getString(R.string.editorial)
        Ubicacio.text = getString(R.string.location)
    }
    private fun internetConnected(){
        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = cm.activeNetworkInfo

        if(networkInfo == null){
            Toast.makeText(baseContext,getString(R.string.Nointernet), Toast.LENGTH_LONG).show()
        }
    }

}
