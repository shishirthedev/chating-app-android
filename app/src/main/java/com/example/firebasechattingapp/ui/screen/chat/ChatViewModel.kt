package com.example.firebasechattingapp.ui.screen.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.firebasechattingapp.ARG_FRIENDS_ID
import com.example.firebasechattingapp.ARG_MY_ID
import com.example.firebasechattingapp.CHAT_APP
import com.example.firebasechattingapp.CHAT_ROOMS
import com.example.firebasechattingapp.model.Message
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * @Created_by: Shishir
 * @Created_on: 09,February,2025
 */

private const val MAX_PER_PAGE = 14
private const val ARG_HAS_NEXT_PAGE = "ARG_HAS_NEXT_PAGE"
private const val ARG_END_KEY = "ARG_END_KEY"

class ChatViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val dbReference = FirebaseDatabase.getInstance().reference.child(CHAT_APP)

    val myId: Int = savedStateHandle.get<Int>(ARG_MY_ID) ?: 0
    private val friendsId: Int = savedStateHandle.get<Int>(ARG_FRIENDS_ID) ?: 0

    private var _messages: MutableStateFlow<List<Message>> = MutableStateFlow(listOf())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private var hasNextPage: Boolean
        get() = savedStateHandle[ARG_HAS_NEXT_PAGE] ?: false
        private set(value) {
            savedStateHandle[ARG_HAS_NEXT_PAGE] = value
        }

    private var endKey: String
        get() = savedStateHandle[ARG_END_KEY] ?: ""
        private set(value) {
            savedStateHandle[ARG_END_KEY] = value
        }

    private var _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var childEventListener: ChildEventListener? = null

    init {
        loadMessages(initialLoad = true, limit = 14)
    }

    fun sendMessage(
        message: String,
    ) {
        val messageId = dbReference.child(CHAT_ROOMS).child(getChatRoomId()).push().key ?: return
        val messageData = Message(from = myId.toString(), message = message)
        getChatRoomReference().child(messageId).setValue(messageData)
    }


    private fun loadMessages(
        initialLoad: Boolean,
        limit: Int,
    ) {
        if (_isLoading.value) return
        _isLoading.value = true
        val query = if (initialLoad) {
            getChatRoomReference().orderByKey().limitToLast(limit)
        } else if (hasNextPage) {
            getChatRoomReference().orderByKey().endBefore(endKey).limitToLast(limit)
        } else {
            return
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _isLoading.value = false
                processMessages(initialLoad = initialLoad, snapshot = snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
            }
        })
    }

    private fun processMessages(
        initialLoad: Boolean,
        snapshot: DataSnapshot,
    ) {
        val messagesList = snapshot.children.mapNotNull { it.getValue(Message::class.java) }

        if (messagesList.size < MAX_PER_PAGE) {
            hasNextPage = false
            endKey = ""
        } else {
            hasNextPage = true
            endKey = snapshot.children.firstOrNull()?.key.orEmpty()
        }

        if (initialLoad) {
            _messages.value = messagesList.reversed()
            startListeningForNewMessages(
                lastMessageKey = snapshot.children.lastOrNull()?.key.orEmpty()
            )
        } else {
            _messages.update { it + messagesList.reversed() }
        }
    }


    private fun startListeningForNewMessages(lastMessageKey: String) {
        /**
         * onChildAdded:
         * is triggered once for each existing child and then again every time a new child is added to the specified path.
         * If you want to only be informed of new children,you can use the keys that generates to only get child nodes that are generated after you start listening with:
         *
         * String key = myRef.push().getKey()
         * myRef.orderByKey().startAfter(key).addChildEventListener(childEventListener);
         * */
        childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                (snapshot.getValue(Message::class.java))?.let { newMessage ->
                    _messages.update { listOf(newMessage) + it }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }
        getChatRoomReference()
            .orderByKey()
            .startAfter(lastMessageKey)
            .addChildEventListener(childEventListener!!)
    }

    fun loadMoreMessages() {
        if (hasNextPage && !isLoading.value) {
            loadMessages(initialLoad = false, limit = MAX_PER_PAGE)
        }
    }

    private fun getChatRoomReference() = dbReference.child(CHAT_ROOMS).child(getChatRoomId())
    private fun getChatRoomId(): String {
        return if (myId < friendsId) "${myId}_$friendsId" else "${friendsId}_$myId"
    }

    override fun onCleared() {
        childEventListener?.let {
            getChatRoomReference().removeEventListener(it)
        }
    }
}