package br.edu.agendafirestore.activity

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.edu.agendafirestore.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

   private  var currentUser = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val signInLauncher = registerForActivityResult(
                FirebaseAuthUIActivityResultContract()
            ) { result: FirebaseAuthUIAuthenticationResult? ->
                   if (result?.resultCode == RESULT_OK) {
                       val uid =  FirebaseAuth.getInstance().uid!!
                       val mapValues = mapOf("usuario" to uid,
                                              "nome" to FirebaseAuth.getInstance().currentUser!!.displayName)

                       val db = Firebase.firestore
                       db.collection("usuarios").document(uid).set(mapValues)
                       findNavController().popBackStack()
                   }
                }


            val intent = AuthUI.getInstance().createSignInIntentBuilder().build()
            signInLauncher.launch(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

}