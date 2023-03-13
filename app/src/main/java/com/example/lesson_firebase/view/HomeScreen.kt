package com.example.lesson_firebase.view

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.lesson_firebase.MainActivity
import com.example.lesson_firebase.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

@Composable
fun HomeScreen(
    auth: FirebaseAuth,
    context: Context,
    cUser: FirebaseUser,
    databaseReference: DatabaseReference
) {
    val name = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(text = "You are logged in as: ${cUser.email}")
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Log out",
                modifier = Modifier.clickable {
                    auth.signOut()
                    context.startActivity(Intent(context, MainActivity::class.java))
                })
        }
        OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text(text = "Type a name of contact") }
        )
        OutlinedTextField(
            value = phoneNumber.value,
            onValueChange = { phoneNumber.value = it },
            label = { Text(text = "Type a phone number of contact") }
        )
        Button(onClick = {
            databaseReference.push().setValue(
                User(
                    id = cUser.uid,
                    name = name.value,
                    phoneNumber = phoneNumber.value
                )
            )
        }) { Text(text = "Save") }
    }
}