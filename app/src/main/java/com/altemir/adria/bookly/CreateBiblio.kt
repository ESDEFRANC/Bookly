package com.altemir.adria.bookly

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_create_biblio.*

class CreateBiblio : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_biblio)

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
}
