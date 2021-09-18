package com.example.trialapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.trialapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            getPostsActivity()
        }

        btn_Login.setOnClickListener {
            btn_Login.isEnabled = false
            val  email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email.isBlank() || password.isBlank()){
                Toast.makeText(applicationContext, "Email or Password can't be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Firebase authenticate check
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task->
                    btn_Login.isEnabled = true
                    if (task.isSuccessful){
                        Toast.makeText(applicationContext, "Successfully logged in", Toast.LENGTH_SHORT).show()
                        getPostsActivity()
                    } else{
                        Log.e("LoginActivity", "Sign in with email failed ", task.exception)
                        Toast.makeText(applicationContext, "authentication with email failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun getPostsActivity() {
        Log.i("LoginActivity", "getPostsActivity: ")
        startActivity(Intent(this, PostsActivity::class.java))
        finish()
    }


}