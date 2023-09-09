package com.ayush.mynotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import com.ayush.mynotes.databinding.ActivityAvatarBinding


class AvatarActivity : AppCompatActivity() {
    private lateinit var avatarBinding: ActivityAvatarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        avatarBinding = ActivityAvatarBinding.inflate(layoutInflater)
        setContentView(avatarBinding.root)
        var eye = 0f
        var body = 0f
        var mouth = 0f

        avatarBinding.bodySeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                body = progress.toFloat()
                avatarBinding.avatarAnimation.controller.setNumberState("State Machine 1","Bodies" , body)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        avatarBinding.eyeSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                eye = progress.toFloat()
                if (eye!= 0f){
                    avatarBinding.button.isEnabled = true
                }
                avatarBinding.avatarAnimation.controller.setNumberState("State Machine 1","Eyes" , eye)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        avatarBinding.mouthSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mouth = progress.toFloat()
                avatarBinding.avatarAnimation.controller.setNumberState("State Machine 1","Mouth" , mouth)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        avatarBinding.button.setOnClickListener {
            if (eye != 0f) {
                startActivity(
                    Intent(this, SignUpActivity::class.java).putExtra("body", body)
                        .putExtra("eye", eye).putExtra("mouth", mouth).putExtra("emailInfo",intent.getStringExtra("emailInfo")).putExtra("nameInfo",intent.getStringExtra("nameInfo"))
                )
                finish()
            } else {
                Toast.makeText(this, "Eye must exist!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}