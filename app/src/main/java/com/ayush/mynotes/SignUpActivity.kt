package com.ayush.mynotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import app.rive.runtime.kotlin.core.Rive
import com.ayush.mynotes.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var signupBinding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private val stateMachineName: String = "Login Machine"
    private val stateMachine2: String = "State Machine 1"
    private var inputType: String = "Pressed"
    private var body: Float = 0f
    private var eye: Float = 0f
    private var mouth: Float = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(signupBinding.root)

        auth = FirebaseAuth.getInstance()

        val db = FirebaseFirestore.getInstance().collection("users")

        Rive.init(this)

        signupBinding.profilePic.setOnClickListener {

            signupBinding.profileAnimation.controller.fireState(stateMachine2, inputType)
            Toast.makeText(
                this,
                "This is a one time event. Choose Avatar Carefully!!",
                Toast.LENGTH_SHORT
            ).show()

            Handler(mainLooper).postDelayed({

                startActivity(
                    Intent(this, AvatarActivity::class.java).putExtra(
                        "nameInfo",
                        signupBinding.profileEt.text.toString()
                    ).putExtra("emailInfo", signupBinding.etEmail.text.toString())
                )
                super.onPause()
            }, 101)
        }

        signupBinding.profileEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                signupBinding.signupCharacter.controller.setBooleanState(
                    stateMachineName,
                    "isChecking",
                    true
                )
            } else {
                signupBinding.signupCharacter.controller.setBooleanState(
                    stateMachineName,
                    "isChecking",
                    false
                )
            }
        }

        signupBinding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                signupBinding.signupCharacter.controller.setBooleanState(
                    stateMachineName,
                    "isChecking",
                    true
                )
            } else {
                signupBinding.signupCharacter.controller.setBooleanState(
                    stateMachineName,
                    "isChecking",
                    false
                )
            }
        }

        signupBinding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                signupBinding.btnSignUp.isEnabled =
                    (signupBinding.etPassword.text.toString() == signupBinding.etConfirmPassword.text.toString()) && signupBinding.etEmail.text.isNotEmpty() && (eye != 0f) && signupBinding.profileEt.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    signupBinding.signupCharacter.controller.setNumberState(
                        stateMachineName,
                        "numLook",
                        s!!.length.toFloat() * 1.9f
                    )
                } catch (_: Exception) {

                }
            }
        })

        signupBinding.profileEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                signupBinding.btnSignUp.isEnabled =
                    (signupBinding.etPassword.text.toString() == signupBinding.etConfirmPassword.text.toString()) && signupBinding.etEmail.text.isNotEmpty() && (eye != 0f) && signupBinding.profileEt.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    signupBinding.signupCharacter.controller.setNumberState(
                        stateMachineName,
                        "numLook",
                        s!!.length.toFloat() * 1.9f + 10f
                    )
                } catch (_: Exception) {

                }
            }
        })

        signupBinding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                signupBinding.signupCharacter.controller.setBooleanState(
                    stateMachineName,
                    "isHandsUp",
                    true
                )
            } else {
                signupBinding.signupCharacter.controller.setBooleanState(
                    stateMachineName,
                    "isHandsUp",
                    false
                )
            }
        }

        signupBinding.etConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                signupBinding.signupCharacter.controller.setBooleanState(
                    stateMachineName,
                    "isHandsUp",
                    true
                )
            } else {
                signupBinding.signupCharacter.controller.setBooleanState(
                    stateMachineName,
                    "isHandsUp",
                    false
                )
            }
        }

        signupBinding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                signupBinding.btnSignUp.isEnabled =
                    (signupBinding.etPassword.text.toString() == signupBinding.etConfirmPassword.text.toString()) && signupBinding.etEmail.text.isNotEmpty() && (eye != 0f) && signupBinding.profileEt.text.isNotEmpty()
            }
        })

        signupBinding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                signupBinding.btnSignUp.isEnabled =
                    (signupBinding.etPassword.text.toString() == signupBinding.etConfirmPassword.text.toString()) && signupBinding.etEmail.text.isNotEmpty() && (eye != 0f) && signupBinding.profileEt.text.isNotEmpty()
            }
        })

        signupBinding.btnSignUp.setOnClickListener {
            signupBinding.etPassword.clearFocus()
            signupBinding.etConfirmPassword.clearFocus()
            signupBinding.etEmail.clearFocus()

            Handler(mainLooper).postDelayed({
                try {
                    val email = signupBinding.etEmail.text.toString()
                    val pass = signupBinding.etPassword.text.toString()
                    signupBinding.btnSignUp.isEnabled = false
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {

                            val user = hashMapOf(
                                "name" to signupBinding.profileEt.text.toString(),
                                "body" to body,
                                "eye" to eye,
                                "mouth" to mouth
                            )

                            db.document(auth.uid.toString()).set(user)

                            signupBinding.signupCharacter.controller.fireState(
                                stateMachineName,
                                "trigSuccess"
                            )

                            Handler(mainLooper).postDelayed({
                                startActivity(Intent(this, MainActivity::class.java))
                                Toast.makeText(
                                    this,
                                    "Account SignedIn Successfully!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                finish()
                            }, 1535)

                        } else {
                            signupBinding.btnSignUp.isEnabled = true
                            signupBinding.signupCharacter.controller.fireState(
                                stateMachineName,
                                "trigFail"
                            )
                            Handler(mainLooper).postDelayed({
                                Toast.makeText(
                                    this,
                                    it.exception?.localizedMessage.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }, 500)
                        }
                    }
                } catch (e: Exception) {
                    signupBinding.btnSignUp.isEnabled = true
                    signupBinding.signupCharacter.controller.fireState(stateMachineName, "trigFail")
                    Toast.makeText(this, e.stackTraceToString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }, 500)
        }


        signupBinding.signIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        signupBinding.fabBack.setOnClickListener {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        body = intent.getFloatExtra("body", 0f)
        eye = intent.getFloatExtra("eye", 0f)
        mouth = intent.getFloatExtra("mouth", 0f)
        signupBinding.profileEt.setText(intent.getStringExtra("nameInfo"))
        signupBinding.etEmail.setText(intent.getStringExtra("emailInfo"))
        Log.i("body", body.toString())
        Log.i("eye", eye.toString())
        Log.i("mouth", mouth.toString())
        if (eye != 0f) {
            signupBinding.profilePic.isEnabled = false
            signupBinding.profileAnimation.setRiveResource(R.raw.avatar_prototype)
            signupBinding.profileAnimation.controller.setNumberState(stateMachine2, "Bodies", body)
            signupBinding.profileAnimation.controller.setNumberState(stateMachine2, "Eyes", eye)
            signupBinding.profileAnimation.controller.setNumberState(stateMachine2, "Mouth", mouth)
        }

    }
}