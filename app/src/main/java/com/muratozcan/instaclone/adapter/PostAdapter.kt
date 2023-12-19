package com.muratozcan.instaclone.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.muratozcan.instaclone.R
import com.muratozcan.instaclone.databinding.RecyclerRowBinding
import com.muratozcan.instaclone.model.Post
import com.muratozcan.instaclone.view.MapsActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kotlinx.coroutines.newFixedThreadPoolContext
import java.net.URL


class PostAdapter(private val context: Context, private val postList : ArrayList<Post>) : RecyclerView.Adapter<PostAdapter.PostHoler>() {

    class PostHoler(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHoler {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHoler(binding)
    }

    override fun onBindViewHolder(holder: PostHoler, position: Int) {

        val transformation: Transformation = object : Transformation {
            override fun transform(source: Bitmap): Bitmap {
                var width = source.width
                var height = source.height

                //val targetWidth: Int = holder.binding.recyclerImageView.width
                val aspectRatio = width.toDouble() / height.toDouble()

                if (aspectRatio > 1) {
                    val scaledHeight = width / aspectRatio
                    height = scaledHeight.toInt()
                } else {
                    val scaledWidth = height * aspectRatio
                    width = scaledWidth.toInt()
                }
                //val targetHeight = (targetWidth * aspectRatio).toInt()
                val result = Bitmap.createScaledBitmap(source, width, height, true)
                if (result != source) {
                    source.recycle()
                }
                return result
            }
            override fun key(): String {
                return "transformation" + " desiredWidth"
            }
        }

        holder.binding.recyclerUsernameText.text = postList[position].username
        holder.binding.recyclerCommentText.text = postList[position].comment

        Picasso.get().load(postList[position].downloadUrl).transform(transformation).into(holder.binding.recyclerImageView, object:
            Callback {
            override fun onSuccess() {
                holder.binding.recyclerViewProgressBar.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                Log.e("Error", "Not Loaded")
            }
        })

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