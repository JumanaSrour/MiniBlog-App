package com.example.trialapp.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.trialapp.R
import com.example.trialapp.models.Post
import com.example.trialapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create.*

class CreateActivity : AppCompatActivity() {
    private var signedInUser: User? = null
    private var photoUri: Uri? = null
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        storageReference = FirebaseStorage.getInstance().reference

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

        btnPickImage.setOnClickListener {
            Log.i("---", "Open image picker in device")
            val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type = "image/*"
            if (imagePickerIntent.resolveActivity(packageManager)!= null){
                startActivityForResult(imagePickerIntent, PICK_PHOTO_CODE)
            }
        }

        btnSubmit.setOnClickListener {
            handleSubmitButtonClick()
        }
    }

    private fun handleSubmitButtonClick() {
        if (photoUri == null){
            Toast.makeText(applicationContext, "No photo selected", Toast.LENGTH_SHORT).show()
            return
        }
        if (etDescription.text.isBlank()){
            Toast.makeText(applicationContext, "Description can't be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (signedInUser==null){
            Toast.makeText(applicationContext, "No signed in user, please wait", Toast.LENGTH_SHORT).show()
            return
        }

        btnSubmit.isEnabled = false
        val photoUploadUri = photoUri as Uri
        val photoReference = storageReference.child("images/${System.currentTimeMillis()}-photo.jpg")
        // Upload photo to firebase storage
        photoReference.putFile(photoUploadUri)
            .continueWithTask { photoUploadTask ->
                Log.e("---", "uploaded bytes: ${photoUploadTask.result?.bytesTransferred}", )
                //Retrieve image url of the uploaded photo
                 photoReference.downloadUrl
            }.continueWithTask{ downloadUrlTask ->
                // Create a post object with the image url and add that to the posts collection
                val post = Post(
                    etDescription.text.toString(),
                    downloadUrlTask.result.toString(),
                    System.currentTimeMillis(),
                    signedInUser
                )
                firestoreDb.collection("posts").add(post)
            }.addOnCompleteListener { postCreationTask ->
                btnSubmit.isEnabled = true
                if (!postCreationTask.isSuccessful){
                    Log.e("---", "Exception during firebase opts: ", postCreationTask.exception)
                    Toast.makeText(applicationContext, "Failed to save post", Toast.LENGTH_SHORT).show()
                }
                etDescription.text.clear()
                ivAvatar.setImageResource(0)
                Toast.makeText(applicationContext, "Post was saved successfully", Toast.LENGTH_SHORT).show()
                val profileIntent = Intent(this, ProfileActivity::class.java)
                profileIntent.putExtra("EXTRA_USER", signedInUser?.username)
                startActivity(profileIntent)
                finish()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO_CODE){
            if (resultCode == Activity.RESULT_OK){
                 photoUri = data?.data
                Log.i("---", "onActivityResult: photoUri $photoUri ")
                ivAvatar.setImageURI(photoUri)
            } else {
                Toast.makeText(applicationContext, "Image picking was canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {
        private const val PICK_PHOTO_CODE = 1234
    }
}