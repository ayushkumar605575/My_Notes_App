package com.ayush.mynotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditNoteFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val notesId: Int = requireArguments().getInt("notesId")

        val view = inflater.inflate(R.layout.fragment_edit_note, container, false)

        val now = LocalDateTime.now()

        val db = FirebaseFirestore.getInstance()
        val okbtn = view.findViewById<MaterialButton>(R.id.okBtn)
        val cancelbtn = view.findViewById<MaterialButton>(R.id.cancelBtn)
        val title = view.findViewById<EditText>(R.id.titleEt)
        val content = view.findViewById<EditText>(R.id.contentEt)
        val myNotesFragment = MyNotesFragment()
        title.setText(requireArguments().getString("Title"))
        content.setText(requireArguments().getString("Content"))

        okbtn.setOnClickListener {
            if (title.text.isNotEmpty()) {
                val twelveHourFormat = DateTimeFormatter.ofPattern("hh:mm a")
                val time = twelveHourFormat.format(now)

                val note = hashMapOf(
                    "id" to notesId,
                    "Title" to title.text.toString(),
                    "Content" to content.text.toString(),
                    "Time" to "On $time   ${now.dayOfMonth}/${now.monthValue}/${now.year}",
                )
                db.document("Notes/$tokenID/note/$notesId").set(note)

                Toast.makeText(this.activity, "Note Edited Successfully!", Toast.LENGTH_SHORT).show()

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, myNotesFragment).commit()

            } else {
                Toast.makeText(this.activity, "Empty Note Discarded!", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, myNotesFragment).commit()
            }
        }
        cancelbtn.setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, myNotesFragment).commit()

        }
        return view
    }

}