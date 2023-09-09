package com.ayush.mynotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore


class RecyclerNotesAdapter(private val data: ArrayList<NotesModel>) :
    RecyclerView.Adapter<RecyclerNotesAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun collapseExpandedView() {
            content.maxLines = 1
            deleteBtn.visibility = View.GONE
            title.maxLines = 1
            title.textAlignment = View.TEXT_ALIGNMENT_CENTER
            time.visibility = View.GONE
            editBtn.visibility = View.GONE
        }

        val title: TextView = itemView.findViewById(R.id.myTitleTv)
        val content: TextView = itemView.findViewById(R.id.myContentTv)
        val time: TextView = itemView.findViewById(R.id.timeStamp)
        val cardView: MaterialCardView = itemView.findViewById(R.id.notesEditCv)
        val editBtn: MaterialButton = itemView.findViewById(R.id.editNoteBtn)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteNoteBtn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val slideIn = AnimationUtils.loadAnimation(holder.itemView.context, android.R.anim.slide_in_left)
        holder.itemView.startAnimation(slideIn)


        val noteData = data[position]
        holder.title.text = noteData.title
        holder.content.text = noteData.content
        holder.time.text = noteData.time
        val isExpanded = noteData.isExpanded
        holder.title.maxLines = if (isExpanded) Int.MAX_VALUE else 1
        holder.title.textAlignment = if (isExpanded) View.TEXT_ALIGNMENT_TEXT_START else View.TEXT_ALIGNMENT_CENTER
        holder.content.maxLines = if (isExpanded) Int.MAX_VALUE else 1
        holder.deleteBtn.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.editBtn.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.time.visibility = if (isExpanded) View.VISIBLE else View.GONE
        if (holder.content.text.isEmpty()) {
            holder.content.visibility = View.GONE
        } else {
            holder.content.visibility = View.VISIBLE
        }

        holder.deleteBtn.setOnClickListener {

            val dialogBuilder = MaterialAlertDialogBuilder(holder.itemView.context)

            dialogBuilder.setTitle("Do you really want to delete this note?")
            dialogBuilder.setMessage("This cannot be undone.")

            dialogBuilder.setPositiveButton("OK") { _, _ ->
                updateEntry(position)
            }
            dialogBuilder.setNegativeButton("Cancel", null)
            dialogBuilder.show()
        }

        holder.cardView.setOnClickListener {
            isAnyItemExpanded(position)
            noteData.isExpanded = !noteData.isExpanded
            notifyItemChanged(position, Unit)
        }

        holder.editBtn.setOnClickListener {
            val context = holder.itemView.context as MainActivity

            val fragmentManager = context.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            val bundle = Bundle()
            bundle.putInt("notesId", data[position].id)
            bundle.putString("Title", data[position].title)
            bundle.putString("Content", data[position].content)

            val editNoteFragment = EditNoteFragment()
            editNoteFragment.arguments = bundle

            fragmentTransaction.replace(R.id.fragmentContainer, editNoteFragment).commit()
        }


    }

    private fun updateEntry(position: Int) {
        val id = data[position].id
        db.document("Notes/$tokenID/note/$id").delete()
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    private fun isAnyItemExpanded(position: Int) {
        val temp = data.indexOfFirst {
            it.isExpanded
        }
        if (temp >= 0 && temp != position) {
            data[temp].isExpanded = false
            notifyItemChanged(temp, 0)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty() && payloads[0] == 0) {
            holder.collapseExpandedView()
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

}