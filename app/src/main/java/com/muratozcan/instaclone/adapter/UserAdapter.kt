package com.muratozcan.instaclone.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muratozcan.instaclone.model.User
import androidx.annotation.NonNull
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.muratozcan.instaclone.databinding.UserItemLayoutBinding

class UserAdapter (private var userList:ArrayList<User>):RecyclerView.Adapter<UserAdapter.UserHolder>(){

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    class UserHolder(val binding: UserItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val binding = UserItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserHolder(binding)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user= userList[position]
        holder.binding.userNameSearch.text=user.username

        firestore= Firebase.firestore
        auth = Firebase.auth

        if (auth.currentUser!=null){
            val docRef = firestore.collection("Friendship").document(auth.currentUser!!.uid)
            docRef.get().addOnSuccessListener { value ->
                if (value != null && value.exists()) {
                    if (value.data?.get("Follow")!=null){
                        var followers = value.data?.get("Follow") as ArrayList<String>
                        if (followers.contains(user.userID)){
                            holder.binding.followBtnSearch.text="Unfollow"
                        }else{
                            holder.binding.followBtnSearch.text="Follow"
                        }
                    }
                } else {
                    Log.d("Error", "Current data: null")
                }
            }
        }

        holder.binding.followBtnSearch.setOnClickListener {
            if (holder.binding.followBtnSearch.text.toString()=="Follow"){
                if (auth.currentUser!=null){
                    val docRef = firestore.collection("Friendship").document(auth.currentUser!!.uid)
                    docRef.get().addOnSuccessListener { value ->
                        if (value != null && value.exists()) {
                            firestore.collection("Friendship").document(auth.currentUser!!.uid)
                                .update("Follow",FieldValue.arrayUnion(user.userID))
                                .addOnSuccessListener {
                                    holder.binding.followBtnSearch.text="Unfollow"
                                }.addOnFailureListener {
                                    Log.e("FOLLOW","Error Occured When Following New Friend")
                                }
                            firestore.collection("Friendship").document(user.userID)
                                .update("Followers",FieldValue.arrayUnion(auth.currentUser!!.uid))
                                .addOnSuccessListener {
                                    holder.binding.followBtnSearch.text="Unfollow"
                                }.addOnFailureListener {
                                    Log.e("FOLLOWERS","Error Occured When Following New Friend")
                                }
                        } else {
                            Log.d("Error", "Current data: null")
                        }
                    }
                }
            }else{

                if (auth.currentUser!=null){
                    val docRef = firestore.collection("Friendship").document(auth.currentUser!!.uid)
                    docRef.get().addOnSuccessListener { value ->
                        if (value != null && value.exists()) {
                            firestore.collection("Friendship").document(auth.currentUser!!.uid)
                                .update("Follow",FieldValue.arrayRemove(user.userID))
                                .addOnSuccessListener {
                                    holder.binding.followBtnSearch.text="Follow"
                                }.addOnFailureListener {
                                    Log.e("FOLLOW","Error Occured When Unfollowing Friend")
                                }
                            firestore.collection("Friendship").document(user.userID)
                                .update("Followers",FieldValue.arrayRemove(auth.currentUser!!.uid))
                                .addOnSuccessListener {
                                    holder.binding.followBtnSearch.text="Follow"
                                }.addOnFailureListener {
                                    Log.e("FOLLOW","Error Occured When Unfollowing Friend")
                                }
                        } else {
                            Log.d("Error", "Current data: null")
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }



}