package com.android.virgilsecurity.ethreenexmodemo.ui.signUp

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.virgilsecurity.ethreenexmodemo.R
import kotlinx.android.synthetic.main.fragment_sign_in.*

/**
 * SignUpFragment
 */
class SignUpFragment : Fragment() {

    private lateinit var presenter: SignUpPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        presenter = SignUpPresenter(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignUp.setOnClickListener {
            if (etLogin.text.isBlank())
                Toast.makeText(this@SignUpFragment.activity, "Fill in login first", Toast.LENGTH_SHORT).show()
            else
                presenter.requestAuthenticate(etLogin.text.toString(), ::onAuthenticateSuccess, ::onAuthenticateError)
        }
    }

    override fun onDetach() {
        super.onDetach()

        presenter.disposeAll()
    }

    private fun onAuthenticateSuccess() {

    }

    private fun onAuthenticateError(throwable: Throwable) {

    }

    companion object {
        fun instance() = SignUpFragment()
    }
}