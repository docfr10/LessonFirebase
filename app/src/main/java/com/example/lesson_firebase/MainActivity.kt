package com.example.lesson_firebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.example.lesson_firebase.ui.theme.LessonFirebaseTheme
import com.example.lesson_firebase.view.AuthenticationScreen
import com.example.lesson_firebase.view.HomeScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    private var auth = FirebaseAuth.getInstance()
    private val cUser = auth.currentUser
    private val databaseReference = FirebaseDatabase.getInstance().getReference("USERS/${auth.uid}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LessonFirebaseTheme {
                val context = LocalContext.current

                if (cUser == null)
                    AuthenticationScreen(auth = auth, context = context)
                else
                    HomeScreen(
                        auth = auth,
                        context = context,
                        cUser = cUser,
                        databaseReference = databaseReference
                    )
            }
        }
    }
}