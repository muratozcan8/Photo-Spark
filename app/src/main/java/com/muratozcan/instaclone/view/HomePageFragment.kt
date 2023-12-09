package com.muratozcan.instaclone.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.muratozcan.instaclone.R
import com.muratozcan.instaclone.adapter.PostAdapter
import com.muratozcan.instaclone.databinding.FragmentHomePageBinding
import com.muratozcan.instaclone.model.Post

class HomePageFragment : Fragment() {

    private lateinit var binding: FragmentHomePageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var postArrayList : ArrayList<Post>
    private lateinit var postAdapter: PostAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomePageBinding.inflate(inflater, container, false)

        db = Firebase.firestore

        postArrayList = ArrayList<Post>()

        getData()
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        postAdapter = PostAdapter(postArrayList)
        binding.recyclerView.adapter = postAdapter

        return binding.root
    }

    private fun getData() {
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

                            val post = Post(username, comment, downloadUrl, "0", "0")
                            postArrayList.add(post)
                        }

                        postAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}