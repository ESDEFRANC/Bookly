package com.altemir.adria.bookly

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
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
import kotlinx.android.synthetic.main.activity_create_shelf.*
import java.util.UUID.randomUUID
import java.util.regex.Pattern

class ShelfsActivity : AppCompatActivity() {

    val shelfs = arrayListOf<Shelf>()
    val shelfsName = arrayListOf<String>()
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_shelf)
        val drawer = intent.getParcelableExtra<Drawer>("drawerUID");
        val drawerUID = drawer.uid
        this.title = drawer.name

        internetConnected()
        getShelfs(shelfs,drawerUID)

        ButtonCreateShelf.setOnClickListener(){
            createCalaix(drawerUID)
        }
        gridShelf.setOnItemClickListener { _, _, position, _ ->
            gridShelf.adapter
            val intent = Intent(this, BooksActivity::class.java)
            intent.putExtra("shelfUID", gridShelf.getItemAtPosition(position) as Shelf);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
        }
        gridShelf.setOnItemLongClickListener { parent, view, position, id ->
            val inflater = layoutInflater
            gridShelf.adapter

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

    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.signout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.find -> {
                val intent = Intent(this, FindBooksActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent)
            }
        }
            return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    private fun createCalaix(draweruid:String) {
        val shelf = randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Shelf/$shelf")
        val inflater = layoutInflater

        val myBuild = AlertDialog.Builder(this)
        val dialoglayout = inflater.inflate(R.layout.activity_add_shelf, null)
        val calaixName = dialoglayout.findViewById<EditText>(R.id.ShelfName)
        val add = dialoglayout.findViewById<Button>(R.id.btnAdd)
        val cancel = dialoglayout.findViewById<Button>(R.id.btnCancelar)
        myBuild.setView(dialoglayout)
        val dialog = myBuild.create()
        dialog.show()
        add.setOnClickListener() {
            if (checkName(calaixName.text.toString())) {
                if(!shelfsName.contains(calaixName.text.toString())){
                    val shelf1 = Shelf(shelf, draweruid, calaixName.text.toString(),0)
                    ref.setValue(shelf1)
                    dialog.dismiss()
                }else{
                    Toast.makeText(this, getString(R.string.NombreRepetido), Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, getString(R.string.NombreValido), Toast.LENGTH_LONG).show()
            }
        }
        cancel.setOnClickListener() {
            dialog.dismiss()
        }

    }
    private fun getShelfs(shelfs:ArrayList<Shelf>,draweruid:String){
        val ref = FirebaseDatabase.getInstance().getReference("Shelf")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    shelfs.clear()
                    for (e in p0.children){
                        val shelf = e.getValue(Shelf::class.java)
                        if (shelf != null) {
                            if(draweruid.equals(shelf.uidDrawer)){
                                shelfs.add(shelf)
                                getBooksCount(shelf.uid)
                                shelfsName.add(shelf.name)
                            }
                        }

                    }
                    val adapter = customShelf(this@ShelfsActivity, shelfs)
                    gridShelf.adapter = adapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        });

    }
    private fun updateBtn(position:Int){
        val inflater = layoutInflater
        gridShelf.adapter

        val myBuild = AlertDialog.Builder(this@ShelfsActivity)
        val dialoglayout = inflater.inflate(R.layout.activity_update_shelf, null)
        val shelfName = dialoglayout.findViewById<EditText>(R.id.ShelfName)
        val update = dialoglayout.findViewById<Button>(R.id.btnUpdate)
        val cancel = dialoglayout.findViewById<Button>(R.id.btnCancelar)

        myBuild.setView(dialoglayout)
        val dialog = myBuild.create()
        dialog.show()

        val grid = gridShelf.getItemAtPosition(position) as Shelf
        val ref = FirebaseDatabase.getInstance().getReference("/Shelf/${grid.uid}")

        update.setOnClickListener() {
            if (checkName(shelfName.text.toString())) {
                if(!shelfsName.contains(shelfName.text.toString())) {
                    val shelf = Shelf(grid.uid, grid.uidDrawer, shelfName.text.toString(),0)
                    ref.setValue(shelf)
                    dialog.dismiss()
                }else{
                    Toast.makeText(this, getString(R.string.NombreRepetido), Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, getString(R.string.NombreValido), Toast.LENGTH_LONG).show()
            }
        }
        cancel.setOnClickListener() {
            dialog.dismiss()
        }

        true
    }
    private fun deleteBtn(position:Int){
        val inflater = layoutInflater
        gridShelf.adapter

        val myBuild = AlertDialog.Builder(this@ShelfsActivity)
        val dialoglayout = inflater.inflate(R.layout.activity_delete_shelf, null)
        val borrar = dialoglayout.findViewById<Button>(R.id.btnBorrar)
        val cancel = dialoglayout.findViewById<Button>(R.id.btnCancelar)

        myBuild.setView(dialoglayout)
        val dialog = myBuild.create()
        dialog.show()

        val grid = gridShelf.getItemAtPosition(position) as Shelf

        borrar.setOnClickListener() {
            val refShelf = FirebaseDatabase.getInstance().getReference("Shelf").child(grid.uid)
            refShelf.removeValue()
            getBooks(grid.uid)
            Toast.makeText(this, getString(R.string.ElementoBorrado), Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
        cancel.setOnClickListener() {
            dialog.dismiss()
        }

        true
    }
    private fun getBooks(shelfUid:String) {
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (e in p0.children) {
                        val book = e.getValue(Book::class.java)
                        if (book != null) {
                            if (shelfUid.equals(book.uidShelf)) {
                                val refBook = FirebaseDatabase.getInstance().getReference("Books").child(book.uid)
                                refBook.removeValue()
                            }
                        }
                    }

                }
            }

        });
    }
    private fun getBooksCount(shelfUid:String) {
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (e in p0.children) {
                        val book = e.getValue(Book::class.java)
                        if (book != null) {
                            if (shelfUid.equals(book.uidShelf)) {
                                count++
                            }
                        }
                    }

                }
            }

        });
    }
    private fun internetConnected(){
        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = cm.activeNetworkInfo

        if(networkInfo == null){
            Toast.makeText(baseContext,getString(R.string.Nointernet),Toast.LENGTH_LONG).show()
            this.finish()
        }
    }
    private fun checkName(shelfName: String): Boolean {
        val regex = "^[a-zA-Z0-9]+$"
        val p = Pattern.compile(regex)
        val shelftrimed = shelfName.trim()
        val m = p.matcher(shelftrimed)
        val b = m.matches()
        return b
    }
}
