package com.example.lesson_firebase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.lesson_firebase.model.UserModel
import com.example.lesson_firebase.ui.theme.LessonFirebaseTheme
import com.example.lesson_firebase.view.AppScreen
import com.example.lesson_firebase.view.AuthenticationScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class MainActivity : ComponentActivity() {
    private var auth = FirebaseAuth.getInstance()
    private val cUser = auth.currentUser
    private val storage = Firebase.storage
    private val databaseReference = FirebaseDatabase.getInstance().getReference("USERS/${auth.uid}")
    private var imagesReference: StorageReference? = storage.reference.child("images/${auth.uid}")

    private val userData = mutableStateOf<List<UserModel>>(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LessonFirebaseTheme {
                val context = LocalContext.current
                val activity = LocalContext.current as Activity
                val navController = rememberNavController()

                if (cUser == null)
                    AuthenticationScreen(auth = auth, context = context)
                else
                    AppScreen(
                        activity = activity,
                        auth = auth,
                        context = context,
                        cUser = cUser,
                        databaseReference = databaseReference,
                        navController = navController,
                        storage = storage,
                        userData = userData
                    )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            uploadImage(imageUri!!)
        }
    }

    private fun uploadImage(imageUri: Uri) {
        val imageRef = imagesReference!!.child(imageUri.lastPathSegment!!)
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            // Image uploaded successfully
            imageRef.downloadUrl.addOnSuccessListener {
                // Use the download URL to display the uploaded image
            }
        }
    }
}