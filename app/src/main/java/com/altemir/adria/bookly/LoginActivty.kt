package com.altemir.adria.bookly

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_login_activty.*

class LoginActivty : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_activty)

        Login.setOnClickListener{
           login()
        }

        backToRegister.setOnClickListener{
            finish()
        }


    }
    private fun login(){
        val email = Email.text.toString()
        val password = Password.text.toString()
        if(!emailCheck()){
            if(!passwordCheck()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(this, OnCompleteListener { task ->
                            if(task.isSuccessful){
                                startActivity(Intent(this, DrawersActivity::class.java))
                                Toast.makeText(this, "SUCCESSFULLY LOGGED IN", Toast.LENGTH_LONG).show()
                            }else{
                                Toast.makeText(this, "Email o password incorrectos", Toast.LENGTH_LONG).show()
                            }
                        })
            }else{
                Password.error = "Introduce Password"
            }
        }else{
            Email.error = "Introduce mail"
        }




    }

    private fun emailCheck(): Boolean{
        return Email.text.toString().isEmpty()
    }
    private fun passwordCheck():Boolean{
        return Password.text.toString().isEmpty()
    }

}
