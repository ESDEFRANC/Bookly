package com.altemir.adria.bookly

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerMain.setOnClickListener {
            perfomRegistration()


        }

        alreadyMain.setOnClickListener {
            Log.d("MainActivity", "Try to show login activity")

            val intent = Intent(this, LoginActivty::class.java)
            startActivity(intent)
        }
    }

    private fun perfomRegistration(){
        val email = emailMain.text.toString()
        val password = passwordMain.text.toString()
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter text in email/password",Toast.LENGTH_LONG).show()
            return
        }
        Log.d("MainActivity", "Email is: $email")
        Log.d("MainActivity", "Password is: $password")

        //Firebase authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{
                    if(!it.isSuccessful) return@addOnCompleteListener
                    //else if succesfull
                    Log.d("Main", "Succesfully created user with uid: ${it.result!!.user.uid}")
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Failed to create user:  ${it.message}",Toast.LENGTH_LONG).show()
                    Log.d("Main", "Failed to create user:  ${it.message}")
                }
    }

}
