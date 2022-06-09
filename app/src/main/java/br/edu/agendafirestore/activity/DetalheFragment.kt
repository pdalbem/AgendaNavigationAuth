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
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class DetalheFragment : Fragment() {

    val db = Firebase.firestore
    var contatoID: String?=null
    val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contatoID = it.getString("contatoID") as String
         }


        db.collection("usuarios")
            .document(currentUser!!.uid)
            .collection("contatos")
            .document(contatoID!!)
            .addSnapshotListener { value, error ->
                if (value!=null)
                {
                    val c = value.toObject<Contato>()

                    val nome = view?.findViewById<EditText>(R.id.editTextNome)
                    val fone = view?.findViewById<EditText>(R.id.editTextFone)
                    val email = view?.findViewById<EditText>(R.id.editTextEmail)

                    nome?.setText(c?.nome.toString())
                    fone?.setText(c?.fone.toString())
                    email?.setText(c?.email.toString())

                }
            }
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
        inflater.inflate(R.menu.menu_detalhe, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.action_alterarContato) {
            val nome = view?.findViewById<EditText>(R.id.editTextNome)?.text.toString()
            val fone = view?.findViewById<EditText>(R.id.editTextFone)?.text.toString()
            val email = view?.findViewById<EditText>(R.id.editTextEmail)?.text.toString()

            val c = Contato(nome,fone,email)
            db.collection("usuarios")
                .document(currentUser!!.uid)
                .collection("contatos")
                .document(contatoID!!)
                .set(c)

            Toast.makeText(activity,"Informações alteradas", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()

        }

        if (item.itemId==R.id.action_excluirContato) {

            db.collection("usuarios")
                .document(currentUser!!.uid)
                .collection("contatos")
                .document(contatoID!!)
                .delete()

            Toast.makeText(activity,"Contato excluído", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }

        return super.onOptionsItemSelected(item)
    }


}