package com.leilaarsene.hypheny.controller

import BaseActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.leilaarsene.hypheny.R
import com.leilaarsene.hypheny.model.ApiService

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        val usernameEditView = findViewById<EditText>(R.id.username_edit_text)
        val emailEditView = findViewById<EditText>(R.id.email_edit_text)
        val passwordEditView = findViewById<EditText>(R.id.password_edit_text)

        val apiService = ApiService(this)
        val signupButton = findViewById<Button>(R.id.signup_button)
        val loginButton = findViewById<Button>(R.id.login_btn)

        signupButton.setOnClickListener {
            apiService.signup(usernameEditView.text.toString(), emailEditView.text.toString(), passwordEditView.text.toString()) { success, message ->
                if (success) {
                    //Log.d("FromSignupActivity", "${message}")
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    //Log.d("FromSignupActivity", "${message}")
                }
            }
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}