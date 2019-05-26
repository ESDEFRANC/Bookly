package com.altemir.adria.bookly

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.altemir.adria.bookly.Model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login_activty.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [LoginFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LoginFragment : Fragment() {
    interface OnLoginConfirmPressed {
        fun onLoginConfirmPressed()
    }

    private lateinit var buttonRegisteredListener: OnLoginConfirmPressed
    private var fieldsOk = false



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_login_activty, container, false)
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        buttonRegisteredListener = activity as OnLoginConfirmPressed
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Login.setOnClickListener{
            login()
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
    companion object {
        fun newInstance():LoginFragment{
            val fragmentRegister = LoginFragment()
            val args = Bundle()

            fragmentRegister.arguments = args

            return fragmentRegister
        }
    }

    private fun login(){
        if(isNetworkConnected()) {
            if (checkFields()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(Email.text.toString(), Password.text.toString())
                        .addOnCompleteListener{ task ->
                            if (task.isSuccessful) {
                                buttonRegisteredListener.onLoginConfirmPressed()
                            } else {
                                notifyUser(getString(R.string.EmailPassIncorrectos))
                            }
                        }
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
        Toast.makeText(context,message, Toast.LENGTH_LONG).show()
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
}
