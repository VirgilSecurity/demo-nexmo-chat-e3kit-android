package com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.threadsList

import com.nexmo.client.NexmoClient
import com.nexmo.client.NexmoConversation
import com.nexmo.client.NexmoMember
import com.nexmo.client.NexmoNewConversationListener
import com.nexmo.client.request_listener.NexmoApiError
import com.nexmo.client.request_listener.NexmoRequestListener

/**
 * ThreadsListPresenter
 */
class ThreadsListPresenter {

    private val nexmoClient = NexmoClient.get()

    fun requestThreads(onSuccess: (Collection<NexmoConversation>) -> (Unit), onError: (Throwable) -> Unit) {
        nexmoClient.getConversations(object : NexmoRequestListener<Collection<NexmoConversation>> {
            override fun onSuccess(conversations: Collection<NexmoConversation>) {
                onSuccess(conversations)
            }

            override fun onError(error: NexmoApiError) {
                onError(Throwable(error.message))
            }
        })
    }

    fun listenNewThreads(threadAdded: (NexmoConversation) -> Unit) {
        nexmoClient.addNewConversationListener {
            threadAdded(it)
        }
    }

    fun listenInvites(onSuccess: (NexmoMember) -> Unit, onError: (Throwable) -> Unit) {
        nexmoClient.addNewConversationListener {
            it.join(object : NexmoRequestListener<NexmoMember> {
                override fun onSuccess(member: NexmoMember) {
                    onSuccess(member)
                }

                override fun onError(error: NexmoApiError) {
                    onError(Throwable(error.message))
                }

            })
        }
    }

    fun disposeAll() {
        nexmoClient.removeNewConversationListener()
    }
}