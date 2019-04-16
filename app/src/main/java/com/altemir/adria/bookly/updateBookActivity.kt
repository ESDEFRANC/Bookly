package com.altemir.adria.bookly

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.altemir.adria.bookly.Model.Book
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_update_book.*
import java.util.regex.Pattern

class updateBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_book)

        val book = intent.getParcelableExtra<Book>("book");
        ISBNUpdate.setText(book.isbn)
        TitolUpdate.setText(book.title)
        BookEditorial.setText(book.editorial)
        AutorUpdate.setText(book.autor)
        ratingBar2.rating = book.stars.toFloat()

        Update.setOnClickListener(){
            updateBook(book)
        }



    }

    private fun updateBook(book:Book){


        val ref = FirebaseDatabase.getInstance().getReference("/Books/${book.uid}")
        if(checkFields()){
            if(isValidISBN13(ISBNUpdate.text.toString())){
                if(checkAutor(AutorUpdate.text.toString())){
                    if(checkFormat(BookEditorial.text.toString())){
                        if(checkFormat(TitolUpdate.text.toString())){
                            val bookInsert = Book(ISBNUpdate.text.toString(),AutorUpdate.text.toString(),BookEditorial.text.toString(),TitolUpdate.text.toString(),ratingBar2.rating.toDouble(),book.uid,book.uidShelf,book.uidDrawer)
                            ref.setValue(bookInsert)
                            finish()
                        }else{
                            Toast.makeText(this, "Nombre del Titulo mal introducido", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(this, "Nombre de Editorial mal introducido", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this, "Nombre de Autor mal introducido", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "ISBN mal introducido", Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun checkFields():Boolean{
        return if (!ISBNUpdate.text.isEmpty() &&
                !TitolUpdate.text.toString().isEmpty()&&
                !BookEditorial.text.toString().isEmpty()&&
                !AutorUpdate.text.toString().isEmpty()) {
            true
        } else {
            Toast.makeText(this, "Porfavor introduzca los campos", Toast.LENGTH_LONG).show()
            false
        }
    }

    private fun checkISBN(bookISBN: String): Boolean {
        val regex = "^(?=[0-9]{13}$|[0-9]{17}$)97[89][0-9]{1,5}[0-9]+[0-9]+[0-9]$"
        val p = Pattern.compile(regex)
        val drawertrimed = bookISBN.trim()
        val m = p.matcher(drawertrimed)
        val b = m.matches()
        return b
    }

    private fun isValidISBN13( isbn: String ): Boolean {

        var result = false
        if ( checkISBN(isbn)) {
            var sum = 0
/*
        for ( i in 0 until isbn.length )
            sum += ( isbn[i].toInt() - '0'.toInt()) * if ( i.isOdd() ) 3 else 1
*/
            // OR...
            var i = 0
            sum = isbn.sumBy { c -> (c.toInt() - '0'.toInt()) * if (i++.isOdd()) 3 else 1 }

            result = sum % 10 == 0
        }

        return result
    }

    private fun Int.isOdd(): Boolean {

        return this % 2 != 0
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
