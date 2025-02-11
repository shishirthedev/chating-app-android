package com.example.firebasechattingapp.model

import androidx.compose.runtime.Immutable

/**
 * @Created_by: Shishir
 * @Created_on: 09,February,2025
 */


@Immutable
data class Message(
    val from: String? = null,
    val message: String? = null,
    val isSeen: Boolean = false,
    val time: Long = System.currentTimeMillis(),
)