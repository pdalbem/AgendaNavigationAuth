package br.edu.agendafirestore.activity

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.agendafirestore.R
import br.edu.agendafirestore.adapter.ContatoAdapter
import br.edu.agendafirestore.model.Contato
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ListaContatosFragment : Fragment() {


    lateinit var contatoAdapter: ContatoAdapter
     var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        if (fab != null) {
            fab.setOnClickListener{
                 findNavController().navigate(R.id.action_ListaToCadastro)
            }
        }
    }

    override fun onStart() {
        super.onStart()
         currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null)
            findNavController().navigate(R.id.action_ListaToLogin)
        else
            updateUI(currentUser!!)
    }

    private fun updateUI(currentUser:FirebaseUser)
    {
            val db = Firebase.firestore
            val query: Query = db.collection("usuarios").document(currentUser.uid).collection("contatos").orderBy("nome")
            val options: FirestoreRecyclerOptions<Contato> =
                FirestoreRecyclerOptions.Builder<Contato>()
                    .setLifecycleOwner(this)
                    .setQuery(query, Contato::class.java).build()

            contatoAdapter = ContatoAdapter(options)

            val recyclerview = view?.findViewById<RecyclerView>(R.id.recyclerview)
            recyclerview!!.layoutManager = LinearLayoutManager(context)
            recyclerview.adapter = contatoAdapter

            val clickListener = object : ContatoAdapter.ContatoClickListener {
                override fun onItemClick(pos: Int) {
                    val bundle =
                        bundleOf("contatoID" to contatoAdapter.snapshots.getSnapshot(pos).id)
                    findNavController().navigate(R.id.action_ListaToDetalhe, bundle)
                }

                override fun onImageClick(pos: Int) {
                    var fav: Boolean? = contatoAdapter.snapshots
                        .getSnapshot(pos).getBoolean("favorito")

                    if (fav == null) fav = false

                    db.collection("usuarios")
                        .document(currentUser.uid)
                        .collection("contatos")
                        .document(contatoAdapter.snapshots.getSnapshot(pos).id)
                        .update("favorito", if (fav) false else true)
                }

            }

            contatoAdapter.clickListener = clickListener
            contatoAdapter.startListening()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       return inflater.inflate(R.layout.fragment_listacontatos, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        val item = menu.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0!="") {
                    contatoAdapter.stopListening()
                    val query: Query = Firebase.firestore
                                       .collection("usuarios")
                        .document(FirebaseAuth.getInstance().uid!!)
                        .collection("contatos")
                        .whereGreaterThanOrEqualTo("nome", p0.toString())
                        .whereLessThanOrEqualTo("nome",p0.toString()+"\uf8ff")

                    val newOptions: FirestoreRecyclerOptions<Contato> = FirestoreRecyclerOptions
                        .Builder<Contato>()
                        .setQuery(query, Contato::class.java).build()

                    contatoAdapter.updateOptions(newOptions)
                    contatoAdapter.notifyDataSetChanged()
                    contatoAdapter.startListening()
                }
                else
                {
                    contatoAdapter.stopListening()
                    updateUI(currentUser!!)
                    contatoAdapter.startListening()
                }
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.action_logout){
            AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener { // user is now signed out
                     activity?.onBackPressed()
                }
        }

        if (item.itemId==R.id.action_deleteaccount){
            Firebase.firestore.collection("usuarios")
                .document(FirebaseAuth.getInstance().uid!!)
                .delete()

            AuthUI.getInstance()
                .delete(requireContext())
                .addOnCompleteListener { // user is now signed out
                    activity?.onBackPressed()
                }
        }

         return super.onOptionsItemSelected(item)
    }


}


