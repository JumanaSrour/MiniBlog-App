package com.example.trialapp.activities

import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.trialapp.R
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : PostsActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_logout){
            Log.i("---", "Logout")
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}