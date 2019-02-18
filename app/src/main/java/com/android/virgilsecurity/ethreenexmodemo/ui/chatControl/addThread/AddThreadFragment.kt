package com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.addThread

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.Toast
import com.android.virgilsecurity.ethreenexmodemo.R
import com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.ChatControlActivity
import com.android.virgilsecurity.ethreenexmodemo.ui.chatControl.threadsList.ThreadsListFragment
import com.android.virgilsecurity.ethreenexmodemo.ui.toolbar.Toolbar
import com.nexmo.client.NexmoConversation
import kotlinx.android.synthetic.main.fragment_add_thread.*
import kotlinx.android.synthetic.main.fragment_thread.*

/**
 * AddThreadFragment
 */
class AddThreadFragment : Fragment() {

    private lateinit var presenter: AddThreadPresenter
    private lateinit var toolbar: Toolbar

    override fun onAttach(context: Context) {
        super.onAttach(context)

        presenter = AddThreadPresenter(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = toolbarThread as Toolbar
        toolbar.setTitle("Add Thread")
        toolbar.showBackButton()
        toolbar.setOnToolbarItemClickListener {
            when (it.id) {
                R.id.ivBack -> {
                    activity!!.onBackPressed()
                }
            }
        }

        btnAddThread.setOnClickListener {
            if (etInterlocutor.text.toString().isNotBlank())
                presenter.requestAddThread(etInterlocutor.text.toString(), ::onAddThreadSuccess, ::onAddThreadError)
        }
    }

    private fun onAddThreadSuccess(nexmoConversation: NexmoConversation) {
        Toast.makeText(activity!!, "Created ${nexmoConversation.displayName}", Toast.LENGTH_SHORT).show()

        (activity!! as ChatControlActivity).changeFragment(ThreadsListFragment.instance())
    }

    private fun onAddThreadError(throwable: Throwable) {
        Toast.makeText(activity!!, throwable.message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun instance() = AddThreadFragment()
    }
}