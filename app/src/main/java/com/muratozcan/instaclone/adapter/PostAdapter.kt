package com.muratozcan.instaclone.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.muratozcan.instaclone.R
import com.muratozcan.instaclone.databinding.RecyclerRowBinding
import com.muratozcan.instaclone.model.Post
import com.muratozcan.instaclone.view.HomePageFragment
import com.muratozcan.instaclone.view.MainActivity
import com.muratozcan.instaclone.view.MapsActivity
import com.squareup.picasso.Picasso

class PostAdapter(private val context: Context, private val postList : ArrayList<Post>) : RecyclerView.Adapter<PostAdapter.PostHoler>() {

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
        holder.binding.recyclerDate.text = postList[position].date

        holder.binding.recyclerLocation.setOnClickListener {
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra("lat", postList[position].lat)
            intent.putExtra("lng", postList[position].lng)
            intent.putExtra("username", postList[position].username)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}