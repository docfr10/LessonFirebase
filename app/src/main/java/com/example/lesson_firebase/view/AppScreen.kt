package com.example.lesson_firebase.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.lesson_firebase.utils.Constants
import com.example.lesson_firebase.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.StorageReference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    auth: FirebaseAuth,
    context: Context,
    cUser: FirebaseUser,
    databaseReference: DatabaseReference,
    navController: NavHostController,
    userData: MutableState<List<UserModel>>,
    activity: Activity,
    imagesReference: StorageReference?,
    firebaseRemoteConfig: FirebaseRemoteConfig,
    startForResultImage: ActivityResultLauncher<Intent>,
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = { padding ->
            NavHostContainer(
                activity = activity,
                auth = auth,
                context = context,
                cUser = cUser,
                databaseReference = databaseReference,
                imagesReference = imagesReference,
                firebaseRemoteConfig = firebaseRemoteConfig,
                navController = navController,
                startForResultImage = startForResultImage,
                padding = padding,
                userData = userData
            )
        })
}

@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        // Observe the backstack
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        // Observe current route to change the icon
        // Color,label color when navigated
        val currentRoute = navBackStackEntry?.destination?.route
        // Bottom nav items we declared
        Constants.BottomNavItems.forEach { navItem ->
            // Place the bottom nav items
            NavigationBarItem(
                // It currentRoute is equal then its selected route
                selected = currentRoute == navItem.route,
                // Navigate on click
                onClick = { navController.navigate(navItem.route) },
                // Icon of navItem
                icon = { Icon(imageVector = navItem.icon, contentDescription = navItem.label) },
                // Label
                label = { Text(text = navItem.label) },
                alwaysShowLabel = true
            )
        }
    }
}

@Composable
private fun NavHostContainer(
    auth: FirebaseAuth,
    context: Context,
    cUser: FirebaseUser,
    databaseReference: DatabaseReference,
    navController: NavHostController,
    padding: PaddingValues,
    userData: MutableState<List<UserModel>>,
    activity: Activity,
    imagesReference: StorageReference?,
    firebaseRemoteConfig: FirebaseRemoteConfig,
    startForResultImage: ActivityResultLauncher<Intent>,
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(padding),
        builder = {
            composable(route = "home") {
                HomeScreen(
                    activity = activity,
                    auth = auth,
                    context = context,
                    cUser = cUser,
                    databaseReference = databaseReference,
                    firebaseRemoteConfig = firebaseRemoteConfig,
                    startForResultImage = startForResultImage,
                )
            }
            composable(route = "contacts") {
                getUserData(
                    databaseReference = databaseReference,

                    userData = userData
                )
                ContactsScreen(
                    context = context,
                    imagesReference = imagesReference,
                    userData = userData
                )
            }
        })
}

private fun getUserData(
    databaseReference: DatabaseReference,
    userData: MutableState<List<UserModel>>,
) {
    databaseReference.addValueEventListener(
        object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList = mutableListOf<UserModel>()
                for (ds in dataSnapshot.children) {
                    val userMap = ds.value as HashMap<*, *>
                    val userModel = UserModel(
                        userMap["id"] as String,
                        userMap["name"] as String,
                        userMap["phoneNumber"] as String
                    )
                    userList.add(userModel)
                }
                userData.value = userList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        }
    )
}
