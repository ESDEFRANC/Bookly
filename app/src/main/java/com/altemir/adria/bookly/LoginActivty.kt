package com.altemir.adria.bookly

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
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
        if(internetConnected()) {
            if (checkFields()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(Email.text.toString(), Password.text.toString())
                        .addOnCompleteListener(this, OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(this, DrawersActivity::class.java))
                                notifyUser(getString(R.string.Logedin))
                            } else {
                                notifyUser(getString(R.string.EmailPassIncorrectos))
                            }
                        })
            }
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
            notifyUser(getString(R.string.IntroducePassword))
            false
        }
    }else{
        notifyUser(getString(R.string.IntroduceMail))
        false
    }

    private fun notifyUser(message:String){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }
    private fun internetConnected():Boolean{
        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = cm.activeNetworkInfo

        if(networkInfo == null){
            Toast.makeText(baseContext,getString(R.string.Nointernet),Toast.LENGTH_LONG).show()
            return false
        }else{
            return true
        }
    }

}
