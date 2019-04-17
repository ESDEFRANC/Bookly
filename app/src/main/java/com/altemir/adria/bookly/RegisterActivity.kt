package com.altemir.adria.bookly

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.altemir.adria.bookly.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register_activity.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var selected = false
        setContentView(R.layout.activity_register_activity)
        internetConnected()
        books_progressBar.visibility = View.INVISIBLE

        registerMain.setOnClickListener {
            perfomRegistration()
        }

        alreadyMain.setOnClickListener {
            val intent = Intent(this, LoginActivty::class.java)
            startActivity(intent)
        }
        buttonImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }


    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectPhotoImg.setImageBitmap(bitmap)

            buttonImg.alpha = 0f
        }
    }

    private fun perfomRegistration() {
        val email = emailMain.text.toString()
        val password = passwordMain.text.toString()
        if(selectedPhotoUri != null){
                if (!checkUserName()) {
                    if (!checkUserMail()) {
                        if (!checkPassword1()) {
                            if (!checkPassword2()) {
                                if (!checkPasswordEquals()) {
                                    passwordMain2.error = "Password are differents"
                                } else {

                                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener {
                                                if (!it.isSuccessful) return@addOnCompleteListener
                                                books_progressBar.visibility = View.VISIBLE
                                                uploadImageToFireBaseStorage()

                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(this, "Failed to create user:  ${it.message}", Toast.LENGTH_LONG).show()
                                                Log.d("Main", "Failed to create user:  ${it.message}")
                                            }
                                }
                            } else {
                                passwordMain2.error = "Introduzca password"
                            }

                        } else {
                            passwordMain.error = "Introduzca password"
                        }
                    } else {
                        emailMain.error = "Introduzca mail"
                    }
                } else {
                    nameMain.error = "Introduzca nombre de usuario"
                }
                }else{
                    buttonImg.error =  "Seleccione imagen"
                }
    }


    private fun uploadImageToFireBaseStorage(){
        if (selectedPhotoUri == null) {
            return
        }else{
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

            ref.putFile(selectedPhotoUri!!)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            saveUserToFirebaaseDatabase(it.toString())
                        }
                    }
                    .addOnFailureListener {
                    }
        }


    }

    private fun saveUserToFirebaaseDatabase(profileUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, nameMain.text.toString(), profileUrl)

        ref.setValue(user)
                .addOnSuccessListener {
                    val intent = Intent(this, DrawersActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    books_progressBar.visibility = View.GONE
                    Toast.makeText(this, getString(R.string.RegisterComplete),Toast.LENGTH_LONG).show()



                }
    }
    private fun internetConnected(){
        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = cm.activeNetworkInfo

        if(networkInfo == null){
            Toast.makeText(baseContext,getString(R.string.Nointernet),Toast.LENGTH_LONG).show()
            this.finish()
        }
    }
    private fun checkPasswordEquals():Boolean{
        return passwordMain.text.toString() == passwordMain2.text.toString()
    }
    private fun checkUserName():Boolean{
        return nameMain.text.toString().isEmpty()
    }
    private fun checkUserMail():Boolean{
        return emailMain.text.toString().isEmpty()
    }
    private fun checkPassword1():Boolean{
        return passwordMain.text.toString().isEmpty()
    }
    private fun checkPassword2():Boolean{
        return passwordMain2.text.toString().isEmpty()
    }

}
