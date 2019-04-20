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
       if(checkFields()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(Email.text.toString(),Password.text.toString())
                        .addOnCompleteListener(this, OnCompleteListener { task ->
                            if(task.isSuccessful){
                                startActivity(Intent(this, DrawersActivity::class.java))
                                notifyUser("SUCCESSFULLY LOGGED IN")
                            }else{
                                notifyUser("Email o password incorrectos")
                            }
                        })
            }
        }


    private fun emailCheck(): Boolean{
        return Email.text.toString().isEmpty()
    }
    private fun passwordCheck():Boolean{
        return Password.text.toString().isEmpty()
    }

    private fun checkFields():Boolean = if(!emailCheck()) {
        if(!passwordCheck()){
            true
        }else {
            notifyUser("Introduce password")
            false
        }
    }else{
        notifyUser("Introduce email")
        false
    }

    private fun notifyUser(message:String){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

}
