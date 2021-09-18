package com.example.trialapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trialapp.adapters.PostAdapter
import com.example.trialapp.R
import com.example.trialapp.models.Post
import com.example.trialapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_posts.*

open class PostsActivity : AppCompatActivity() {
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var adapter: PostAdapter
    private var signedInUser: User? = null
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        //create the layout file which represents one post
        //create data source
        posts = mutableListOf()
        //create the adapter
        adapter = PostAdapter(this, posts)
        //bind the adapter and layout manager to rv
        rv_posts.adapter = adapter
        rv_posts.layoutManager = LinearLayoutManager(this)


        firestoreDb = FirebaseFirestore.getInstance()
        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i("---", "Signed in user: $signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.e("---", "onCreate: $exception", )
            }


        var postsReference = firestoreDb
            .collection("posts")
            .limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)

        val user =intent.getStringExtra("EXTRA_USER")
                if (user!=null){
                    supportActionBar?.title = user
                    postsReference = postsReference.whereEqualTo("user.username", user)
                }
            postsReference.addSnapshotListener { snapshot, exception ->
                if (exception != null || snapshot==null){
                    Log.e("---", "exception querying posts ", exception )
                    return@addSnapshotListener
                }
                val postsList = snapshot.toObjects(Post::class.java)
                posts.clear()
                posts.addAll(postsList)
                adapter.notifyDataSetChanged()
                for (document in postsList){
                    Log.i("---", "Document $document")
                }
            }

        fabCreate.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_profile){
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("EXTRA_USER", signedInUser?.username)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}