package com.altemir.adria.bookly

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.altemir.adria.bookly.Model.Book
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_book.view.*
import kotlinx.android.synthetic.main.activity_update_book.*
import java.util.regex.Pattern

class updateBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_book)

        val book = intent.getParcelableExtra<Book>("book")

        ISBNUpdateTextView.text = book.isbn
        TitolUpdate.setText(book.title)
        EditorialUpdate.setText(book.editorial)
        AutorUpdate.setText(book.autor)
        ratingBarUpdate.rating = book.stars.toFloat()


        Update.setOnClickListener(){
            updateBook(book)
        }



    }

    private fun updateBook(book:Book) {


        val ref = FirebaseDatabase.getInstance().getReference("/Books/${book.uid}")
        val user = FirebaseAuth.getInstance().currentUser!!.uid
        if (checkFields()) {
                    if (checkAutor(AutorUpdate.text.toString())) {
                        if (checkFormat(EditorialUpdate.text.toString())) {
                            if (checkFormat(TitolUpdate.text.toString())) {
                                val bookInsert = Book(ISBNUpdateTextView.text.toString(), AutorUpdate.text.toString(), EditorialUpdate.text.toString(), TitolUpdate.text.toString(), ratingBarUpdate.rating.toDouble(), book.uid, book.uidShelf, book.uidDrawer, user)
                                ref.setValue(bookInsert)
                                finish()
                            } else {
                                Toast.makeText(this, getString(R.string.TituloMalIntroducido), Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this, getString(R.string.EditorialMalIntroducida), Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.AutorMalIntroducido), Toast.LENGTH_LONG).show()
                    }
        }
    }

    private fun checkFields():Boolean{
        return if (!TitolUpdate.text.toString().isEmpty()&&
                !EditorialUpdate.text.toString().isEmpty()&&
                !AutorUpdate.text.toString().isEmpty()) {
            true
        } else {
            Toast.makeText(this, getString(R.string.IntroduzcaLosCampos), Toast.LENGTH_LONG).show()
            false
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
    private fun checkAutor(autorName:String):Boolean{
        val regex = "^[a-zA-Z ]+$"
        val p = Pattern.compile(regex)
        val autorTrimedd = autorName.trim()
        val m = p.matcher(autorTrimedd)
        val b = m.matches()
        return b
    }

}
