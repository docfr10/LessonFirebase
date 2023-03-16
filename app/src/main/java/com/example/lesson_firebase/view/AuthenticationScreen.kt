package com.example.lesson_firebase.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.lesson_firebase.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth


@Composable
fun AuthenticationScreen(
    context: Context,
    auth: FirebaseAuth,
    googleSignInClient: GoogleSignInClient,
    activity: Activity
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(MaterialTheme.colorScheme.background)
            .imePadding(),
        // parameters set to place the items in center
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon Composable
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "authentication",
            tint = MaterialTheme.colorScheme.surfaceTint
        )
        // Text to Display the current Screen
        Text(text = "Authentication", color = MaterialTheme.colorScheme.onSurface)
        // OutlinedTextField to type the Email
        OutlinedTextField(
            value = email.value,
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            onValueChange = { newText -> email.value = newText },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Email address") },
            placeholder = {
                Text(
                    text = "abc@domain.com",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        // OutlinedTextField to type the password
        OutlinedTextField(
            value = password.value,
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            onValueChange = { newText -> password.value = newText },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .imePadding(),
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
        // Registration button
        Button(onClick = {
            // Check the registration
            if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email.value, password.value)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                            Toast.makeText(
                                context,
                                "User successful authorized",
                                Toast.LENGTH_SHORT
                            ).show()
                        else
                            Toast.makeText(context, "User is already exist", Toast.LENGTH_SHORT)
                                .show()
                    }
            } else {
                Toast.makeText(
                    context,
                    "Please enter an email address and a password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, modifier = Modifier.padding(top = 5.dp)) { Text(text = "Registered") }
        // SignIn button
        Button(onClick = {
            // Authorized user login
            if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email.value, password.value)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                            context.startActivity(Intent(context, MainActivity::class.java))
                        else
                            Toast.makeText(
                                context,
                                "Please check that your email address and password are correct",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
            } else {
                Toast.makeText(
                    context, "Please enter an email address and a password", Toast.LENGTH_SHORT
                ).show()
            }
        }) { Text(text = "Sign in") }
        // SignIn button
        Button(onClick = {
            signInWithGoogle(activity = activity, googleSignInClient = googleSignInClient)
        }) {
            Text(text = "Sign in with Google")
        }
    }
}

private fun signInWithGoogle(googleSignInClient: GoogleSignInClient, activity: Activity) {
    val signInIntent = googleSignInClient.signInIntent
    startActivityForResult(activity, signInIntent, 1, null)
}
