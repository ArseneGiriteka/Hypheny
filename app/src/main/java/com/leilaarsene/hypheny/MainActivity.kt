package com.leilaarsene.hypheny

import BaseActivity
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import com.leilaarsene.hypheny.controller.LoginActivity
import com.leilaarsene.hypheny.controller.SignupActivity
import com.leilaarsene.hypheny.model.ApiService


class MainActivity : BaseActivity() {

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data

            val resultData = data?.getStringExtra("message")
            Log.d("FromMainActivity", "$resultData")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.login_btn)
        val signupButton = findViewById<Button>(R.id.signup_btn)

        val apiService = ApiService(this)
        Log.d("FromMainAct", "In MainActivity")

        loginButton.setOnClickListener {
            Log.d("FromMainActivity.onCreate.loginbtn.clickListener", "MainActivity.loginButtonClicked")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}