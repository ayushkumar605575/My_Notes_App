package com.ayush.mynotes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore


class MyNotesFragment : Fragment() {

    private var arrNotes = ArrayList<NotesModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_notes, container, false)
        val db = FirebaseFirestore.getInstance()

        db.collection("Notes/$tokenID/note").get()
            .addOnSuccessListener { result ->
                val sortedNotes = result.documents.sortedByDescending {
                    it.data?.get("id").toString().toInt()
                }
                for (document in sortedNotes) {
                    arrNotes.add(NotesModel(
                        document.data!!["Title"].toString(),
                        document.data!!["Content"].toString(),
                        document.data!!["Time"].toString(),
                        document.data!!["id"].toString().toInt()
                    ))
                }
            }.addOnCompleteListener {
                val recyclerView = view.findViewById<RecyclerView>(R.id.notesRecycleView)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                val notesAdapter = RecyclerNotesAdapter(arrNotes)
                recyclerView.adapter = notesAdapter
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }

        return view
    }

}