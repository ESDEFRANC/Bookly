package com.altemir.adria.bookly

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.altemir.adria.bookly.Model.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register_activity.*
import java.util.*
import com.google.firebase.auth.*
import java.util.regex.Pattern


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_activity)

        books_progressBar.visibility = View.INVISIBLE

        registerMain.setOnClickListener {
            if(internetConnected()){
                perfomRegistration()
            }

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


    private var selectedPhotoUri: Uri? = null
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
        if(selectedPhotoUri != null){
               if(checkAllFields()){
                   FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailMain.text.toString(), passwordMain.text.toString())

                           .addOnCompleteListener {
                               if (!it.isSuccessful) return@addOnCompleteListener
                               uploadImageToFireBaseStorage()
                               books_progressBar.visibility = View.VISIBLE

                           }
                           .addOnFailureListener {
                               onFailure(it.also{})
                           }
               }
        }else{
            buttonImg.error =  getString(R.string.SeleccioneImagen)
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
    private fun checkPasswordEquals():Boolean{
        return passwordMain.text.toString() == passwordMain2.text.toString()
    }
    private fun checkUserName():Boolean{
        return nameMain.text.toString().isEmpty()
    }
    private fun checkUserMail():Boolean{
        val regex = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*\$"
        val p = Pattern.compile(regex)
        val drawertrimed = emailMain.text.toString().trim()
        val m = p.matcher(drawertrimed)
        val b = m.matches()
        return b
    }
    private fun checkPassword1():Boolean{
        return passwordMain.text.toString().isEmpty()
    }
    private fun checkPassword2():Boolean{
        return passwordMain2.text.toString().isEmpty()
    }

    private fun checkAllFields():Boolean{
        if (!checkUserName()) {
            if (checkUserMail()) {
                if (!checkPassword1()) {
                    if (!checkPassword2()) {
                        if (checkPasswordEquals()) {
                            return true
                        } else {
                            passwordMain2.error = getString(R.string.passwordsDiferentes)
                            return false
                        }
                    } else {
                        passwordMain2.error = getString(R.string.IntroduzcaPassword)
                        return false
                    }
                } else {
                    passwordMain.error = getString(R.string.IntroduzcaPassword)
                    return false
                }
            } else {
                emailMain.error = getString(R.string.Introduzcamailvalido)
                return false
            }
        } else {
            nameMain.error = getString(R.string.NombreUsuario)
            return false
        }

    }

    private fun notifyUser(message:String){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()

    }
    private fun onFailure(e: Exception) {
        when (e) {
            is FirebaseAuthWeakPasswordException -> notifyUser(getString(R.string.PasswordShort))
            is FirebaseAuthUserCollisionException -> notifyUser(getString(R.string.AlreadyRegisteredMail))
        }
    }





}
