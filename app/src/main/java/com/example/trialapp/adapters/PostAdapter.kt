package com.example.trialapp.adapters

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trialapp.R
import com.example.trialapp.models.Post
import kotlinx.android.synthetic.main.item_post.view.*
import java.math.BigInteger
import java.security.MessageDigest

class PostAdapter(val context: Context, val posts: List<Post>):
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(post: Post) {
            val username = post.user?.username as String
            itemView.tv_username.text = username
            itemView.tv_description.text = post.description
            Glide.with(context).load(post.imageUrl).into(itemView.iv_post)
            Glide.with(context).load(getProfileImageUrl(username)).into(itemView.ivProfileImage)
            itemView.tv_RelativeTime.text = DateUtils.getRelativeTimeSpanString(post.creationTimeMs)
        }
          private fun getProfileImageUrl(username: String): String{
            val digest = MessageDigest.getInstance("MD5")
            val has = digest.digest(username.toByteArray())
            val bigInt = BigInteger(has)
            val hex = bigInt.abs().toString(16)
            return "http://www.gravatar.com/avatar/$hex?id=identicon"
          }
    }
}