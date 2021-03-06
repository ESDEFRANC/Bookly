package com.altemir.adria.bookly

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.altemir.adria.bookly.Adapter.customShelf
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
import kotlinx.android.synthetic.main.activity_add_book.*
import kotlinx.android.synthetic.main.activity_custom_shelf.view.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class AddBook : AppCompatActivity() {
    var scannedResult: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {

        var booksISBN = arrayListOf<String>()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        internetConnected()
        val shelf = intent.getParcelableExtra<Shelf>("shelfUid");
        getBooks(booksISBN)
        createBook(shelf, booksISBN)
    }

    private fun createBook(shelf: Shelf, booksISBN: ArrayList<String>) {
        val bookID = UUID.randomUUID().toString()
        val user = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/Books/$bookID")
        val refShelf = FirebaseDatabase.getInstance().getReference("/Shelf/${shelf.uid}")

        ButtonCamera.setOnClickListener(){
            run {
                IntentIntegrator(this).initiateScan()
            }
        }

        Add.setOnClickListener() {
            if (checkFields()) {
                if (isbnSize(ISBNAdd.text.toString())) {
                    if (!booksISBN.contains(ISBNAdd.text.toString())) {
                        if (checkAutor(AutorAdd.text.toString())) {
                            if (checkFormat(EditorialAdd.text.toString())) {
                                if (checkFormat(TitolAdd.text.toString())) {
                                    val book = Book(ISBNAdd.text.toString(), AutorAdd.text.toString(), EditorialAdd.text.toString(), TitolAdd.text.toString(), ratingBar.rating.toDouble(), bookID, shelf.uid, shelf.uidDrawer,user)
                                    ref.setValue(book)
                                    val shelf = Shelf(shelf.uid,shelf.uidDrawer,shelf.name,1)
                                    refShelf.setValue(shelf)
                                    finish()
                                } else {
                                    Toast.makeText(this, getString(R.string.TituloMalIntroducido), Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(this, getString(R.string.EditorialMalIntroducido), Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this, getString(R.string.AutorMalIntroducido), Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.IsbnRepetido), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, getString(R.string.IsbnMalIntroducido), Toast.LENGTH_LONG).show()
                }

            }


        }
    }

    private fun getBooks(booksISBN: ArrayList<String>) {
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        val user = FirebaseAuth.getInstance().currentUser?.uid
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    booksISBN.clear()
                    for (e in p0.children) {
                        val book = e.getValue(Book::class.java)
                        if (book != null) {
                            if (user == book.uidUser) {
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

    private fun checkFields(): Boolean {
        return if (!ISBNAdd.text.isEmpty() &&
                !AutorAdd.text.toString().isEmpty() &&
                !EditorialAdd.text.toString().isEmpty() &&
                !TitolAdd.text.toString().isEmpty()) {
            true
        } else {
            Toast.makeText(this, getString(R.string.IntroduzcaCampos), Toast.LENGTH_LONG).show()
            false
        }
    }

    private fun checkISBN(bookISBN: String): Boolean {
        val regex = "^(?=[0-9X]{10}\$|(?=(?:[0-9]){3})[0-9X]{13}\$|97[89][0-9]{10}\$|(?=(?:[0-9]){4})[0-9]{17}\$)(?:97[89])?[0-9]{1,5}[0-9]?[0-9]+[0-9X]\$"
        val p = Pattern.compile(regex)
        val drawertrimed = bookISBN.trim()
        val m = p.matcher(drawertrimed)
        val b = m.matches()
        return b
    }

    private fun isValidISBN13(isbn: String): Boolean {

        var result = false
        if (checkISBN(isbn)) {
            val sum: Int

            var i = 0
            sum = isbn.sumBy { c -> (c.toInt() - '0'.toInt()) * if (i++.isOdd()) 3 else 1 }

            result = sum % 10 == 0
        }

        return result
    }

    private fun Int.isOdd(): Boolean {

        return this % 2 != 0
    }

    fun isValidISBN10(isbn: String?): Boolean {
        val isbn: String? = isbn ?: return false

        try {
            if (isbn!!.length < 10) {
                return false
            } else {
                var tot = 0
                for (i in 0..8) {
                    val digit = Integer.parseInt(isbn!!.substring(i, i + 1))
                    tot += (10 - i) * digit
                }

                var checksum = Integer.toString((11 - tot % 11) % 11)
                if ("10" == checksum) {
                    checksum = "X"
                }

                return checksum == isbn!!.substring(9)
            }
            } catch (Enfe: NumberFormatException) {

                return false
            }
        }




    private fun checkFormat(name: String): Boolean {
        val regex = "^[a-zA-Z0-9 ]+$"
        val p = Pattern.compile(regex)
        val nametrimedd = name.trim()
        val m = p.matcher(nametrimedd)
        val b = m.matches()
        return b
    }

    private fun checkAutor(autorName: String): Boolean {
        val regex = "^[a-zA-Z ]+$"
        val p = Pattern.compile(regex)
        val autorTrimedd = autorName.trim()
        val m = p.matcher(autorTrimedd)
        val b = m.matches()
        return b
    }

    private fun isbnSize(ISBNAdd: String): Boolean = if (ISBNAdd.length >= 11) {
        isValidISBN13(ISBNAdd)
    } else {
        isValidISBN10(ISBNAdd)
    }
    private fun internetConnected(){
        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = cm.activeNetworkInfo

        if(networkInfo == null){
            Toast.makeText(baseContext,getString(R.string.Nointernet),Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){

            if(result.contents != null){
                scannedResult = result.contents
                ISBNAdd.setText(scannedResult, TextView.BufferType.EDITABLE)
            } else {

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {

        outState?.putString("scannedResult", scannedResult)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState?.let {
            scannedResult = it.getString("scannedResult")
            ISBNAdd.setText(scannedResult, TextView.BufferType.EDITABLE)
        }
    }

}

