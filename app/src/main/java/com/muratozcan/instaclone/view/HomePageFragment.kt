package com.muratozcan.instaclone.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.muratozcan.instaclone.R
import com.muratozcan.instaclone.adapter.PostAdapter
import com.muratozcan.instaclone.databinding.FragmentHomePageBinding
import com.muratozcan.instaclone.model.Post
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomePageFragment : Fragment() {

    private lateinit var binding: FragmentHomePageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var postArrayList : ArrayList<Post>
    private lateinit var postAdapter: PostAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomePageBinding.inflate(inflater, container, false)

        auth = Firebase.auth
        db = Firebase.firestore

        postArrayList = ArrayList<Post>()
        getFriends()
        //getData()
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        postAdapter = PostAdapter(this.requireContext(), postArrayList)
        binding.recyclerView.adapter = postAdapter

        return binding.root
    }

    private fun getData(friends: MutableMap<String, Any>?) {
        db.collection("Post").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this.context, error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {

                        val documents = value.documents

                        postArrayList.clear()

                        for (document in documents) {
                            //casting
                            val comment = document.get("comment") as String
                            val username = document.get("username") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val uid = document.get("uid") as String
                            val date = document.get("date") as Timestamp
                            val lat = document.get("lat") as String
                            val lng = document.get("lng") as String

                            val formattedDate = convertTimestampToDateString(date.seconds)

                            val friendList = friends?.get("Follow") as ArrayList<*>

                            if (friendList.contains(uid) || uid == auth.currentUser?.uid) {
                                val post = Post(username, comment, downloadUrl, lat, lng, uid, formattedDate)
                                postArrayList.add(post)
                            }

                        }
                        Log.e("data",postArrayList.toString())

                        postAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun getFriends() {
        auth.currentUser?.let {
            db.collection("Friendship").document(it.uid).addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this.context, error.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (value!= null) {
                        val documents = value.data
                        getData(documents)
                    }
                }
            }
        }
    }

    private fun convertTimestampToDateString(timestamp: Long): String {
        val date = Date(timestamp * 1000)

        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

        return dateFormat.format(date)
    }
}