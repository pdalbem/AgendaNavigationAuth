package br.edu.agendafirestore.activity

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.edu.agendafirestore.R
import br.edu.agendafirestore.model.Contato
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CadastroFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.common_layout, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_cadastro, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.action_salvarContato)
        {
            val nome = view?.findViewById<EditText>(R.id.editTextNome)?.text.toString()
            val fone = view?.findViewById<EditText>(R.id.editTextFone)?.text.toString()
            val email = view?.findViewById<EditText>(R.id.editTextEmail)?.text.toString()

            val c = Contato( nome, fone, email)

            val db = Firebase.firestore
            val uid = FirebaseAuth.getInstance().uid

            db.collection("usuarios")
                .document(uid!!)
                .collection("contatos")
                .add(c)

              Toast.makeText(activity,"Contato Inserido", Toast.LENGTH_LONG).show()
             findNavController().popBackStack()

        }
        return super.onOptionsItemSelected(item)
    }


}