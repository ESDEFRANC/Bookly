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
        setContentView(R.layout.activity_register_activity)
        internetConnected()
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

    private fun perfomRegistration() {
        val email = emailMain.text.toString()
        val password = passwordMain.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/password", Toast.LENGTH_LONG).show()
            return
        }

        if (!checkPassword()) {
            passwordMain2.error = "Password are differents"
            return
        } else {

            //Firebase authentication
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener
                        //else if succesfull

                        uploadImageToFireBaseStorage()
                        Log.d("Main", "Succesfully created user with uid: ${it.result!!.user.uid}")


                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to create user:  ${it.message}", Toast.LENGTH_LONG).show()
                        Log.d("Main", "Failed to create user:  ${it.message}")
                    }
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
       // val refchild = FirebaseDatabase.getInstance().getReference().child("/users/$uid/$nameMain")
        //val ref1 = FirebaseStorage.getInstance().getReference("/biblio/$nameMain")

        val user = User(uid, nameMain.text.toString(), profileUrl)
        //val user1 = User1(uid, nameMain.text.toString(),emailMain.text.toString(),profileUrl )

        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d("RegisterActivity" , "Finally we saved the user to Firebasa Database")

                    val intent = Intent(this, CreateDrawer::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    Toast.makeText(this, "Succesfully registered",Toast.LENGTH_LONG).show()


                }
    }
    private fun internetConnected(){
        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = cm.activeNetworkInfo

        if(networkInfo == null){
            Toast.makeText(baseContext,"No internet",Toast.LENGTH_LONG).show()
            this.finish()
        }
    }
    private fun checkPassword():Boolean{
        return passwordMain.text.toString() == passwordMain2.text.toString()
    }

}
