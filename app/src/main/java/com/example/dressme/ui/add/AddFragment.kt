package com.example.dressme.ui.add

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dressme.R
import com.example.dressme.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*

class AddFragment : Fragment() {

    private val TAG: String = "AddFragment"

    private lateinit var addViewModel: AddViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addViewModel =
            ViewModelProviders.of(this).get(AddViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_add, container, false)
        addViewModel.text.observe(this, Observer {
        })

        root.confirm_button_add.setOnClickListener {
            confirm()
        }

        return root
    }


    private fun confirm() {
        // $todo: add proper notification
        if(!valid()) {
            Toast.makeText(getActivity(), "Form cannot be validated", Toast.LENGTH_LONG).show()
            return
        }

        val name       = title_edittext_add.text.toString()
        val desc_text   = item_desc_edittext_add.text.toString()
        // $todo: take a look
        val user_owner_id = FirebaseAuth.getInstance().uid ?: ""
        val owner_user: OwnerUser = OwnerUser(user_owner_id)

        val item: Item = Item(name, desc_text, owner_user)

        val ref = FirebaseFirestore.getInstance().collection("items")
        ref.add(item)
            .addOnSuccessListener {
                Log.d(TAG, "item has been saved to Firebase Database")
                title_edittext_add.text.clear()
                item_desc_edittext_add.text.clear()
            }
            .addOnFailureListener {
                Log.d(TAG, "Fail to save item")
            }
    }


    // $todo: update validation
    private fun valid(): Boolean {
        val name       = title_edittext_add.text.toString()
        val desc_text   = item_desc_edittext_add.text.toString()

        if (name.isEmpty() || desc_text.isEmpty())
            return false
        return true
    }
}


class Item(val name: String, val desc_text: String, val owner_user: OwnerUser)

class OwnerUser(val user_id: String)