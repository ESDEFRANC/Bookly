package com.altemir.adria.bookly

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.altemir.adria.bookly.Model.Shelf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_biblio.*
import kotlinx.android.synthetic.main.activity_create_shelf.*
import java.util.UUID.randomUUID

class CreateShelf : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_shelf)
        val drawerUID = intent.extras.getString("drawerUID");
        ButtonCreateShelf.setOnClickListener(){
            createCalaix(drawerUID)
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
        val calaixName = dialoglayout.findViewById<EditText>(R.id.BiblioName)
        val add = dialoglayout.findViewById<Button>(R.id.btnAdd)
        val cancel = dialoglayout.findViewById<Button>(R.id.btnCancelar)
        myBuild.setView(dialoglayout)
        val dialog = myBuild.create()
        dialog.show()
        add.setOnClickListener() {
            if (!calaixName.text.toString().isEmpty()) {
                val shelf = Shelf(shelf, draweruid, calaixName.text.toString())
                ref.setValue(shelf)
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
