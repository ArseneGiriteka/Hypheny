package com.leilaarsene.hypheny.controller

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leilaarsene.hypheny.R
import com.leilaarsene.hypheny.data.User
import com.leilaarsene.hypheny.model.ApiService
import com.leilaarsene.hypheny.model.UserManager
import com.leilaarsene.hypheny.view.AddContactItemAdapter
import com.leilaarsene.hypheny.view.PendingContactItemAdapter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ContactsFragments(private val activeUserId: String, private val context: Context) : Fragment() {
    private lateinit var listOfPendingUsers: List<User>
    private lateinit var listOfUsersToAdd: MutableList<User>
    private lateinit var pendingContactsRecyclerView: RecyclerView
    private lateinit var contactsToAddRecyclerView: RecyclerView
    private lateinit var pendingContactItemAdapter: PendingContactItemAdapter
    private lateinit var addContactItemAdapter: AddContactItemAdapter
    private val apiService = ApiService(context)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        pendingContactsRecyclerView = view.findViewById<RecyclerView>(R.id.pending_contacts_recyclerview)
        contactsToAddRecyclerView = view.findViewById<RecyclerView>(R.id.contacts_to_add_recyclerview)

        pendingContactsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        listOfPendingUsers = listOf()
        pendingContactItemAdapter = PendingContactItemAdapter(activeUserId, listOfPendingUsers.toMutableList(), context)
        pendingContactsRecyclerView.adapter = pendingContactItemAdapter

        contactsToAddRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        listOfUsersToAdd = mutableListOf()
        addContactItemAdapter = AddContactItemAdapter(activeUserId, listOfUsersToAdd, context)
        contactsToAddRecyclerView.adapter = addContactItemAdapter

        lifecycleScope.launch {
            listOfPendingUsers = getPendingUsersByUserId(activeUserId)
            pendingContactItemAdapter = PendingContactItemAdapter(activeUserId, listOfPendingUsers.toMutableList(), context)
            pendingContactsRecyclerView.adapter = pendingContactItemAdapter
        }

        lifecycleScope.launch {
            listOfUsersToAdd = getUserToAddByUserId(activeUserId)
            addContactItemAdapter = AddContactItemAdapter(activeUserId, listOfUsersToAdd, context)
            contactsToAddRecyclerView.adapter = addContactItemAdapter
        }


        Log.d("FomContactsFragments", "pendings: $listOfPendingUsers ------- toAdds: $listOfUsersToAdd")
        return view
    }

    private suspend fun getPendingUsersByUserId(userId: String): List<User> {
        return suspendCoroutine { continuation ->
            apiService.queryPendingContacts(activeUserId){ success, data ->
                if (success) {
                    if (data != null) {
                        val pendingUsers = UserManager().getUsersListFromJsonArray(JSONArray(data))
                        continuation.resume(pendingUsers)
                    } else {
                        continuation.resume(listOf())
                    }
                }
            }
        }
    }

    private suspend fun getUserToAddByUserId(userId: String): MutableList<User> {
        return suspendCoroutine { continuation ->
            apiService.getUsersToAdd(activeUserId) { success, _, data ->
                if (success) {
                    if (data != null) {
                        val usersToAdd = UserManager().getUsersListFromJsonArray(JSONArray(data)).toMutableList()
                        continuation.resume(usersToAdd)
                    } else {
                        continuation.resume(mutableListOf())
                    }
                }
            }
        }
    }
}