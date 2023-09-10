package com.ayush.mynotes

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import app.rive.runtime.kotlin.core.Rive
import com.ayush.mynotes.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    
    private lateinit var signInBinding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private val stateMachineName: String = "Login Machine"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(signInBinding.root)

        auth = FirebaseAuth.getInstance()

        Rive.init(this)

        signInBinding.signUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        signInBinding.fabBack.setOnClickListener {
            onBackPressed()
        }

        signInBinding.btnSignIn.setOnClickListener {
            signInBinding.etPassword.clearFocus()
            Toast.makeText(
                this,
                "Signing In!",
                Toast.LENGTH_SHORT
            )
                .show()


            Handler(mainLooper).postDelayed({
                try {
                    signInBinding.btnSignIn.isEnabled = false
                    auth.signInWithEmailAndPassword(
                        signInBinding.etEmail.text.toString(),
                        signInBinding.etPassword.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            signInBinding.loginCharacter.controller.fireState(
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
                            }, 400)
                        } else {
                            signInBinding.btnSignIn.isEnabled = true
                            signInBinding.loginCharacter.controller.fireState(
                                stateMachineName,
                                "trigFail"
                            )
                            Handler(mainLooper).postDelayed({
                                Toast.makeText(
                                    this,
                                    it.exception?.localizedMessage.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },500)
                        }
                    }
                } catch (e: Exception) {
                    signInBinding.btnSignIn.isEnabled = true
                    signInBinding.loginCharacter.controller.fireState(stateMachineName, "trigFail")
                    Toast.makeText(this, "Incorrect Email or Password", Toast.LENGTH_SHORT)
                        .show()
                }
            }, 50)
        }

        signInBinding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                signInBinding.loginCharacter.controller.setBooleanState(
                    stateMachineName,
                    "isChecking",
                    true
                )
            } else {
                signInBinding.loginCharacter.controller.setBooleanState(
                    stateMachineName,
                    "isChecking",
                    false
                )
            }
        }

        signInBinding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                signInBinding.loginCharacter.controller.setBooleanState(
                    stateMachineName,
                    "isHandsUp",
                    true
                )
            } else {
                signInBinding.loginCharacter.controller.setBooleanState(
                    stateMachineName,
                    "isHandsUp",
                    false
                )
            }
        }

        signInBinding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (signInBinding.etEmail.text.toString().contains("@gmail.com", false)) {
                    signInBinding.ForgotPas.isEnabled = true
                    signInBinding.ForgotPas.setTextColor(Color.BLUE)
                } else {
                    signInBinding.ForgotPas.isEnabled = false
                    signInBinding.ForgotPas.setTextColor(Color.GRAY)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (signInBinding.etEmail.text.toString().contains("@gmail.com", false)) {
                    signInBinding.ForgotPas.isEnabled = true
                    signInBinding.ForgotPas.setTextColor(Color.BLUE)
                } else {
                    signInBinding.ForgotPas.isEnabled = false
                    signInBinding.ForgotPas.setTextColor(Color.GRAY)
                }
            }

            override fun afterTextChanged(s: Editable?) {

                try {
                    signInBinding.loginCharacter.controller.setNumberState(
                        stateMachineName,
                        "numLook",
                        s!!.length.toFloat()*1.9f
                    )
                } catch (_: Exception) {

                }

                if (signInBinding.etEmail.text.toString().contains("@gmail.com", false)) {
                    signInBinding.ForgotPas.isEnabled = true
                    signInBinding.ForgotPas.setTextColor(Color.BLUE)
                } else {
                    signInBinding.ForgotPas.isEnabled = false
                    signInBinding.ForgotPas.setTextColor(Color.GRAY)
                }
            }
        })

        signInBinding.ForgotPas.setOnClickListener {
            auth.sendPasswordResetEmail(signInBinding.etEmail.text.toString()).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Reset Code Sent Successfully!\nCheck your mail!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
    }


}