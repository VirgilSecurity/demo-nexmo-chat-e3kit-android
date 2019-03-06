package com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.threadsList

import android.content.Context
import com.android.virgilsecurity.ethreenexmodemo.data.local.Preferences
import com.android.virgilsecurity.ethreenexmodemo.data.remote.nexmo.NexmoRx
import com.nexmo.client.NexmoClient
import com.nexmo.client.NexmoConversation
import com.nexmo.client.NexmoMember
import com.nexmo.client.request_listener.NexmoApiError
import com.nexmo.client.request_listener.NexmoRequestListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * ThreadsListPresenter
 */
class ThreadsListPresenter(context: Context) {

    private val compositeDisposable = CompositeDisposable()
    private val nexmoClient: NexmoClient by lazy { NexmoClient.get() }
    private val nexmoRx = NexmoRx(context)
    private val preferences = Preferences.instance(context)

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

    fun initNexmo(onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val initNexmoDisposable = nexmoRx.initNexmo(preferences.nexmoToken()!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { onSuccess() },
                onError = { onError(it) }
            )

        compositeDisposable += initNexmoDisposable
    }

    fun disposeAll() {
        nexmoClient?.removeNewConversationListener()
    }
}