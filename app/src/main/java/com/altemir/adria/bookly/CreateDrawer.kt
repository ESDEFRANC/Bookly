package com.altemir.adria.bookly

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.altemir.adria.bookly.Model.Drawer
import com.altemir.adria.bookly.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_biblio.*
import kotlinx.android.synthetic.main.activity_register_activity.*

class CreateDrawer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_biblio)

        ButtonCreateBiblio.setOnClickListener(){
            createDrawer("1")
        }
      verifyUserIsLogedIn()
    }
    private fun verifyUserIsLogedIn(){
        val uid = FirebaseAuth.getInstance().uid
        val auth = FirebaseAuth.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        setDataText(user)


        if(uid == null){
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

    @SuppressLint("SetTextI18n")
    private fun setDataText(user : FirebaseUser?){
        textView.text = "User Email " + user?.email
    }
    private fun createDrawer(profileUrl: String){
        val user = FirebaseAuth.getInstance().currentUser
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/Drawers/$uid")

        Log.d("CreateBiblio","Succesfully")
        val drawer = Drawer(uid, user!!.uid, "1", "Biblio1")

        ref.setValue(drawer)

    }
}
