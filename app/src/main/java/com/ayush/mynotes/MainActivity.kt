package com.ayush.mynotes


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import app.rive.runtime.kotlin.core.Rive
import com.ayush.mynotes.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

interface FragmentInterface {
    fun updateVariable(value: Int)
    fun getVariable() : Int
}

internal lateinit var tokenID: String


class MainActivity : AppCompatActivity(), FragmentInterface {
    private lateinit var fragmentManger: FragmentManager
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val stateMachineName: String = "State Machine 1"
    private var notesCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        auth = FirebaseAuth.getInstance()
        tokenID = auth.currentUser?.uid.toString()
        Log.i("token", tokenID)
        Rive.init(this)


        val addNotesFragment = AddNotesFragment()
        val myNotesFragment = MyNotesFragment()
        fragmentManger = supportFragmentManager


        val db = FirebaseFirestore.getInstance()

        db.document("users/$tokenID").get().addOnSuccessListener {
            val name = "Hi, " + it.data?.get("name").toString().trimEnd()
            val body = it.data?.get("body").toString().toFloat()
            val eye = it.data?.get("eye").toString().toFloat()
            val mouth = it.data?.get("mouth").toString().toFloat()

            mainBinding.avatarAnimation.controller.setNumberState(stateMachineName, "Bodies", body)
            mainBinding.avatarAnimation.controller.setNumberState(stateMachineName, "Eyes", eye)
            mainBinding.avatarAnimation.controller.setNumberState(stateMachineName, "Mouth", mouth)
            mainBinding.userName.text = name
            mainBinding.avatarAnimation.visibility = View.VISIBLE
            mainBinding.userName.visibility = View.VISIBLE

        }.addOnFailureListener {
            Log.e("Error", it.stackTraceToString())
        }

        try {
            db.collection("Notes/$tokenID/note").get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty){
                        Toast.makeText(this, "Add a new Note and Begin your Journey!!", Toast.LENGTH_SHORT).show()
                        mainBinding.loadingAnimation.visibility = View.GONE
                        goToFragment(addNotesFragment)
                    } else {
                        val sortedNotes = result.documents.sortedByDescending {
                            it.data?.get("id").toString().toInt()
                        }
                        notesCount = sortedNotes.first().data?.get("id").toString().toInt() + 1
                        goToFragment(myNotesFragment)
                        mainBinding.loadingAnimation.visibility = View.GONE
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents.", exception)
                }
        } catch (e: Exception){
            Log.e("ERROR",e.stackTraceToString())
            goToFragment(addNotesFragment)
        }

        mainBinding.logOutAnimation.controller.setBooleanState(stateMachineName, "ON", true)

        mainBinding.addNoteBtn.setOnClickListener {
            goToFragment(addNotesFragment)

        }


        mainBinding.logOutBtn.setOnClickListener {

            mainBinding.logOutAnimation.controller.setBooleanState(stateMachineName, "ON", false)

            Handler(mainLooper).postDelayed({
                val dialogBuilder = MaterialAlertDialogBuilder(this)

                dialogBuilder.setTitle("Do you really want to Sign out?")

                dialogBuilder.setPositiveButton("OK") { _, _ ->
                    auth.signOut()
                    startActivity(Intent(this, SignInActivity::class.java))
                    Toast.makeText(this, "Logged Out Successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                dialogBuilder.setNegativeButton("Cancel") { _, _ ->
                    mainBinding.logOutAnimation.controller.setBooleanState(
                        stateMachineName,
                        "ON",
                        true
                    )
                }
                dialogBuilder.show()

            }, 500)

        }
    }
    private fun goToFragment(fragment: Fragment) {
        fragmentManger = supportFragmentManager
        fragmentManger.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
    }

    override fun updateVariable(value: Int) {
        notesCount = value
    }

    override fun getVariable(): Int = notesCount


}