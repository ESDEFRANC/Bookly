package com.altemir.adria.bookly

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.altemir.adria.bookly.Model.User

class MainActivity : AppCompatActivity(),Register.OnRegistrationConfirmPressed, Register.OnTextRegistredPressedListener,LoginFragment.OnLoginConfirmPressed{

    override fun onLoginConfirmPressed() {
        val intent = Intent(this, DrawersActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


    override fun onRegistredPressed() {
        val fragmentLogin = LoginFragment.newInstance()
        supportFragmentManager.
                beginTransaction().
                replace(R.id.main_container, fragmentLogin).
                addToBackStack(null).
                commit()
    }

    override fun onRegistrationConfirmPressed() {
        val intent = Intent(this, DrawersActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            val fragmentRegisterActivity = Register()
            supportFragmentManager.beginTransaction().add(R.id.main_container, fragmentRegisterActivity).commit()
        }
    }


}
