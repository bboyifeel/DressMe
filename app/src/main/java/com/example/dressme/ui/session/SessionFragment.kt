package com.example.dressme.ui.session

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.dressme.R
import com.example.dressme.model.ItemModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import org.w3c.dom.Document


class SessionFragment : Fragment() {
    private lateinit var sessionViewModel: SessionViewModel;
    private lateinit var mFirebaseRef: FirebaseFirestore
    private lateinit var mItemRef: CollectionReference
    private lateinit var mStorageRef: StorageReference
    private val DEBUG = "Sandybug"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sessionViewModel =
            ViewModelProviders.of(this).get(SessionViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_session, container, false)
        sessionViewModel.text.observe(this, Observer {
        })

        // TODO Implement Bindings
//        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
//            inflater, R.layout.fragment_sleep_tracker, container, false)

        mFirebaseRef = FirebaseFirestore.getInstance()
        mStorageRef = FirebaseStorage.getInstance().getReference()
        mItemRef = mFirebaseRef.collection("items")

        /* Setting up the recycler with its reference, adapter and orientation. */
        val objectsRecycler : RecyclerView = root.findViewById<RecyclerView>(R.id.session_objectList_recycler)
        val adapter = ItemsObjectAdapter()

        objectsRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        objectsRecycler.adapter = adapter

        /* UX:  Recycler SnapHelper. */
        //val snapHelper : SnapHelperOneByOne = SnapHelperOneByOne()
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(objectsRecycler)

        /* Recycler cosmetics. */
        val dividerItemDecoration = DividerItemDecoration(
            objectsRecycler.context,
            LinearLayoutManager.HORIZONTAL
        )
        val horizontalSpaceItemDecoration =
            HorizontalSpaceItemDecoration()
        objectsRecycler.addItemDecoration(dividerItemDecoration)
        objectsRecycler.addItemDecoration(horizontalSpaceItemDecoration)

        retrieveData(adapter)

        return root
    }

    private fun retrieveData(adapter: ItemsObjectAdapter) {
        var task: Task<QuerySnapshot> = mItemRef.get()
        task.addOnSuccessListener {
            var queryRes: MutableList<DocumentSnapshot> = it.getDocuments()
            var adapterData: MutableList<ItemModel> = mutableListOf()
            queryRes.shuffle()

            for (snapshot: DocumentSnapshot in queryRes) {
                adapterData.add(snapshotToModel(snapshot))
            }
            adapterData.toList()
            adapter.data = adapterData
        }
    }

    private fun snapshotToModel(doc: DocumentSnapshot): ItemModel {
        return ItemModel(
            doc.get("name") as String?,
            doc.get("description") as String?,
            doc.get("brand") as String?,
            doc.get("image_uri") as String?,
            doc.get("price") as String?
        )
    }
}