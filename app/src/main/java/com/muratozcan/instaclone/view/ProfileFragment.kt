package com.muratozcan.instaclone.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.muratozcan.instaclone.R
import com.muratozcan.instaclone.adapter.FriendshipAdapter
import com.muratozcan.instaclone.adapter.PostAdapter
import com.muratozcan.instaclone.adapter.UserAdapter
import com.muratozcan.instaclone.databinding.FragmentProfileBinding
import com.muratozcan.instaclone.model.Friendship
import com.muratozcan.instaclone.model.User


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var friendList: ArrayList<Friendship>
    private lateinit var friendAdapter: FriendshipAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding=FragmentProfileBinding.inflate(inflater,container,false)

        db= Firebase.firestore
        auth = Firebase.auth

        getOwnInformations()

        friendList= ArrayList<Friendship>()

        getFriends()

        binding.buttonLogOut.setOnClickListener {
            logOut()
        }

        return binding.root
    }

    private fun getOwnInformations() {
        if (auth.currentUser!=null){
            val userDoc=db.collection("User").document(auth.currentUser!!.uid).get().addOnSuccessListener{value->
                if (value!=null && value.exists()){
                    binding.textViewUsernameProfile.text=value.getString("username").toString()
                }
            }
        }
    }

    private fun getFriends() {
        friendList.clear()
        friendAdapter = FriendshipAdapter(friendList)
        if (auth.currentUser!=null){
            val docRef=db.collection("Friendship").document(auth.currentUser!!.uid)
            docRef.addSnapshotListener { value,error ->
                if (error!=null){
                    Log.e("ERROR","Error in getFriends()")
                }
                if (value!=null && value.exists()){
                    var followList=ArrayList<String>()
                    if (value.get("Follow")!=null){
                        followList=value.get("Follow") as ArrayList<String>
                    }
                    var followerList = ArrayList<String>()
                    if (value.get("Followers")!=null){
                        followerList=value.get("Followers") as ArrayList<String>
                    }
                    binding.followersCountTextView.text=followList.size.toString()
                    binding.followingCountTextView.text=followerList.size.toString()
                    friendList.clear()
                    friendAdapter = FriendshipAdapter(friendList)
                    for (follow in followList){
                        val userDoc=db.collection("User").document(follow).addSnapshotListener {userInfo,error ->
                            if(error!=null){
                                Log.e("ERROR","Error in getFriends()")
                            }
                            if (userInfo!=null && userInfo.exists()){
                                val email = userInfo.getString("email") as String
                                val username= userInfo.getString("username") as String
                                val userID=userInfo.id.toString()
                                val friend = Friendship(email,username,userID)
                                friendList.add(friend)
                            }
                            binding.recyclerViewFriends.layoutManager = LinearLayoutManager(this.context)
                            friendAdapter = FriendshipAdapter(friendList)
                            binding.recyclerViewFriends.adapter = friendAdapter
                            friendAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    private fun logOut() {
        auth.signOut().run {
            val intent= Intent(activity,AuthActivity::class.java)
            startActivity(intent)
        }
    }

}