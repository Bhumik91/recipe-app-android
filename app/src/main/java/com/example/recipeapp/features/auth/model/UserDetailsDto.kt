package com.example.recipeapp.features.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDetailsDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    @SerialName("username")
    val userName: String,
    @SerialName("image")
    val imageUrl: String
)
