package com.muratozcan.instaclone.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.muratozcan.instaclone.databinding.FragmentProfileBinding
import com.muratozcan.instaclone.databinding.FriendItemLayoutBinding
import com.muratozcan.instaclone.model.Friendship

class FriendshipAdapter(private var friendList:ArrayList<Friendship>): RecyclerView.Adapter<FriendshipAdapter.FriendHolder>() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    class FriendHolder(val binding: FriendItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder {
        val binding = FriendItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendshipAdapter.FriendHolder, position: Int) {
        val friend= friendList[position]
        holder.binding.textViewFriendUsername.text=friend.username
        holder.binding.textViewFriendEmail.text=friend.email

        firestore= Firebase.firestore
        auth = Firebase.auth

        if (auth.currentUser!=null){
            val docRef = firestore.collection("Friendship").document(auth.currentUser!!.uid)
            docRef.addSnapshotListener { value,error ->
                if(error!=null){
                    Log.e("ERROR","Error in getFriends()")
                }
                if (value != null && value.exists()) {
                    var followers = value.data?.get("Follow") as ArrayList<String>
                    if (followers.contains(friend.userID)){
                        holder.binding.buttonFriendFollow.text="Unfollow"
                    }else{
                        holder.binding.buttonFriendFollow.text="Follow"
                    }
                } else {
                    Log.d("Error", "Current data: null")
                }
            }
        }

        holder.binding.buttonFriendFollow.setOnClickListener {
            if (auth.currentUser!=null){
                val docRef = firestore.collection("Friendship").document(auth.currentUser!!.uid)
                docRef.get().addOnSuccessListener { value ->
                    if (value != null && value.exists()) {
                        firestore.collection("Friendship").document(auth.currentUser!!.uid).update("Follow",
                            FieldValue.arrayRemove(friend.userID))
                            .addOnSuccessListener {
                                holder.binding.buttonFriendFollow.text="Follow"
                            }.addOnFailureListener {
                                Log.e("FOLLOW","Error Occured When Unfollowing Friend")
                            }
                        firestore.collection("Friendship").document(friend.userID)
                            .update("Followers",FieldValue.arrayRemove(auth.currentUser!!.uid))
                            .addOnSuccessListener {
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

    override fun getItemCount(): Int {
        return friendList.size
    }

}