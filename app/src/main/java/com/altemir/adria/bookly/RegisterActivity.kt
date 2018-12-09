package com.altemir.adria.bookly

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register_activity.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_activity)

        registerMain.setOnClickListener {
            perfomRegistration()


        }

        alreadyMain.setOnClickListener {
            Log.d("RegisterActivity", "Try to show login activity")

            val intent = Intent(this, LoginActivty::class.java)
            startActivity(intent)
        }
        buttonImg.setOnClickListener {
            Log.d("RegisterActivity", "Try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }


    var selectedPhotoUri: Uri?=null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //proced and check what thw selected image
            Log.d("RegisteActivity", "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectPhotoImg.setImageBitmap(bitmap)

            buttonImg.alpha = 0f

            //val bitmapDrawable = BitmapDrawable(bitmap)
            //buttonImg.setBackgroundDrawable(bitmapDrawable)

        }
    }

    private fun perfomRegistration(){
        val email = emailMain.text.toString()
        val password = passwordMain.text.toString()
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter text in email/password",Toast.LENGTH_LONG).show()
            return
        }
        Log.d("RegisterActivity", "Email is: $email")
        Log.d("RegisterActivity", "Password is: $password")

        //Firebase authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{
                    if(!it.isSuccessful) return@addOnCompleteListener
                    //else if succesfull
                    Log.d("Main", "Succesfully created user with uid: ${it.result!!.user.uid}")

                    uploadImageToFireBaseStorage()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Failed to create user:  ${it.message}",Toast.LENGTH_LONG).show()
                    Log.d("Main", "Failed to create user:  ${it.message}")
                }
    }
    private fun uploadImageToFireBaseStorage(){
        if(selectedPhotoUri == null ) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("Register","Succesfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        saveUserToFirebaaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener{

                }


    }
    private fun saveUserToFirebaaseDatabase(profileUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, nameMain.text.toString(), profileUrl)

        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d("RegisterActivity" , "Finally we saved the user to Firebasa Database")
                }
    }

}

class User(val uid:String,val userName:String, val profileUrl : String)