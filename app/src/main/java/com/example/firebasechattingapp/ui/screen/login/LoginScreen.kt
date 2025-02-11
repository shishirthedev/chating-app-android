package com.example.firebasechattingapp.ui.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @Created_by: Shishir
 * @Created_on: 09,February,2025
 */


@Composable
internal fun LoginRoute(
    viewModel: LoginViewModel,
    navigateToChatScreen: (Int, Int) -> Unit,
) {

    LoginScreen(
        onNextBtnClick = navigateToChatScreen
    )
}

@Composable
internal fun LoginScreen(
    onNextBtnClick: (Int, Int) -> Unit,
) {
    var userId by rememberSaveable { mutableStateOf("") }
    var receiverId by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = userId,
            onValueChange = { newValue ->
                userId = newValue
            },
            label = {
                Text("My ID", color = Color.LightGray, fontSize = 16.sp)
            },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color.DarkGray
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = receiverId,
            onValueChange = { newValue ->
                receiverId = newValue
            },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            label = {
                Text("Friend's ID", color = Color.LightGray, fontSize = 16.sp)
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color.DarkGray
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            onNextBtnClick.invoke(
                userId.toIntOrNull() ?: 0, receiverId.toIntOrNull() ?: 0
            )
        }) {
            Text(text = "Next")
        }
    }
}