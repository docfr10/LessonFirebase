package com.example.lesson_firebase.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.lesson_firebase.MainActivity
import com.example.lesson_firebase.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

@Composable
fun HomeScreen(
    auth: FirebaseAuth,
    context: Context,
    cUser: FirebaseUser,
    databaseReference: DatabaseReference,
    activity: Activity,
    firebaseRemoteConfig: FirebaseRemoteConfig,
) {
    val focusManager = LocalFocusManager.current

    val name = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }

    val logged = firebaseRemoteConfig.getString("You_are_logged_as")

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
            Text(text = "Youare logged in as: ${cUser.email}")
            //Text(text = "$logged ${cUser.email}")
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
            label = { Text(text = "Type a name of contact") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            value = phoneNumber.value,
            onValueChange = { phoneNumber.value = it },
            label = { Text(text = "Type a phone number of contact") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )
        Button(onClick = {
            setImage(activity = activity)
            databaseReference.push().setValue(
                UserModel(
                    id = databaseReference.push().key,
                    name = name.value,
                    phoneNumber = phoneNumber.value
                )
            )
        }) { Text(text = "Save") }
    }
}

private fun setImage(activity: Activity) {
    val intent = Intent().setType("image/*").setAction(Intent.ACTION_PICK)
    activity.startActivityForResult(Intent.createChooser(intent, "Select image"), 100)
}


