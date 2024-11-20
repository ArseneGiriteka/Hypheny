package com.leilaarsene.hypheny.controller

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.leilaarsene.hypheny.R
import com.leilaarsene.hypheny.data.User
import com.leilaarsene.hypheny.model.ApiService
import com.leilaarsene.hypheny.model.UserManager
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.github.dhaval2404.imagepicker.ImagePicker

class EditProfileFragment(private val activeUserId: String, private val context: Context): Fragment() {
    private var activeUser: User? = null
    private lateinit var profilePicture: ImageView
    private lateinit var usernameText: TextView
    private lateinit var usernameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit1: EditText
    private lateinit var passwordEdit2: EditText
    private lateinit var updateBtn: Button
    private val apiService = ApiService(context)
    private val sharedPreferences: SharedPreferences = this.context.getSharedPreferences("user_session", Context.MODE_PRIVATE)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.fragment_edit_profile, container, false)

        profilePicture = view.findViewById(R.id.profile_picture)
        usernameText = view.findViewById(R.id.username_text)
        usernameEdit = view.findViewById(R.id.username_edit)
        emailEdit = view.findViewById(R.id.email_edit)
        passwordEdit1 = view.findViewById(R.id.password_edit_1)
        passwordEdit2 = view.findViewById(R.id.password_edit_2)
        updateBtn = view.findViewById(R.id.update_btn)


        updateBtn.isEnabled = false

        lifecycleScope.launch {
            activeUser = getUserById(activeUserId)

            activeUser?.let {
                Glide.with(view).load(it.userProfile).placeholder(R.drawable.user_profile).into(profilePicture)
                usernameText.text = it.username
                usernameEdit.setText(it.username)
                emailEdit.setText(it.email)
                updateBtn.isEnabled = true



                updateBtn.setOnClickListener {
                    lifecycleScope.launch {
                        val userJsonData = updateUserProfile(activeUserId, usernameEdit.text.toString(), emailEdit.text.toString(), passwordEdit1.text.toString(), passwordEdit2.text.toString())
                        Log.d("FromProfileFragment", "userdata updated to $userJsonData")
                        userJsonData?.let { sharedPreferences.edit().putString("active_user_data", userJsonData).apply() }
                        usernameText.text = usernameEdit.text
                    }
                }
            }

            profilePicture.setOnClickListener {
                showDialogImage()
            }
        }

        return view
    }

    private fun showDialogImage() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_image_popup, null)
        val popupImageView = dialogView.findViewById<ImageView>(R.id.popupImageView)

        Glide.with(context).load(profilePicture.drawable).placeholder(R.drawable.invalid_image_icon_foreground).into(popupImageView)
        val updateUserProfileButton: ImageView = dialogView.findViewById(R.id.update_profile_image_btn)
        val closeBtn: ImageView = dialogView.findViewById(R.id.close_btn)
        val infoBtn: ImageView = dialogView.findViewById(R.id.info_btn)


        val dialog: Dialog = Dialog(context)
        dialog.setContentView(dialogView)
        dialog.show()

        updateUserProfileButton.setOnClickListener {
        }
        closeBtn.setOnClickListener { dialog.dismiss() }
        infoBtn.setOnClickListener {  }
    }

    private suspend fun getUserById(userId: String): User? {
        return suspendCoroutine { continuation ->
            apiService.findUserByUserId(userId) { success, data ->
                if (success) {
                    val user = data?.let { UserManager().getUserFromJSON(JSONObject(it)) }
                    continuation.resume(user)
                } else continuation.resume(null)
            }
        }
    }

    private suspend fun updateUserProfile(userId: String, username: String, email: String, password1: String, password2: String): String? {
        var password = ""
        if (password1 == password2) {
            password = password1
        }
        return suspendCoroutine { continuation ->
            apiService.updateUserProfile(userId, username, email, password) { success, message, data ->
                if (message != null) {
                    Log.d("FromEditProfileAdapter", message)
                    Log.d("FromEditProfileAdapter", "username: $username")
                    Log.d("FromEditProfileAdapter", "userId: $userId")
                    Log.d("FromEditProfileAdapter", "email: $email")
                    Log.d("FromEditProfileAdapter", "password: $password")
                }
                if (success && data != null) {
                    continuation.resume(data)
                }
                else {
                    continuation.resume(null)
                }

            }
        }
    }
}