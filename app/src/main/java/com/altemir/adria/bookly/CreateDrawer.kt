package com.altemir.adria.bookly

import android.annotation.SuppressLint
import android.content.DialogInterface
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
import com.altemir.adria.bookly.Model.Drawer
import com.altemir.adria.bookly.Model.Shelf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_biblio.*
import java.util.UUID.randomUUID


class CreateDrawer : AppCompatActivity() {

    val draweruid = randomUUID().toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_biblio)

        ButtonCreateBiblio.setOnClickListener() {
            createDrawer(draweruid)

        }
        verifyUserIsLogedIn()
    }

    private fun verifyUserIsLogedIn() {
        val uid = FirebaseAuth.getInstance().uid
        //val auth = FirebaseAuth.getInstance()
        val user = FirebaseAuth.getInstance().currentUser


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

    private fun createDrawer(draweruid: String) {
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
                val intent = Intent(this, CreateShelf::class.java)
                intent.putExtra("drawerUID", draweruid);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent)
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


}
