package com.altemir.adria.bookly

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.altemir.adria.bookly.Adapter.customShelf
import com.altemir.adria.bookly.Model.Drawer
import com.altemir.adria.bookly.Model.Shelf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_create_shelf.*
import java.util.UUID.randomUUID

class ShelfsActivity : AppCompatActivity() {

    val shelfs = arrayListOf<Shelf>()
    val shelfsName = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_shelf)
        val drawer = intent.getParcelableExtra<Drawer>("drawerUID");
        val drawerUID = drawer.uid

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
            if (!calaixName.text.toString().isEmpty()) {
                if(!shelfsName.contains(calaixName.text.toString())){
                    val shelf1 = Shelf(shelf, draweruid, calaixName.text.toString())
                    ref.setValue(shelf1)
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
}
