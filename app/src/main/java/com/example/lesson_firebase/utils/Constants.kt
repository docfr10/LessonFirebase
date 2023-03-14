package com.example.lesson_firebase.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import com.example.lesson_firebase.model.BottomNavItemModel

object Constants {
    val BottomNavItems = listOf(
        BottomNavItemModel(
            label = "Home",
            icon = Icons.Filled.Home,
            route = "home"
        ),
        BottomNavItemModel(
            label = "Contacts",
            icon = Icons.Default.AccountCircle,
            route = "contacts"
        )
    )
}