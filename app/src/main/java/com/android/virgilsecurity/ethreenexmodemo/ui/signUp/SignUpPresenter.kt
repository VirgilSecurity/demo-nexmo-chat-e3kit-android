package com.android.virgilsecurity.ethreenexmodemo.ui.signUp

import android.content.Context
import com.android.virgilsecurity.ethreenexmodemo.data.local.Preferences
import com.android.virgilsecurity.ethreenexmodemo.data.remote.auth.AuthRx
import com.android.virgilsecurity.ethreenexmodemo.data.remote.nexmo.NexmoRx
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * SignUpPresenter
 */
class SignUpPresenter(context: Context) {

    private val compositeDisposable = CompositeDisposable()
    private val authRx = AuthRx
    private val nexmoRx = NexmoRx(context)
    private val preferences = Preferences.instance(context)

    fun requestAuthenticate(identity: String, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val authenticateDisposable = authRx.authenticate(identity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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

    fun requestTokens(onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val getTokensDisposable = authRx.nexmoJwt(preferences.authToken()!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                preferences.setNexmoToken(it)
            }
            .flatMap {
                authRx.virgilJwt(preferences.authToken()!!)
            }.map {
                preferences.setVirgilToken(it)
            }.subscribeBy(
                onSuccess = { onSuccess() },
                onError = { onError(it) }
            )

        compositeDisposable += getTokensDisposable
    }

    fun initNexmo(onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val initNexmoDisposable = nexmoRx.initNexmo()
            .subscribeOn(Schedulers.io())
            .doOnComplete {
                nexmoRx.loginNexmo(preferences.nexmoToken()!!)
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { onSuccess() },
                onError = { onError(it) }
            )

        compositeDisposable += initNexmoDisposable
    }

    fun disposeAll() = compositeDisposable.clear()
}