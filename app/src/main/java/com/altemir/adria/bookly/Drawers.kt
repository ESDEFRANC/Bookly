package com.altemir.adria.bookly

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.altemir.adria.bookly.Adapter.customDrawer
import com.altemir.adria.bookly.Model.Drawer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_biblio.*
import java.util.UUID.randomUUID


class Drawers : AppCompatActivity() {

    val drawers = arrayListOf<Drawer>()


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
            val intent = Intent(this, Shelfs::class.java)
                intent.putExtra("drawerUID", grid.getItemAtPosition(position) as Drawer);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent)
        }

        grid.setOnItemLongClickListener{ parent, view, position, id ->
            val inflater = layoutInflater
            grid.adapter

            val myBuild = AlertDialog.Builder(this)
            val dialoglayout = inflater.inflate(R.layout.activity_add_biblio, null)
            val biblioName = dialoglayout.findViewById<EditText>(R.id.BiblioName)
            val add = dialoglayout.findViewById<Button>(R.id.btnAdd)
            val cancel = dialoglayout.findViewById<Button>(R.id.btnCancelar)

            myBuild.setView(dialoglayout)
            val dialog = myBuild.create()
            dialog.show()
            val grid = grid.getItemAtPosition(position) as Drawer
            val ref = FirebaseDatabase.getInstance().getReference("/Drawers/${grid.uid}")
            add.setOnClickListener() {
                if (!biblioName.text.toString().isEmpty()) {
                    val drawer = Drawer(grid.uid, grid.uidUser!!, biblioName.text.toString())
                    /*if(drawer.name.equals(ref)){
                        Toast.makeText(this, "El nombre es igual", Toast.LENGTH_LONG).show()
                    }*/
                    ref.setValue(drawer)
                    dialog.dismiss()


                } else {
                    Toast.makeText(this, "Porfavor introduzca un nombre", Toast.LENGTH_LONG).show()
                }
            }
            cancel.setOnClickListener() {
                dialog.dismiss()
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
            if (!biblioName.text.toString().isEmpty()) {
                val drawer = Drawer(draweruid, user!!.uid, biblioName.text.toString())
                ref.setValue(drawer)
                dialog.dismiss()


            } else {
                Toast.makeText(this, "Porfavor introduzca un nombre", Toast.LENGTH_LONG).show()
            }
        }
        cancel.setOnClickListener() {
            dialog.dismiss()
        }
        Log.d("CreateBiblio", "Succesfully")

    }
    private fun getDrawers(drawers:ArrayList<Drawer>){
        val ref = FirebaseDatabase.getInstance().getReference("Drawers")
        val user = FirebaseAuth.getInstance().currentUser?.uid
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
            if(p0.exists()){
                drawers.clear()
                for (e in p0.children){
                    val drawer = e.getValue(Drawer::class.java)
                    if (drawer != null) {
                        if(drawer.uidUser.equals(user)){
                            drawers.add(drawer)
                        }
                    }

                }
                val adapter = customDrawer(this@Drawers, drawers)
                grid.adapter = adapter
            }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        });

    }


}
