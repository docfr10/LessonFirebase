package com.example.lesson_firebase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.lesson_firebase.model.UserModel
import com.example.lesson_firebase.ui.theme.LessonFirebaseTheme
import com.example.lesson_firebase.view.AppScreen
import com.example.lesson_firebase.view.AuthenticationScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class MainActivity : ComponentActivity() {
    private var auth = FirebaseAuth.getInstance()
    private val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private var cUser = auth.currentUser
    private val storage = Firebase.storage
    private val databaseReference = FirebaseDatabase.getInstance().getReference("USERS/${auth.uid}")
    private var imagesReference: StorageReference? =
        storage.reference.child("images/${auth.uid}/${databaseReference.push().key}")

    private val userData = mutableStateOf<List<UserModel>>(listOf())

    private val startForResultSignIn =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    val idToken = account.idToken
                    // Authenticate with Firebase Authentication using the ID token
                    firebaseAuthWithGoogle(idToken!!)
                } catch (e: ApiException) {
                    // Handle sign-in failure
                }
            }
        }

    private val startForResultImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val imageUri = it.data!!.data
                uploadImage(imageUri!!)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LessonFirebaseTheme {
                val context = LocalContext.current
                val activity = LocalContext.current as Activity
                val navController = rememberNavController()

                val configSettings = FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .setFetchTimeoutInSeconds(60)
                    .build()
                firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
                firebaseRemoteConfig.activate()
                firebaseRemoteConfig.fetch()

                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                val googleSignInClient = GoogleSignIn.getClient(this, gso)

                if (cUser == null)
                    AuthenticationScreen(
                        auth = auth,
                        context = context,
                        googleSignInClient = googleSignInClient,
                        startForResultSignIn = startForResultSignIn
                    )
                else
                    AppScreen(
                        activity = activity,
                        auth = auth,
                        context = context,
                        cUser = cUser!!,
                        databaseReference = databaseReference,
                        imagesReference = imagesReference,
                        firebaseRemoteConfig = firebaseRemoteConfig,
                        navController = navController,
                        startForResultImage = startForResultImage,
                        userData = userData
                    )
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    cUser = auth.currentUser
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                } else {
                    // Authentication failed
                }
            }
    }

    private fun uploadImage(imageUri: Uri) {
        val imageRef = imagesReference!!
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            // Image uploaded successfully
            imageRef.downloadUrl.addOnSuccessListener {
                // Use the download URL to display the uploaded image
            }
        }
    }
}