package com.example.lesson_firebase.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat.startActivity
import coil.compose.rememberImagePainter
import com.example.lesson_firebase.model.UserModel
import com.google.firebase.storage.StorageReference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    userData: MutableState<List<UserModel>>,
    imagesReference: StorageReference?,
    context: Context,
) {
    LazyColumn(content = {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Name")
                    Text(text = "Phone number")
                }
            }
        }
        items(userData.value) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = rememberImagePainter(imagesReference),
                        contentDescription = "Contact icon"
                    )
                    Text(text = it.name)
                    Text(text = it.phoneNumber)
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.clickable {
                            val intent = Intent()
                            intent.action = Intent.ACTION_SEND
                            intent.putExtra(
                                Intent.EXTRA_TEXT,
                                "Contact - ${it.name}, phone number - ${it.phoneNumber}"
                            )
                            intent.type = "text/plain"
                            startActivity(context, Intent.createChooser(intent, "Share To:"), null)
                        })
                }
            }
        }
    })
}
