package com.example.lesson_firebase

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

class MainActivity : ComponentActivity() {
    private var auth = FirebaseAuth.getInstance()
    private val cUser = auth.currentUser
    private val databaseReference = FirebaseDatabase.getInstance().getReference("USERS/${auth.uid}")
    private val userData = mutableStateOf<List<UserModel>>(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LessonFirebaseTheme {
                val context = LocalContext.current
                val navController = rememberNavController()

                if (cUser == null)
                    AuthenticationScreen(auth = auth, context = context)
                else
                    AppScreen(
                        auth = auth,
                        context = context,
                        cUser = cUser,
                        databaseReference = databaseReference,
                        navController = navController,
                        userData = userData
                    )
            }
        }
    }
}