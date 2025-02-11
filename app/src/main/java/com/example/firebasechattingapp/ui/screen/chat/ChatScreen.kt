package com.example.firebasechattingapp.ui.screen.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebasechattingapp.model.Message
import com.example.firebasechattingapp.R
import com.example.firebasechattingapp.reachedToEnd

/**
 * @Created_by: Shishir
 * @Created_on: 09,February,2025
 */

@Composable
fun ChatRoute(
    viewModel: ChatViewModel,
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    ChatScreen(
        myId = viewModel.myId,
        messages = messages,
        isLoading = isLoading,
        onSendButtonTapped = viewModel::sendMessage,
        onNeedToLoadMore = viewModel::loadMoreMessages
    )
}

@Composable
fun ChatScreen(
    myId: Int,
    isLoading: Boolean,
    messages: List<Message>,
    onSendButtonTapped: (String) -> Unit,
    onNeedToLoadMore: () -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
            .imePadding(),
        topBar = {
            Row(
                modifier = Modifier
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(width = 1.dp, color = Color.White, shape = CircleShape),
                    painter = painterResource(R.drawable.avatar),
                    contentDescription = "User Image"
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Mr. Shishir", fontSize = 16.sp)
            }
        },
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                MessageList(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(color = Color.LightGray.copy(alpha = 0.3f))
                        .padding(horizontal = 16.dp),
                    myId = myId,
                    isLoading = isLoading,
                    messages = messages,
                    onNeedToLoadMore = onNeedToLoadMore
                )
                SendMessageBox(onSendBtnTapped = onSendButtonTapped)
            }
        }
    )
}


@Composable
fun MessageList(
    modifier: Modifier,
    myId: Int,
    isLoading: Boolean,
    messages: List<Message>,
    onNeedToLoadMore: () -> Unit,
) {
    val messageListState = rememberLazyListState()

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            messageListState.scrollToItem(0)
        }
    }

    val reachedEnd by remember { derivedStateOf { messageListState.reachedToEnd() } }
    LaunchedEffect(reachedEnd) {
        if (reachedEnd && messages.isNotEmpty()) {
            onNeedToLoadMore.invoke()
        }
    }

    LazyColumn(
        state = messageListState,
        modifier = modifier,
        reverseLayout = true,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(messages, key = { it.time }) { message ->
            if (message.from == myId.toString()) {
                SenderMessage(
                    modifier = Modifier.padding(bottom = 8.dp),
                    message = message.message.orEmpty()
                )
            } else {
                ReceiverMessage(
                    modifier = Modifier.padding(bottom = 8.dp),
                    message = message.message.orEmpty()
                )
            }
        }
        if (isLoading) {
            item { ShowLoader() }
        }
    }
}


@Composable
internal fun ShowLoader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp
        )
    }
}

@Composable
internal fun SenderMessage(
    modifier: Modifier = Modifier,
    message: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End // Pushes content to the right
    ) {
        Text(
            text = message,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier
                .widthIn(max = (LocalConfiguration.current.screenWidthDp * 0.7f).dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Blue.copy(alpha = 0.4f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
internal fun ReceiverMessage(
    modifier: Modifier = Modifier,
    message: String,
) {
    Row {
        Text(
            modifier = modifier
                .wrapContentWidth()
                .widthIn(max = (LocalConfiguration.current.screenWidthDp * 0.7f).dp)
                .padding(vertical = 6.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color.White)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            text = message,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
fun SendMessageBox(
    onSendBtnTapped: (msg: String) -> Unit,
) {
    var messageText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color.LightGray.copy(alpha = 0.4f)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            value = messageText,
            onValueChange = { messageText = it },
            placeholder = {
                Text("Type a message", color = Color.DarkGray)
            },
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            modifier = Modifier
                .clip(CircleShape)
                .background(color = Color.Blue.copy(alpha = 0.5f)),
            onClick = {
                if (messageText.isNotEmpty()) {
                    onSendBtnTapped.invoke(messageText)
                    messageText = ""  // Clear text field
                }
            }
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Filled.Send,
                tint = Color.White,
                contentDescription = "Send"
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
internal fun SendMessageBoxPreview() {
    ChatScreen(
        messages = listOf(
            Message(from = "1", "hi how are you?,"),
            Message(from = "2", "I am fine, what about you?"),
        ),
        myId = 1,
        onSendButtonTapped = {},
        onNeedToLoadMore = {},
        isLoading = false
    )
}