package com.leilaarsene.hypheny.controller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.leilaarsene.hypheny.R
import com.leilaarsene.hypheny.model.ApiService
import org.json.JSONObject

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("FromLoginActivityOnCreate", "LoginActivityLogin.onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val apiService = ApiService(this)

        val usernameEditText = findViewById<EditText>(R.id.username_edit)
        val password = findViewById<EditText>(R.id.password_edit)
        val loginButton = findViewById<Button>(R.id.login_btn)
        val errorShowerTextView = findViewById<TextView>(R.id.errors_shower)
        val signupButton = findViewById<Button>(R.id.signin_btn)

        loginButton.setOnClickListener{
            //login(client, usernameEditText.text.toString(), password.text.toString(), errorShowerTextView)
            apiService.login(usernameEditText.text.toString(), password.text.toString()) { success, message ->
                if (success) {
                    //Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ConversationActivity::class.java)
                    intent.putExtra("userData",
                        message?.let { it1 -> JSONObject(it1).getString("userData") })
                    startActivity(intent)
                    finish()
                } else {
                    runOnUiThread {
                        errorShowerTextView.text = "Wrong credintials"
                        errorShowerTextView.isVisible = true
                    }
                }
            }
        }

        signupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}