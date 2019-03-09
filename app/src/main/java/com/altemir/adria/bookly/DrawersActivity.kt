package com.altemir.adria.bookly

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.altemir.adria.bookly.Adapter.customBook
import com.altemir.adria.bookly.Adapter.customDrawer
import com.altemir.adria.bookly.Adapter.customShelf
import com.altemir.adria.bookly.Model.Book
import com.altemir.adria.bookly.Model.Drawer
import com.altemir.adria.bookly.Model.Shelf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_biblio.*
import kotlinx.android.synthetic.main.activity_create_books.*
import kotlinx.android.synthetic.main.activity_create_shelf.*
import java.util.UUID.randomUUID
import java.util.regex.Pattern


class DrawersActivity : AppCompatActivity() {

    val drawers = arrayListOf<Drawer>()
    val drawersName = arrayListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_biblio)
        verifyUserIsLogedIn()
        getDrawers(drawers)


        ButtonCreateBiblio.setOnClickListener() {
            createDrawer()
        }


        grid.setOnItemClickListener { _, _, position, _ ->
            grid.adapter
            val intent = Intent(this, ShelfsActivity::class.java)
            intent.putExtra("drawerUID", grid.getItemAtPosition(position) as Drawer);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
        }

        grid.setOnItemLongClickListener { parent, view, position, id ->
            val inflater = layoutInflater
            grid.adapter

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

    private fun verifyUserIsLogedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
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

    private fun createDrawer() {
        val draweruid = randomUUID().toString()
        val user = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().getReference("/Drawers/$draweruid")
        val inflater = layoutInflater

        val myBuild = AlertDialog.Builder(this)
        val dialoglayout = inflater.inflate(R.layout.activity_add_biblio, null)
        val biblioName = dialoglayout.findViewById<EditText>(R.id.BiblioName)
        val add = dialoglayout.findViewById<Button>(R.id.btnAdd)
        val cancel = dialoglayout.findViewById<Button>(R.id.btnCancelar)
        myBuild.setView(dialoglayout)
        val dialog = myBuild.create()
        dialog.show()
        add.setOnClickListener() {
            if (checkName(biblioName.text.toString())) {
                if (!drawersName.contains(biblioName.text.toString())) {
                    val drawer = Drawer(draweruid, user!!.uid, biblioName.text.toString())
                    ref.setValue(drawer)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Nombre repetido", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Porfavor introduzca un nombre valido", Toast.LENGTH_LONG).show()
            }
        }
        cancel.setOnClickListener() {
            dialog.dismiss()
        }
    }

    private fun getDrawers(drawers: ArrayList<Drawer>) {
        val ref = FirebaseDatabase.getInstance().getReference("Drawers")
        val user = FirebaseAuth.getInstance().currentUser?.uid
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    drawers.clear()
                    drawersName.clear()
                    for (e in p0.children) {
                        val drawer = e.getValue(Drawer::class.java)
                        if (drawer != null) {
                            if (drawer.uidUser.equals(user)) {
                                drawers.add(drawer)
                                drawersName.add(drawer.name)
                            }
                        }

                    }
                    val adapter = customDrawer(this@DrawersActivity, drawers)
                    grid.adapter = adapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        });

    }

    private fun updateBtn(position:Int){
        val inflater = layoutInflater
        grid.adapter

        val myBuild = AlertDialog.Builder(this@DrawersActivity)
        val dialoglayout = inflater.inflate(R.layout.activity_update_biblio, null)
        val biblioName = dialoglayout.findViewById<EditText>(R.id.BiblioName)
        val add = dialoglayout.findViewById<Button>(R.id.btnUpdate)
        val cancel = dialoglayout.findViewById<Button>(R.id.btnCancelar)

        myBuild.setView(dialoglayout)
        val dialog = myBuild.create()
        dialog.show()

        val grid = grid.getItemAtPosition(position) as Drawer
        val ref = FirebaseDatabase.getInstance().getReference("/Drawers/${grid.uid}")

        add.setOnClickListener() {
            if (checkName(biblioName.text.toString())) {
                if(!drawersName.contains(biblioName.text.toString())) {
                    val drawer = Drawer(grid.uid, grid.uidUser, biblioName.text.toString())
                    ref.setValue(drawer)
                    dialog.dismiss()
                }else{
                    Toast.makeText(this, "Nombre repetido", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, "Porfavor introduzca un nombre", Toast.LENGTH_LONG).show()
            }
        }
        cancel.setOnClickListener() {
            dialog.dismiss()
        }

        true
    }
    private fun deleteBtn(position:Int){
        val inflater = layoutInflater
        grid.adapter

        val myBuild = AlertDialog.Builder(this@DrawersActivity)
        val dialoglayout = inflater.inflate(R.layout.activity_delete_drawer, null)
        val borrar = dialoglayout.findViewById<Button>(R.id.btnBorrar)
        val cancel = dialoglayout.findViewById<Button>(R.id.btnCancelar)

        myBuild.setView(dialoglayout)
        val dialog = myBuild.create()
        dialog.show()

        val grid = grid.getItemAtPosition(position) as Drawer

        borrar.setOnClickListener() {
            val refDrawer = FirebaseDatabase.getInstance().getReference("Drawers").child(grid.uid)
            getShelfs(grid.uid)
            getBooks(grid.uid)
            refDrawer.removeValue()
            Toast.makeText(this, "Elemento borrado correctamente", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
        cancel.setOnClickListener() {
            dialog.dismiss()
        }

        true
    }

    private fun getShelfs(draweruid:String){
        val ref = FirebaseDatabase.getInstance().getReference("Shelf")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for (e in p0.children){
                        val shelf = e.getValue(Shelf::class.java)
                        if (shelf != null) {
                            if(draweruid.equals(shelf.uidDrawer)){
                                ref.removeValue()
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
    private fun getBooks(draweruid:String){
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for (e in p0.children){
                        val book = e.getValue(Book::class.java)
                        if (book != null) {
                            if(draweruid.equals(book.uidDrawer)){
                                ref.removeValue()
                            }
                        }
                    }

                }
            }

        });
    }

    private fun checkName(drawerName: String): Boolean {
        val regex = "^[a-zA-Z0-9]+$"
        val p = Pattern.compile(regex)
        val m = p.matcher(drawerName)
        val b = m.matches()
        return b
    }
}


