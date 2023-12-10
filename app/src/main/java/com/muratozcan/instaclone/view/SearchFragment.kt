package com.muratozcan.instaclone.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.muratozcan.instaclone.R
import com.muratozcan.instaclone.adapter.UserAdapter
import com.muratozcan.instaclone.databinding.FragmentSearchBinding
import com.muratozcan.instaclone.model.User
import java.lang.Exception


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentSearchBinding.inflate(inflater,container,false)

        db=Firebase.firestore

        auth = Firebase.auth

        userList= ArrayList<User>()

        binding.btnSearch.setOnClickListener {
            val searchedText = binding.searchEditText.text.toString()
            if (searchedText.isNotEmpty()) {
                getUsers(searchedText)
            } else {
                userList.clear()
                userAdapter.notifyDataSetChanged()
            }
        }

        return binding.root
    }

    private fun getUsers(query:String){
        db.collection("User").whereGreaterThanOrEqualTo("username", query)
            .whereLessThanOrEqualTo("username", query + "\uf8ff").get().addOnSuccessListener { value ->

                if (value!=null){
                    if (!value.isEmpty){

                        userList.clear()

                        for (document in value){
                            val email = document.getString("email") as String
                            val username= document.getString("username") as String
                            val userID=document.id.toString()
                            val user = User(email,username,userID)
                            Log.e("USER","UserID: $userID, Email: $email, Username: $username")
                            userList.add(user)
                        }

                        userAdapter = UserAdapter(userList)
                        binding.recyclerViewSearch.layoutManager = LinearLayoutManager(this.context)
                        binding.recyclerViewSearch.adapter = userAdapter

                        userAdapter.notifyDataSetChanged()
                    }
                }else{
                    Toast.makeText(this.context, "No user with this name was found.", Toast.LENGTH_LONG).show()
                }

        }.addOnFailureListener {
            Log.e("SEARCH","NOT FOUND USER: ${it.localizedMessage}")
        }
    }


}