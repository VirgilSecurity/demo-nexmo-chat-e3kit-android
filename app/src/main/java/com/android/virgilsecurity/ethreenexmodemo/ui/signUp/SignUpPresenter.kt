package com.android.virgilsecurity.ethreenexmodemo.ui.signUp

import android.content.Context
import com.android.virgilsecurity.ethreenexmodemo.data.local.Preferences
import com.android.virgilsecurity.ethreenexmodemo.data.remote.auth.AuthRx
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

/**
 * SignUpPresenter
 */
class SignUpPresenter(context: Context) {

    private val compositeDisposable = CompositeDisposable()
    private val authRx = AuthRx
    private val preferences = Preferences.instance(context)

    fun requestAuthenticate(identity: String, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val authenticateDisposable = authRx.authenticate(identity)
            .map {
                preferences.setAuthToken(it)
                preferences.setUsername(identity)
                it
            }
            .subscribeBy(
                onSuccess = { onSuccess() },
                onError = { onError(it) }
            )

        compositeDisposable += authenticateDisposable
    }

    fun disposeAll() = compositeDisposable.clear()
}