package com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.thread

import android.content.Context
import com.android.virgilsecurity.ethreenexmodemo.data.local.Preferences
import com.android.virgilsecurity.ethreenexmodemo.data.model.chat.NexmoMessage
import com.nexmo.client.*
import com.nexmo.client.request_listener.NexmoApiError
import com.nexmo.client.request_listener.NexmoRequestListener

/**
 * ThreadPresenter
 */
class ThreadPresenter(context: Context) {

    private val preferences = Preferences.instance(context)

    fun requestMessages(thread: NexmoConversation, onNewMessage: (NexmoMessage) -> Unit) {
        thread.addMessageEventListener(object : NexmoMessageEventListener {
            override fun onTypingEvent(p0: NexmoTypingEvent) {
                // TODO Implement body or it will be empty ):
            }

            override fun onAttachmentEvent(p0: NexmoAttachmentEvent) {
                // TODO Implement body or it will be empty ):
            }

            override fun onTextEvent(textEvent: NexmoTextEvent) {
                if (textEvent.member.user.name != preferences.username()) {
                    val sender = thread.allMembers.first { it.user.name != preferences.username() }
                        .user.name // TODO get right name
                    onNewMessage(NexmoMessage(textEvent.text, sender))
                }
            }

            override fun onSeenReceipt(p0: NexmoSeenEvent) {
                // TODO Implement body or it will be empty ):
            }

            override fun onEventDeleted(p0: NexmoDeletedEvent) {
                // TODO Implement body or it will be empty ):
            }

            override fun onDeliveredReceipt(p0: NexmoDeliveredEvent) {
                // TODO Implement body or it will be empty ):
            }

        })
    }

    fun requestSendMessage(
        thread: NexmoConversation,
        text: String,
        onSuccess: (NexmoMessage) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        thread.sendText(text, object : NexmoRequestListener<Void> {
            override fun onSuccess(p0: Void?) {
                onSuccess(NexmoMessage(text, preferences.username()!!))
            }

            override fun onError(error: NexmoApiError) {
                onError(Throwable(error.message))
            }

        })
    }
}