package com.altemir.adria.bookly

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.jetbrains.anko.doAsync
import com.altemir.adria.bookly.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register_activity.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import android.net.NetworkInfo




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Register.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Register.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Register : Fragment() {

    interface OnRegistrationConfirmPressed {
        fun onRegistrationConfirmPressed()
    }
    interface OnTextRegistredPressedListener {
        fun onRegistredPressed()

    }

    private lateinit var buttonRegisteredListener: OnRegistrationConfirmPressed
    private lateinit var registerListener: OnTextRegistredPressedListener
    private var fieldsOk = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.run { inflate(R.layout.activity_register_activity, container, false) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonRegisteredListener = activity as OnRegistrationConfirmPressed
        registerListener = activity as OnTextRegistredPressedListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        books_progressBar.visibility = View.INVISIBLE
        registerMain.setOnClickListener {
            if(isNetworkConnected()){
                perfomRegistration()
            }

            }
        alreadyMain.setOnClickListener {
            registerListener.onRegistredPressed()
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

        val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, selectedPhotoUri)

        selectPhotoImg.setImageBitmap(bitmap)

        buttonImg.alpha = 0f
    }
}

    private fun perfomRegistration() {
        if(selectedPhotoUri != null){
            if(checkAllFields()){
                disableUIInteractions()
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailMain.text.toString(), passwordMain.text.toString())

                        .addOnCompleteListener {
                            if (!it.isSuccessful) return@addOnCompleteListener
                            uploadImageToFireBaseStorage()
                            books_progressBar.visibility = View.VISIBLE

                        }
                        .addOnFailureListener {
                            onFailure(it.also{})
                            enableUIInteractions()
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
                    buttonRegisteredListener.onRegistrationConfirmPressed()
                }
    }

    protected fun isNetworkConnected(): Boolean {
        try {
            val mConnectivityManager = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mNetworkInfo = mConnectivityManager.activeNetworkInfo
            return if (mNetworkInfo == null) false else true

        } catch (e: NullPointerException) {
            return false

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
        Toast.makeText(context,message, Toast.LENGTH_LONG).show()

    }
    private fun onFailure(e: Exception) {
        when (e) {
            is FirebaseAuthWeakPasswordException -> notifyUser(getString(R.string.PasswordShort))
            is FirebaseAuthUserCollisionException -> notifyUser(getString(R.string.AlreadyRegisteredMail))
        }
    }
    companion object {
        fun newInstance():Register{
            val fragmentRegister = Register()
            val args = Bundle()
            fragmentRegister.arguments = args

            return fragmentRegister
        }
    }

    private fun disableUIInteractions() {
        selectPhotoImg.isEnabled = false
        nameMain.isEnabled = false
        emailMain.isEnabled = false
        passwordMain.isEnabled = false
        passwordMain2.isEnabled = false
        alreadyMain.isEnabled = false
        registerMain.isEnabled = false
        buttonImg.isEnabled = false
    }

    private fun enableUIInteractions() {
        selectPhotoImg.isEnabled = true
        nameMain.isEnabled = true
        emailMain.isEnabled = true
        passwordMain.isEnabled = true
        passwordMain2.isEnabled = true
        alreadyMain.isEnabled = true
        registerMain.isEnabled = true
        buttonImg.isEnabled = true
    }



}
