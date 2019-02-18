package com.android.virgilsecurity.ethreenexmodemo.data.remote.nexmo

import android.content.Context
import com.nexmo.client.NexmoClient
import com.nexmo.client.NexmoConnectionState
import com.nexmo.client.NexmoUser
import com.nexmo.client.request_listener.NexmoApiError
import com.nexmo.client.request_listener.NexmoLoginListener
import com.nexmo.client.request_listener.NexmoRequestListener
import io.reactivex.Completable
import io.reactivex.Single

/**
 * NexmoRx
 */
class NexmoRx(val context: Context) {

    fun initNexmo() = Completable.create { emitter ->
        NexmoClient.init(NexmoClient.NexmoClientConfig(), context, object : NexmoLoginListener {
            override fun onLoginStateChange(
                state: NexmoLoginListener.ELoginState?,
                reason: NexmoLoginListener.ELoginStateReason?
            ) {
                if (state == NexmoLoginListener.ELoginState.LOGGED_IN
                    && reason == NexmoLoginListener.ELoginStateReason.SUCCESS
                ) {
                    emitter.onComplete()
                } else if (reason == NexmoLoginListener.ELoginStateReason.GENERAL_ERROR) {
                    emitter.onError(Throwable("Nexmo init error"))
                }
            }

            override fun onAvailabilityChange(
                availability: NexmoLoginListener.EAvailability?,
                connectionState: NexmoConnectionState?
            ) {
                // Set user's availability state here
            }

        })
    }

    fun loginNexmo(nexmoToken: String) = Single.create<NexmoUser> { emitter ->
        NexmoClient.get().login(nexmoToken, object : NexmoRequestListener<NexmoUser> {
            override fun onSuccess(user: NexmoUser) {
                emitter.onSuccess(user)
            }

            override fun onError(error: NexmoApiError) {
                emitter.onError(Throwable(error.message))
            }
        })
    }
}