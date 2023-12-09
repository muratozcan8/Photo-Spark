package com.muratozcan.instaclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muratozcan.instaclone.databinding.RecyclerRowBinding
import com.muratozcan.instaclone.model.Post
import com.squareup.picasso.Picasso

class PostAdapter(private val postList : ArrayList<Post>) : RecyclerView.Adapter<PostAdapter.PostHoler>() {

    class PostHoler(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHoler {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHoler(binding)
    }

    override fun onBindViewHolder(holder: PostHoler, position: Int) {
        holder.binding.recyclerUsernameText.text = postList[position].username
        holder.binding.recyclerCommentText.text = postList[position].comment
        Picasso.get().load(postList.get(position).downloadUrl).into(holder.binding.recyclerImageView)
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}